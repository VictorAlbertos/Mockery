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

package io.victoralbertos.mockery.internal.built_in_mockery;

import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.internal.InstantiateInterface;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class DTOMockery implements Mockery.Behaviour<DTO> {
  private final InstantiateInterface instantiateInterface;

  public DTOMockery() {
    instantiateInterface = new InstantiateInterface();
  }

  /**
   * Provide a legal value deferring to the {@link Mockery.Behaviour} implementation supplied.
   */
  @Override public Object legal(Metadata<DTO> metadata) {
    DTO dto = metadata.getAnnotation();
    DTO.Behaviour behaviour = instantiateInterface.from(dto.value());
    return behaviour.legal();
  }

  /**
   * Provide a null reference as the illegal value.
   */
  @Override public Object illegal(Metadata<DTO> metadata) {
    return null;
  }

  /**
   * Perform validation upon {@code candidate} deferring to the {@link Mockery.Behaviour} implementation supplied.
   */
  @Override public void validate(Metadata<DTO> metadata, Object candidate) throws AssertionError {
    DTO dto = metadata.getAnnotation();
    DTO.Behaviour behaviour = instantiateInterface.from(dto.value());
    behaviour.validate(candidate);
  }

  /**
   * Support the same type that the one that has been parameterized when implementing the {@link Mockery.Behaviour} interface.
   */
  @Override public Type[] supportedTypes(Metadata<DTO> metadata) {
    DTO dto = metadata.getAnnotation();

    ParameterizedType behaviourInterfaceType = null;

    for (Type type : dto.value().getGenericInterfaces()) {
      if (!(type instanceof ParameterizedType)) continue;

      Class<?> interfaceClass = (Class) ((ParameterizedType)type).getRawType();
      if (DTO.Behaviour.class.isAssignableFrom(interfaceClass)) {
        behaviourInterfaceType = (ParameterizedType) type;
        break;
      }
    }

    Type supportedType = behaviourInterfaceType.getActualTypeArguments()[0];
    Type[] types = {supportedType};
    return types;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean isOptional() {
    return false;
  }

}
