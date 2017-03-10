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

package io.victoralbertos.example_rx_retrofit2.data;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.victoralbertos.example_rx_retrofit2.data.Mockeries.ReposDTO;
import io.victoralbertos.example_rx_retrofit2.data.Mockeries.UserDTO;
import io.victoralbertos.example_rx_retrofit2.data.Mockeries.UsersDTO;
import io.victoralbertos.example_rx_retrofit2.domain.Repo;
import io.victoralbertos.example_rx_retrofit2.domain.User;
import io.victoralbertos.mockery.api.built_in_interceptor.ErrorResponseAdapter;
import io.victoralbertos.mockery.api.built_in_interceptor.Rx2Retrofit;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import io.victoralbertos.mockery.api.built_in_mockery.NoDTO;
import io.victoralbertos.mockery.api.built_in_mockery.Enum;
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.util.List;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;

@Rx2Retrofit(errorResponseAdapter = RestApi.GithubResponseAdapter.class)
public interface RestApi {
  String URL_BASE = "https://api.github.com";
  String HEADER_API_VERSION = "Accept: application/vnd.github.v3+json";

  @Headers(HEADER_API_VERSION)
  @PUT("/users/{username}")
  @NoDTO Completable putSomething(@Valid(value = STRING) @Path("username") String something);

  @Headers(HEADER_API_VERSION)
  @GET("/users/{username}")
  @DTOArgs(UserDTO.class) Single<Response<User>> getUserByName(
      @Valid(value = STRING, legal = "google") @Path("username") String username);

  @Headers(HEADER_API_VERSION)
  @GET("/users")
  @DTOArgs(UsersDTO.class)
  Single<List<User>> getUsers(@Optional @Query("since") int lastIdQueried,
      @Optional @Query("per_page") int perPage);

  @Headers(HEADER_API_VERSION)
  @GET("/users/{username}/repos")
  @DTO(ReposDTO.class)
  Single<Response<List<Repo>>> getRepos(
      @Valid(value = STRING, legal = "google") @Path("username") String username,
      @Enum(value = {"all", "owner", "member"}, legal = "owner") @Query("type") String type,
      @Enum({"asc", "desc"}) @Query("direction") String direction);

  class GithubResponseAdapter implements ErrorResponseAdapter {
    @Override public String adapt(String error) {
      String json = "{'message':'%s'}";
      return String.format(json, error);
    }
  }

}
