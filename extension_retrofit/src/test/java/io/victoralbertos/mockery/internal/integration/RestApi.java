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

import io.victoralbertos.jolyglot.GsonSpeaker;
import io.victoralbertos.mockery.api.JsonConverter;
import io.victoralbertos.mockery.api.built_in_interceptor.Retrofit;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTOJson;
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import io.victoralbertos.mockery.api.built_in_mockery.RequestBodyDTO;
import io.victoralbertos.mockery.api.built_in_mockery.RequestBodyValid;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import io.victoralbertos.mockery.internal.integration.Mocks.MockModel;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.EMAIL;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.INT;
import static io.victoralbertos.mockery.internal.integration.Mocks.MockModels;

@Retrofit(delay = 0, failurePercent = 0, variancePercentage = 0)
@JsonConverter(GsonSpeaker.class)
interface RestApi {
  @DTO(MockModel.class)
  Model modelWithoutCall();

  Call<Model> modelWithoutDTO();

  @DTO(MockModels.class)
  Call<Model> modelWithWrongDTO();

  @DTO(MockModel.class)
  Call<Model> model();

  @DTO(MockModel.class)
  Call<Model> modelWithParams(@RequestBodyDTO(MockModel.class) RequestBody modelJson,
      @Optional int optional);

  @DTO(MockModel.class)
  Call<Model> modelWithWrongMockeryParam(@DTO(MockModels.class) Model model);

  @DTO(MockModels.class)
  Call<List<Model>> models(@DTOJson(MockModels.class) String modelsJSON);

  @DTO(MockModels.class)
  Call<List<Model>> modelsWithParams(@RequestBodyValid(EMAIL) RequestBody email,
      @Optional String optional);

  @DTO(MockModels.class)
  Call<List<Model>> modelsWithParamWithoutMockery(Integer i1);

  @Valid(value = INT, legal = "30")
  Call<Integer> integer();
}
