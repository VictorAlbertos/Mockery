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

package io.victoralbertos.mockery.api.built_in_mockery;

import io.victoralbertos.mockery.api.JsonConverter;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.internal.built_in_mockery.DTOJsonMockery;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(PARAMETER)
@Mockery(DTOJsonMockery.class)
/**
 * Same as {@link DTO}, but this mockery serialize-deserialize the DTO object from-to a json string.
 * This mockery annotation requires that the interface using it supplies a {@link JsonConverter}.
 * To use it, decorate the {@code String} param with this annotation, supplying a valid {@link DTO.Behaviour} implementation.
 */
public @interface DTOJson {
  Class<? extends DTO.Behaviour> value();
}

