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

package io.victoralbertos.mockery.internal.integration;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.victoralbertos.jolyglot.GsonSpeaker;
import io.victoralbertos.mockery.api.JsonConverter;
import io.victoralbertos.mockery.api.built_in_interceptor.Rx2Retrofit;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.NoDTO;
import io.victoralbertos.mockery.internal.integration.Mocks.MockModel;
import retrofit2.Response;

@Rx2Retrofit(delay = 0, failurePercent = 100, variancePercentage = 0)
@JsonConverter(GsonSpeaker.class)
interface Rx2RestApiFailure {
  @DTO(MockModel.class)
  Single<Model> model();

  @NoDTO
  Completable completableModel();

  @DTO(MockModel.class)
  Single<Response<Model>> modelResponse();
}
