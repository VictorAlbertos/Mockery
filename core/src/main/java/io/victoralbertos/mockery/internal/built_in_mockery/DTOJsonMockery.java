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

import io.victoralbertos.jolyglot.JolyglotGenerics;
import io.victoralbertos.mockery.api.JsonConverter;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.api.built_in_mockery.*;
import io.victoralbertos.mockery.internal.InstantiateInterface;
import io.victoralbertos.mockery.internal.Messages;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class DTOJsonMockery implements Mockery.Behaviour<DTOJson> {
  private final DTOMockery dtoMockery;
  private final InstantiateInterface instantiateInterface;

  public DTOJsonMockery() {
    dtoMockery = new DTOMockery();
    instantiateInterface = new InstantiateInterface();
  }

  /**
   * Serialize the DTO legal object deferred from its mockery as a json {@code String}.
   * @see DTO.Behaviour
   */
  @Override public Object legal(Metadata<DTOJson> metadata) {
    JolyglotGenerics jolyglot = jolyglot(metadata);
    Object seed = dtoMockery.legal(DTOMetadata(metadata));
    return jolyglot.toJson(seed);
  }

  /**
   * Serialize the DTO illegal object deferred from its mockery as a json {@code String}.
   * @see DTO.Behaviour
   */
  @Override public Object illegal(Metadata<DTOJson> metadata) {
    return "";
  }

  /**
   * Deserialize the content of a json {@code String} as the DTO object deferred from its mockery to perform the validation.
   * @see DTO.Behaviour
   */
  @Override public void validate(Metadata<DTOJson> metadata, Object candidate) throws AssertionError {
    if (candidate == null) {
      dtoMockery.validate(DTOMetadata(metadata), candidate);
    }

    Metadata<DTO> dtoMetadata = DTOMetadata(metadata);
    Type type = dtoMockery.supportedTypes(dtoMetadata)[0];

    String json = (String) candidate;
    JolyglotGenerics jolyglot = jolyglot(metadata);

    Object object;

    try {
      object = jolyglot.fromJson(json, type);
    } catch (RuntimeException e) {
      object = null;
    }

    dtoMockery.validate(DTOMetadata(metadata), object);
  }

  /**
   * Support {@code String} as associated param type.
   */
  @Override public Type[] supportedTypes(Metadata<DTOJson> metadata) {
    Type[] types = {String.class};
    return types;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean isOptional() {
    return false;
  }

  private JolyglotGenerics jolyglot(Metadata<DTOJson> metadata) {
    JsonConverter jsonConverter = metadata.getMockingClass()
        .getAnnotation(JsonConverter.class);

    if (jsonConverter == null) {
      String message = Messages.noJsonConverterFound(
          metadata.getMockingClass(),
          metadata.getMethod(),
          DTOJson.class);
      throw new RuntimeException(message);
    }

    JolyglotGenerics jolyglot = instantiateInterface.from(jsonConverter.value());
    return jolyglot;
  }

  /**
   * Convert a {@code DTOJson} annotation into a {@code DTO} one.
   */
  private Metadata<DTO> DTOMetadata(Metadata<DTOJson> metadata) {
    final DTOJson dtoJson = metadata.getAnnotation();

    DTO dto = new DTO() {
      @Override public Class<? extends Behaviour> value() {
        return dtoJson.value();
      }

      @Override public Class<? extends Annotation> annotationType() {
        return DTOArgs.class;
      }
    };

    return new Metadata<>(metadata.getMockingClass(),
        metadata.getMethod(), metadata.getArgs(), dto, metadata.getType());
  }
}
