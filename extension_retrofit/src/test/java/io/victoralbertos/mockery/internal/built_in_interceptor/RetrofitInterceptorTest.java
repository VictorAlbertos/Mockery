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

import io.victoralbertos.jolyglot.Types;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_interceptor.Retrofit;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.mock.Calls;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class RetrofitInterceptorTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private RetrofitInterceptor retrofitInterceptor;

  @Before public void init() {
    retrofitInterceptor = new RetrofitInterceptor();
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Not_Call_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("mock");
    Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Retrofit.class);
    Metadata<Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    retrofitInterceptor.onLegalMock(new Mock(), metadata);
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Not_Call_Parameterized_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("callNotParameterized");
    Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Retrofit.class);
    Metadata<Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    retrofitInterceptor.onLegalMock(new Mock(), metadata);
  }

  @Test public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Not_Call_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("callNotParameterized");
    Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Retrofit.class);
    Metadata<Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    retrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
  }

  @Test public void When_Mock_Seed_Is_Type_Call_Then_Get_Exception() {
    exception.expect(RuntimeException.class);
    retrofitInterceptor.onLegalMock(Calls.response(new Mock()), metadataRetrofit());
  }

  @Test public void When_Call_OnLegalMock_Sync_If_Method_Return_Type_Is_Call_Then_Get_Call()
      throws Exception {
    Mock mock = new Mock();
    Call<Mock> call = retrofitInterceptor.onLegalMock(mock, metadataRetrofit());
    Response<Mock> response = call.execute();
    assertTrue(response.isSuccessful());
    assertThat(response.body(), is(mock));
    assertNull(response.errorBody());
  }

  @Test public void When_Call_OnLegalMock_Async_If_Method_Return_Type_Is_Call_Then_Get_Call()
      throws Exception {
    final AtomicReference<Response<Mock>> responseMock = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);
    Mock mock = new Mock();

    Call<Mock> call = retrofitInterceptor.onLegalMock(mock, metadataRetrofit());
    call.enqueue(new Callback<Mock>() {
      @Override public void onResponse(Call<Mock> call,
          Response<Mock> response) {
        responseMock.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Mock> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    Response<Mock> response = responseMock.get();
    assertTrue(response.isSuccessful());
    assertThat(response.body(), is(mock));
    assertNull(response.errorBody());
  }

  @Test public void When_Call_OnIllegalMock_Sync_If_Method_Return_Type_Is_Call_Then_Get_Response_Body_Null()
      throws NoSuchMethodException, IOException {
    Call<Mock> call = retrofitInterceptor
        .onIllegalMock(new AssertionError("BOOM!"), metadataRetrofit());
    Response<Mock> response = call.execute();
    assertFalse(response.isSuccessful());
    assertNull(response.body());
    assertThat(response.errorBody().string(), is("BOOM!"));
  }

  @Test public void When_Call_OnIllegalMock_Async_If_Method_Return_Type_Is_Call_Then_Get_Response_Body_Null()
      throws Exception {
    final AtomicReference<Response<Mock>> responseMock = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    Call<Mock> call = retrofitInterceptor
        .onIllegalMock(new AssertionError("BOOM!"), metadataRetrofit());
    call.enqueue(new Callback<Mock>() {
      @Override public void onResponse(Call<Mock> call,
          Response<Mock> response) {
        responseMock.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Mock> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    Response<Mock> response = responseMock.get();
    assertFalse(response.isSuccessful());
    assertNull(response.body());
    assertThat(response.errorBody().string(), is("BOOM!"));
  }

  @Test public void When_Call_Validate_With_Call_Then_Do_Not_Throw_Assertion() {
    retrofitInterceptor.validate(Calls.response(new Mock()), metadataRetrofit());
  }

  @Test public void When_Call_Validate_With_Call_Error_Then_Throw_Assertion() {
    final ResponseBody responseBody = ResponseBody
        .create(MediaType.parse("application/json"), "BOOM!");

    exception.expect(AssertionError.class);

    retrofitInterceptor.validate(
        Calls.response(Response.error(404, responseBody)),
        metadataRetrofit());
  }

  @Test public void When_Call_Adapt_Response_With_Call_Then_Unwrap_Its_Value() {
    Mock mock = (Mock) retrofitInterceptor
        .adaptResponse(Calls.response(new Mock()), metadataRetrofit());
    assertNotNull(mock);
  }

  @Test public void When_Call_Adapt_Type_With_Call_ListParameterized_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("callWithListParameterized");
    Type methodType = method.getGenericReturnType();
    Type expectedType = Types.newParameterizedType(List.class, Mock.class);

    Type adaptedType = retrofitInterceptor
        .adaptType(methodType, metadataRetrofit());

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Call_Type_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("call");
    Type methodType = method.getGenericReturnType();
    Type expectedType = Mock.class;

    Type adaptedType = retrofitInterceptor
        .adaptType(methodType, metadataRetrofit());

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_OnLegalMock_Sync_With_Delay_Then_Get_Call_Delayed()
      throws Exception {
    Call<Mock> call = retrofitInterceptor.onLegalMock(new Mock(),
        metadataRetrofitDelayed());

    long startNanos = System.nanoTime();
    call.execute();

    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    assert tookMs >= 100;
  }

  @Test public void When_Call_OnLegalMock_Async_With_Delay_Then_Get_Call_Delayed()
      throws Exception {
    final AtomicReference<Response<Mock>> responseMock = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);
    long startNanos = System.nanoTime();

    Call<Mock> call = retrofitInterceptor
        .onLegalMock(new Mock(),  metadataRetrofitDelayed());
    call.enqueue(new Callback<Mock>() {
      @Override public void onResponse(Call<Mock> call,
          Response<Mock> response) {
        responseMock.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Mock> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    assert tookMs >= 100;
  }

  @Test public void When_Call_OnIllegalMock_Sync_With_Delay_Then_Get_Call_Delayed()
      throws Exception {
    Call<Mock> call = retrofitInterceptor.onIllegalMock(new AssertionError(),
        metadataRetrofitDelayed());

    long startNanos = System.nanoTime();
    call.execute();

    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    assert tookMs >= 100;
  }

  @Test public void When_Call_OnIllegalMock_Async_With_Delay_Then_Get_Call_Delayed()
      throws Exception {
    final AtomicReference<Response<Mock>> responseMock = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);
    long startNanos = System.nanoTime();

    Call<Mock> call = retrofitInterceptor
        .onIllegalMock(new AssertionError(),  metadataRetrofitDelayed());
    call.enqueue(new Callback<Mock>() {
      @Override public void onResponse(Call<Mock> call,
          Response<Mock> response) {
        responseMock.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Mock> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    assert tookMs >= 100;
  }
  private Metadata<Retrofit> metadataRetrofit() {
    try {
      Method method = Providers.class.getDeclaredMethod("call");
      Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Retrofit.class);
      Metadata<Retrofit> metadata = new Metadata(Providers.class,
          method, null, annotation, method.getGenericReturnType());
      return metadata;
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private Metadata<Retrofit> metadataRetrofitDelayed() {
    try {
      Method method = Providers.class.getDeclaredMethod("call");
      Retrofit annotation = PlaceholderRetrofit100DelayAnnotation.class.getAnnotation(Retrofit.class);
      Metadata<Retrofit> metadata = new Metadata(Providers.class,
          method, null, annotation, method.getGenericReturnType());
      return metadata;
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private static class Mock {}

  private interface Providers {
    Mock mock();
    Call<Mock> call();
    Call callNotParameterized();

    Call<List<Mock>> callWithListParameterized();
  }

  @Retrofit(delay = 0, failurePercent = 0)
  private interface PlaceholderRetrofitAnnotation {}

  @Retrofit(delay = 100, failurePercent = 0, variancePercentage = 0)
  private interface PlaceholderRetrofit100DelayAnnotation {}
}
