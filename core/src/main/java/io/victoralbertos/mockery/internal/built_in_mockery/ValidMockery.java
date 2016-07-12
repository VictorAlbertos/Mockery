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

import com.mifmif.common.regex.Generex;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.api.SupportedTypes;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class ValidMockery implements Mockery.Behaviour<Valid> {
  private final SafetyCast safetyCast;

  public ValidMockery() {
    safetyCast = new SafetyCast();
  }

  /**
   * Return as legal value the generated value from the regex pattern supplied.
   */
  @Override public Object legal(Metadata<Valid> metadata) {
    Valid valid = metadata.getAnnotation();

    String defaultLegal = valid.legal();
    if (!defaultLegal.isEmpty()) {
      return safetyCast.with(defaultLegal,
          metadata.getType());
    }

    String regex = valid.value();
    String result = new Generex(regex).random();

    result = result.replace("^", "")
        .replace("$", "")
        .replace("?", "");

    if (regex.equals(Valid.Template.ID)) {
      if (result.length() > 1 && result.charAt(0) == '0') {
        result = result.replaceFirst("0", "");
      }
    }

    if (regex.equals(Valid.Template.INT)
        || regex.equals(Valid.Template.ID) || regex.equals(Valid.Template.NUMBER)) {
      if (result.length() > 8) {
        result = result.substring(0, 7);
      }
    }

    return safetyCast.with(result, metadata.getType());
  }

  /**
   * Return empty if type is string, otherwise return 0.
   */
  @Override public Object illegal(Metadata<Valid> metadata) {
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
   * Validate {@code candidate} based on the regex pattern supplied.
   */
  @Override public void validate(Metadata<Valid> metadata, Object candidate) throws AssertionError {
    Valid valid = metadata.getAnnotation();
    String regex = valid.value();
    String input = String.valueOf(candidate);

    if (!input.matches(regex))
      throw new AssertionError(errorMessage(input,regex));
  }

  /**
   * Support {@link SupportedTypes#NUMERIC} and {@link SupportedTypes#TEXT} {@code type}.
   */
  @Override public Type[] supportedTypes(Metadata<Valid> metadata) {
    Type[] types = SupportedTypes.concat(SupportedTypes.NUMERIC, SupportedTypes.TEXT);
    return types;
  }

  /**
   * {@inheritDoc}
   */
  @Override public boolean isOptional() {
    return false;
  }

  //Visible for testing
  String errorMessage(String input, String regex) {
    String outputMessage = input;
    Field[] fields = Valid.Template.class.getFields();

    for (Field field : fields) {
      try {
        String regexField = (String) field.get(null);
        if (regexField.equals(regex)) {
          outputMessage = field.getName();
          break;
        }
      } catch (Exception i) {}
    }

    if (input.isEmpty()) input = "empty";
    return input + " does not match with regex " + outputMessage;
  }

}
