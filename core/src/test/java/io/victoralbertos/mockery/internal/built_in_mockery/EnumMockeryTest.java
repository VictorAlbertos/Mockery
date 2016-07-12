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
import io.victoralbertos.mockery.api.SupportedTypes;
import io.victoralbertos.mockery.api.built_in_mockery.Enum;
import io.victoralbertos.mockery.internal.Messages;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class EnumMockeryTest {
  private EnumMockery enumMockery;
  @Rule public final ExpectedException exception = ExpectedException.none();

  @Before public void init() {
    enumMockery = new EnumMockery();
  }

  @Test public void When_Empty_Array_Then_Throw_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("emptyArray", String.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    String errorMessage = Messages
        .emptyEnumArray(Providers.class, method);

    try {
      enumMockery.legal(metadata);
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is(errorMessage));
    }

    try {
      enumMockery.illegal(metadata);
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is(errorMessage));
    }

    try {
      enumMockery.validate(metadata, "no matter");
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is(errorMessage));
    }
  }

  @Test public void When_Legal_With_Default_Value_Then_Get_Default()
      throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("checkDefaults", String.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    String result = String
        .valueOf(enumMockery.legal(metadata));
    assertThat(result, is("desc"));
  }

  @Test public void When_Illegal_With_Default_Value_Then_Get_Default()
      throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("checkDefaults", String.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    String result = String
        .valueOf(enumMockery.illegal(metadata));
    assertThat(result, is("illegal"));
  }

  @Test public void When_Call_Legal_Then_Get_Legal() throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("object", String.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    for (int i = 0; i < 30; i++) {
      String legal = (String) enumMockery.legal(metadata);
      enumMockery.validate(metadata, legal);
    }
  }

  @Test public void When_Call_Legal_Primitive_Then_Get_Legal() throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("primitive", int.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    for (int i = 0; i < 30; i++) {
      int legal = (int) enumMockery.legal(metadata);
      enumMockery.validate(metadata, legal);
    }
  }

  @Test public void When_Call_Illegal_Then_Defer_To_Optional_Mockery()
      throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("object", String.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    String legal = (String) enumMockery.illegal(metadata);
    assertTrue(legal.isEmpty());
  }

  @Test public void When_Call_Illegal_Primitive_Then_Defer_To_Optional_Mockery()
      throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("primitive", int.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    int legal = (int) enumMockery.illegal(metadata);
    assertThat(legal, is(0));
  }

  @Test public void When_Pass_Validation_Then_Do_Not_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("object", String.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    String legal = (String) enumMockery.legal(metadata);
    enumMockery.validate(metadata, legal);
  }

  @Test public void When_Not_Pass_Validation_Then_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = Providers.class
        .getDeclaredMethod("object", String.class);
    Enum annotation = (Enum) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Enum> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    try {
      String illegal = (String) enumMockery.illegal(metadata);
      enumMockery.validate(metadata, illegal);
      fail();
    } catch (AssertionError e) {
      assertThat(e.getMessage(), is("empty not matches with any of specified values: "
          + "[asc, desc, sort, random]"));
    }
  }

  @Test public void When_Support_Type_Retrieved_Then_Is_Numeric_And_Text() {
    Type[] supportedTypes = enumMockery.supportedTypes(null);
    assertThat(supportedTypes.length, is(10));

    Type[] expectedTypes = SupportedTypes.concat(SupportedTypes.NUMERIC, SupportedTypes.TEXT);

    for (Type expected : expectedTypes) {
      assertTrue(Arrays.asList(supportedTypes).contains(expected));
    }
  }

  private interface Providers {
    void emptyArray(@Enum(value = {}) String s1);

    void checkDefaults(@Enum(value = {"desc"}, legal = "desc", illegal = "illegal") String s1);

    void object(@Enum(value = {"asc", "desc", "sort", "random"}) String s1);
    void primitive(@Enum(value = {"0", "1", "2", "3"}) int i1);
  }
}
