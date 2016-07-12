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

package io.victoralbertos.example_rx_retrofit.data;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.victoralbertos.example_rx_retrofit.BuildConfig;
import io.victoralbertos.example_rx_retrofit.domain.Repo;
import io.victoralbertos.example_rx_retrofit.domain.User;
import io.victoralbertos.mockery.api.Mockery;
import java.io.IOException;
import java.util.List;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

public enum Repository {
  POOL;

  private RestApi restApi;
  private boolean mock = true;

  Repository() {
    initRestApi();
  }

  public void useMock() {
    this.mock = true;
    initRestApi();
  }

  public void useReal() {
    this.mock = false;
    initRestApi();
  }

  private void initRestApi() {
    if(!BuildConfig.DEBUG) {
      restApi = real();
      return;
    }

    restApi = mock ? mock() : real();
  }

  private RestApi mock() {
    return new Mockery.Builder<RestApi>()
        .mock(RestApi.class)
        .build();
  }

  private RestApi real() {
    return new Retrofit.Builder()
        .baseUrl(RestApi.URL_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build().create(RestApi.class);
  }

  public Observable<User> getUserByName(String username) {
    return handleResponse(restApi.getUserByName(username));
  }

  public Observable<List<User>> getUsers(int lastIdQueried, int perPage) {
    return restApi.getUsers(lastIdQueried, perPage);
  }

  public Observable<List<Repo>> getRepos(String username, String type, String direction) {
    return handleResponse(restApi.getRepos(username, type, direction));
  }

  private <T> Observable<T> handleResponse(Observable<Response<T>> response) {
    return response.flatMap(new Func1<Response<T>, Observable<T>>() {
      @Override public Observable<T> call(Response<T> response) {
        if (response.isSuccessful()) {
          return Observable.just(response.body());
        }

        try {
          ResponseError responseError = new Gson()
              .fromJson(response.errorBody().string(), ResponseError.class);
          return Observable.error(new RuntimeException(responseError.getMessage()));
        } catch (JsonParseException |IOException exception) {
          return Observable.error(new RuntimeException(exception));
        }
      }
    });
  }

  private static class ResponseError {
    private final String message;

    public ResponseError(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }

}
