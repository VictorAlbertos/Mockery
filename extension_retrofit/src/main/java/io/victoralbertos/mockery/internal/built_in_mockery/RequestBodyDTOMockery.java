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
import io.victoralbertos.mockery.api.built_in_mockery.DTOJson;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.RequestBodyDTO;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

public final class RequestBodyDTOMockery implements Mockery.Behaviour<RequestBodyDTO> {
  private final DTOJsonMockery dtoJsonMockery;

  public RequestBodyDTOMockery() {
    dtoJsonMockery = new DTOJsonMockery();
  }

  /**
   * Serialize the DTO legal object deferred from its mockery as the content of a {@code RequestBody}.
   * @see DTO.Behaviour
   */
  @Override public Object legal(Metadata<RequestBodyDTO> metadata) {
    Object legal = dtoJsonMockery.legal(DTOJsonMetadata(metadata));
    RequestBody requestBodyLegal = RequestBody
        .create(MediaType.parse("text/plain"), legal.toString());
    return requestBodyLegal;
  }

  /**
   * Serialize the DTO illegal object deferred from its mockery as the content of a {@code RequestBody}.
   * @see DTO.Behaviour
   */
  @Override public Object illegal(Metadata<RequestBodyDTO> metadata) {
    Object illegal = dtoJsonMockery.illegal(DTOJsonMetadata(metadata));
    RequestBody requestBodyIllegal = RequestBody
        .create(MediaType.parse("text/plain"), illegal.toString());
    return requestBodyIllegal;
  }

  /**
   * Deserialize the content of a {@code RequestBody} as the DTO object and defer to its mockery to perform the validation.
   * @see DTO.Behaviour
   */
  @Override public void validate(Metadata<RequestBodyDTO> metadata, Object candidate) throws AssertionError {
    if (candidate == null) {
      dtoJsonMockery.validate(DTOJsonMetadata(metadata), candidate);
    }

    try {
      RequestBody requestBody = (RequestBody) candidate;
      Buffer buffer = new Buffer();
      requestBody.writeTo(buffer);
      String body = buffer.readUtf8();
      buffer.close();
      dtoJsonMockery.validate(DTOJsonMetadata(metadata), body);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Support {@code RequestBody} as associated param type.
   */
  @Override public Type[] supportedTypes(Metadata<RequestBodyDTO> metadata) {
    Type[] supportedTypes = {RequestBody.class};
    return supportedTypes;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean isOptional() {
    return false;
  }

  /**
   * Convert a {@code RequestBodyDTO} annotation into a {@code DTOJson} one.
   */
  private Metadata<DTOJson> DTOJsonMetadata(Metadata<RequestBodyDTO> metadata) {
    final RequestBodyDTO requestBodyValid = metadata.getAnnotation();

    DTOJson dto = new DTOJson() {

      @Override public Class<? extends DTO.Behaviour> value() {
        return requestBodyValid.value();
      }

      @Override public Class<? extends Annotation> annotationType() {
        return DTOJson.class;
      }
    };

    return new Metadata<>(metadata.getMockingClass(),
        metadata.getMethod(), metadata.getArgs(), dto, metadata.getType());
  }
}
