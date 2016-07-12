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

import io.victoralbertos.jolyglot.JacksonSpeaker;
import io.victoralbertos.mockery.api.JsonConverter;
import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTOJson;
import io.victoralbertos.mockery.api.built_in_mockery.Enum;
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import io.victoralbertos.mockery.internal.integration.Mocks.MockModel;
import java.util.List;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.EMAIL;
import static io.victoralbertos.mockery.internal.integration.Mocks.MockModels;

@Bypass
@JsonConverter(JacksonSpeaker.class)
interface RestApi {
  Model modelWithoutDTO();

  @DTO(MockModels.class)
  Model modelWithWrongDTO();

  @DTO(MockModel.class)
  Model model();

  @DTO(MockModel.class)
  Model modelWithParams(@DTO(MockModel.class) Model model,
      @Optional int optional,
      @DTOJson(MockModel.class) String modelJSON);

  @DTO(MockModel.class)
  Model modelWithWrongMockeryParam(@DTO(MockModels.class) Model model);

  @DTO(MockModels.class)
  List<Model> models();

  @DTO(MockModels.class)
  List<Model> modelsWithParams(@Valid(EMAIL) String email,
      @Optional String optional);

  @DTO(MockModels.class)
  List<Model> modelsWithParamWithoutMockery(Integer id);

  @DTO(MockModel.class)
  Model modelWithEnum(@Enum(value = {"asc", "desc"}, legal = "asc") String s1);
}
