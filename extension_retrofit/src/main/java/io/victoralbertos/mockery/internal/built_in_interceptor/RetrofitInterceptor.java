/*
 * Copyright 2016 Victor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.victoralbertos.mockery.internal.built_in_interceptor;

import io.victoralbertos.mockery.api.Interceptor;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_interceptor.ErrorResponseAdapter;
import io.victoralbertos.mockery.api.built_in_interceptor.Retrofit;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;
import retrofit2.mock.NetworkBehavior;

public class RetrofitInterceptor implements Interceptor.Behaviour<Retrofit> {
  private final CallAdapter callAdapter;

  public RetrofitInterceptor() {
    this.callAdapter = new CallAdapter(new retrofit2.Retrofit.Builder()
        .baseUrl("http://mockery.com")
        .build());
  }

  @Override public Call onLegalMock(Object mock, Metadata<Retrofit> metadata) {
    checkReturnMethodTypeIsCall(metadata);
    checkTypeMockIsNotCall(metadata, mock);

    NetworkBehavior networkBehavior = networkBehaviour(metadata);
    return callAdapter.adapt(metadata.getMethod(),
        networkBehavior, Calls.response(mock));
  }

  @Override public Call onIllegalMock(AssertionError assertionError, Metadata<Retrofit> metadata) {
    checkReturnMethodTypeIsCall(metadata);

    String errorMessage = assertionError.getMessage() != null ? assertionError.getMessage() : "";
    String safeErrorMessage = adaptErrorResponse(errorMessage, metadata);
    ResponseBody responseBody = ResponseBody
        .create(MediaType.parse("application/json"), safeErrorMessage);

    NetworkBehavior networkBehavior = networkBehaviour(metadata);
    return callAdapter.adapt(metadata.getMethod(),
        networkBehavior, Calls.response(Response.error(404, responseBody)));
  }

  @Override public void validate(Object candidate, Metadata<Retrofit> metadata) throws AssertionError {
    checkReturnMethodTypeIsCall(metadata);
    Call call = (Call) candidate;
    try {
      Response response = call.execute();
      if (!response.isSuccessful()) {
        throw new AssertionError("Response must be successful");
      } else if (response.body() == null) {
        throw new AssertionError("Body must be not null");
      } else if (response.errorBody() != null) {
        throw new AssertionError("Error body must be null");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override public Object adaptResponse(Object response, Metadata<Retrofit> metadata) {
    checkReturnMethodTypeIsCall(metadata);
    Call call = (Call) response;
    try {
      Response payload = call.clone().execute();
      Object body = payload.body();
      return body;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override public Type adaptType(Type responseType, Metadata<Retrofit> metadata) {
    checkReturnMethodTypeIsCall(metadata);

    ParameterizedType callType = (ParameterizedType) responseType;
    Type[] types = callType.getActualTypeArguments();
    return types[0];
  }

  private void checkReturnMethodTypeIsCall(Metadata<Retrofit> metadata) {
    Type returnMethodType = metadata.getType();

    if (returnMethodType instanceof ParameterizedType) {
      Type type = ((ParameterizedType)returnMethodType).getRawType();
      if (type == Call.class) return;
    }

    String message = Messages.illegalMethodReturnType(metadata.getMockingClass(),
        metadata.getMethod(), returnMethodType);
    throw new RuntimeException(message);
  }

  private void checkTypeMockIsNotCall(Metadata<Retrofit> metadata, Object mock) {
    if (mock instanceof Call) {
      String message = Messages.illegalMockType(metadata.getMockingClass(),
          metadata.getMethod());
      throw new RuntimeException(message);
    }
  }

  private NetworkBehavior networkBehaviour(Metadata<Retrofit> metadata) {
    Retrofit retrofit = metadata.getAnnotation();

    NetworkBehavior networkBehavior = NetworkBehavior.create();
    networkBehavior.setDelay(retrofit.delay(), TimeUnit.MILLISECONDS);
    networkBehavior.setVariancePercent(retrofit.variancePercentage());
    networkBehavior.setFailurePercent(retrofit.failurePercent());

    return networkBehavior;
  }

  private String adaptErrorResponse(String error, Metadata<Retrofit> metadata) {
    Retrofit retrofit = metadata.getAnnotation();

    try {
      Constructor<? extends ErrorResponseAdapter> constructor = retrofit.errorResponseAdapter()
          .getDeclaredConstructor();
      constructor.setAccessible(true);
      ErrorResponseAdapter errorResponseAdapter = constructor.newInstance();
      return errorResponseAdapter.adapt(error);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
