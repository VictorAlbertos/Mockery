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
import io.victoralbertos.mockery.api.SupportedTypes;
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import java.lang.reflect.Type;

public final class OptionalMockery implements Mockery.Behaviour<Optional> {
  private final SafetyCast safetyCast;

  public OptionalMockery() {
    safetyCast = new SafetyCast();
  }

  /**
   * @see OptionalMockery#legalOrIllegal(Metadata)
   */
  @Override public Object legal(Metadata<Optional> metadata) {
    return legalOrIllegal(metadata);
  }

  /**
   * @see OptionalMockery#legalOrIllegal(Metadata)
   */
  @Override public Object illegal(Metadata<Optional> metadata) {
    return legalOrIllegal(metadata);
  }

  /**
   * Return empty if type is String, 0 if it is a number, and otherwise return a null reference.
   */
  private Object legalOrIllegal(Metadata<Optional> metadata) {
    Type type = metadata.getType();

    if (type.equals(String.class)) {
      return safetyCast.with("", type);
    }

    for (Type numericType : SupportedTypes.NUMERIC) {
        if (type.equals(numericType)) {
          return safetyCast.with(0, type);
        }
    }

    return null;
  }

  /**
   * Validate nothing because {@code candidate} is an optional value.
   */
  @Override public void validate(Metadata<Optional> metadata, Object candidate)
      throws AssertionError {
  }

  /**
   * Support any type, or for that matter, the type of the associated param.
   */
  @Override public Type[] supportedTypes(Metadata<Optional> metadata) {
    Type[] typeParam = {metadata.getType()};
    return typeParam;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean isOptional() {
    return true;
  }

}
