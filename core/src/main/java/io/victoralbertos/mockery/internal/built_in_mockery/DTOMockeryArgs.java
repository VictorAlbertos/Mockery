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
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import io.victoralbertos.mockery.internal.InstantiateInterface;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class DTOMockeryArgs implements Mockery.Behaviour<DTOArgs> {
  private final InstantiateInterface instantiateInterface;

  public DTOMockeryArgs() {
    instantiateInterface = new InstantiateInterface();
  }

  /**
   * Same as {@link DTOMockery}
   */
  @Override public Object legal(Metadata<DTOArgs> metadata) {
    DTOArgs dtoArgs = metadata.getAnnotation();
    DTOArgs.Behaviour behaviour = instantiateInterface.from(dtoArgs.value());
    return behaviour.legal(metadata.getArgs());
  }

  /**
   * Same as {@link DTOMockery}
   */
  @Override public Object illegal(Metadata<DTOArgs> metadata) {
    return null;
  }

  /**
   * Same as {@link DTOMockery}
   */
  @Override public void validate(Metadata<DTOArgs> metadata, Object candidate) throws AssertionError {
    DTOArgs dtoArgs = metadata.getAnnotation();
    DTOArgs.Behaviour behaviour = instantiateInterface.from(dtoArgs.value());
    behaviour.validate(candidate);
  }

  /**
   * Same as {@link DTOMockery}
   */
  @Override public Type[] supportedTypes(Metadata<DTOArgs> metadata) {
    DTOArgs dtoArgs = metadata.getAnnotation();

    ParameterizedType behaviourInterfaceType = null;

    for (Type type : dtoArgs.value().getGenericInterfaces()) {
      if (!(type instanceof ParameterizedType)) continue;

      Class<?> interfaceClass = (Class) ((ParameterizedType)type).getRawType();
      if (DTOArgs.Behaviour.class.isAssignableFrom(interfaceClass)) {
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
