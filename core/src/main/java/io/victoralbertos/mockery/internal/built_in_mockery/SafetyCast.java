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

import java.lang.reflect.Type;

/**
 * Perform a cast from {@code object} to its hidden type.
 * Support for common types both primitives and objects on Java platform.
 */
public final class SafetyCast {

  public Object with(Object object, Type type) {
    Class classType = (Class) type;

    if (classType.equals(int.class) || classType.equals(Integer.class)) {
      return Integer.valueOf(object.toString());
    } else if (classType.equals(long.class) || classType.equals(Long.class)) {
      return Long.valueOf(object.toString());
    } else if (classType.equals(float.class) || classType.equals(Float.class)) {
      return Float.valueOf(object.toString());
    } else if (classType.equals(double.class) || classType.equals(Double.class)) {
      return Double.valueOf(object.toString());
    } else if (classType.equals(String.class)) {
      return object.toString();
    }

    throw new RuntimeException("Type does not match any common java type");
  }

}
