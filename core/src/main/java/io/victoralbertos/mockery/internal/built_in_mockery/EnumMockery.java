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
import io.victoralbertos.mockery.api.built_in_mockery.Enum;
import io.victoralbertos.mockery.internal.Messages;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Random;


public final class EnumMockery implements Mockery.Behaviour<Enum> {
  private final SafetyCast safetyCast;

  public EnumMockery() {
    safetyCast = new SafetyCast();
  }

  /**
   * Return as legal value a random one from the array supplied, or if legal value was set then return that.
   */
  @Override public Object legal(Metadata<Enum> metadata) {
    throwExceptionOnEmptyArray(metadata);

    Enum enumAnnotation = metadata.getAnnotation();

    String defaultLegal = enumAnnotation.legal();
    if (!defaultLegal.isEmpty()) {
      return safetyCast.with(defaultLegal,
          metadata.getType());
    }

    int length = enumAnnotation.value().length;
    int randomIndex = new Random().nextInt(length);

    String result = enumAnnotation.value()[randomIndex];
    return safetyCast.with(result, metadata.getType());
  }

  /**
   * Return empty if type is string, otherwise return 0.
   */
  @Override public Object illegal(Metadata<Enum> metadata) {
    throwExceptionOnEmptyArray(metadata);

    String defaultOptional = metadata.getAnnotation().illegal();
    Type type = metadata.getType();

    if (!defaultOptional.isEmpty()) {
      return safetyCast.with(defaultOptional, metadata.getType());
    } else if (type.equals(String.class)) {
      return safetyCast.with("", type);
    } else {
      return safetyCast.with(0, type);
    }
  }

  /**
   * Validate {@code candidate} checking if its value is present in the array supplied.
   */
  @Override public void validate(Metadata<Enum> metadata, Object candidate) throws AssertionError {
    throwExceptionOnEmptyArray(metadata);

    boolean valid = false;
    String[] values = metadata.getAnnotation().value();

    for (String value : values) {
      if (candidate == null) break;
      if (value.equals(candidate.toString())) {
        valid = true;
        break;
      }
    }

    String input = String.valueOf(candidate);
    if (input.isEmpty()) input = "empty";

    String errorMessage = input + " not matches with any of specified values: "
        + Arrays.toString(values);

    if (!valid) throw new AssertionError(errorMessage);
  }

  /**
   * Support {@link SupportedTypes#NUMERIC} and {@link SupportedTypes#TEXT} {@code type}.
   */
  @Override public Type[] supportedTypes(Metadata<Enum> metadata) {
    Type[] types = SupportedTypes.concat(SupportedTypes.NUMERIC, SupportedTypes.TEXT);
    return types;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean isOptional() {
    return false;
  }

  private void throwExceptionOnEmptyArray(Metadata<Enum> metadata) {
    if (metadata.getAnnotation().value().length == 0) {
      String errorMessage = Messages.emptyEnumArray(metadata.getMockingClass(),
          metadata.getMethod());
      throw new RuntimeException(errorMessage);
    }
  }

}
