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

package io.victoralbertos.example_retrofit.data;

import io.victoralbertos.example_retrofit.BuildConfig;
import io.victoralbertos.example_retrofit.domain.Repo;
import io.victoralbertos.example_retrofit.domain.User;
import io.victoralbertos.mockery.api.Mockery;
import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    .build().create(RestApi.class);
  }

  public Call<User> getUserByName(String username) {
    return restApi.getUserByName(username);
  }

  public Call<List<User>> getUsers(int lastIdQueried, int perPage) {
    return restApi.getUsers(lastIdQueried, perPage);
  }

  public Call<List<Repo>> getRepos(String username, String type, String direction) {
    return restApi.getRepos(username, type, direction);
  }

}
