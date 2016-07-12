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

package io.victoralbertos.mockery.api;

import io.victoralbertos.jolyglot.Types;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public final class SupportedTypes {
  /**
   * An array with the more common text {@code Type} for Java.
   */
  public final static Type[] TEXT = {String.class, Character.class};

  /**
   * An array with the more common list text {@code Type} for Java.
   */
  public final static Type[] LIST_TEXT = {listOf(String.class), listOf(Character.class)};

  /**
   * An array with the more common numeric {@code Type} for Java, containing both primitives and objects.
   */
  public final static Type[] NUMERIC = {double.class, Double.class, float.class, Float.class, int.class, Integer.class, long.class, Long.class};

  /**
   * An array with the more common list numeric {@code Type} for Java, containing both primitives and objects.
   */
  public final static Type[] LIST_NUMERIC = {listOf(Double.class), listOf(Float.class),
      listOf(Integer.class),  listOf(Long.class)};

  /**
   * Given a {@code type}, returns a {@code List} {@code type} parameterized with the previously {@code type} supplied.
   * @param type the type which to parameterize the list.
   */
  public final static Type listOf(final Type type) {
    return Types.newParameterizedType(List.class, type);
  }

  /**
   * Concat two {@code type} arrays.
   * @param first the first array to concat.
   * @param second the second array to concat.
   * @return the resulting concatenated array.
   */
  public final static Type[] concat(Type[] first, Type[] second) {
    Type[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }
}
