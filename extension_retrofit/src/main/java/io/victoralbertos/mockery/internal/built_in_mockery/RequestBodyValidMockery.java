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
import io.victoralbertos.mockery.api.built_in_mockery.RequestBodyValid;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

public final class RequestBodyValidMockery implements Mockery.Behaviour<RequestBodyValid> {
  private final ValidMockery validMockery;

  public RequestBodyValidMockery() {
    validMockery = new ValidMockery();
  }

  /**
   * Serialize the legal object as content of a {@code RequestBody} deferring to {@link ValidMockery} for object creation.
   * @see ValidMockery
   */
  @Override public Object legal(Metadata<RequestBodyValid> metadata) {
    Object legal = validMockery.legal(validMetadata(metadata));
    RequestBody requestBodyLegal = RequestBody
        .create(MediaType.parse("text/plain"), legal.toString());
    return requestBodyLegal;
  }

  /**
   * Serialize the illegal object as content of a {@code RequestBody} deferring to {@link ValidMockery} for object creation.
   * @see ValidMockery
   */
  @Override public Object illegal(Metadata<RequestBodyValid> metadata) {
    Object illegal = validMockery.illegal(validMetadata(metadata));
    illegal = illegal != null ? illegal.toString() : "";
    RequestBody requestBodyIllegal = RequestBody
        .create(MediaType.parse("text/plain"), illegal.toString());
    return requestBodyIllegal;
  }

  /**
   * Deserialize the content of a {@code RequestBody} as the DTO object and defer to its mockery to perform the validation.
   * @see ValidMockery
   */
  @Override public void validate(Metadata<RequestBodyValid> metadata, Object candidate) throws AssertionError {
    try {
      RequestBody requestBody = (RequestBody) candidate;
      Buffer buffer = new Buffer();
      requestBody.writeTo(buffer);
      String body = buffer.readUtf8();
      validMockery.validate(validMetadata(metadata), body);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Support the same type as {@link ValidMockery#supportedTypes(Metadata)}
   */
  @Override public Type[] supportedTypes(Metadata<RequestBodyValid> metadata) {
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
   * Convert a {@code RequestBodyValid} annotation into a {@code Valid} one.
   */
  private Metadata<Valid> validMetadata(Metadata<RequestBodyValid> metadata) {
    final RequestBodyValid requestBodyValid = metadata.getAnnotation();

    Valid dto = new Valid() {

      @Override public String value() {
        return requestBodyValid.value();
      }

      @Override public String legal() {
        return requestBodyValid.legal();
      }

      @Override public String illegal() {
        return requestBodyValid.illegal();
      }

      @Override public Class<? extends Annotation> annotationType() {
        return DTOArgs.class;
      }

    };

    return new Metadata<>(metadata.getMockingClass(),
        metadata.getMethod(), metadata.getArgs(), dto, metadata.getType());
  }
}
