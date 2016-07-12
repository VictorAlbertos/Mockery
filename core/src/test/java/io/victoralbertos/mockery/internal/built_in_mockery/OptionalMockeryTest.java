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
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class OptionalMockeryTest {
  private OptionalMockery optionalMockery;
  
  @Before public void init() {
    optionalMockery = new OptionalMockery();
  }

  @Test public void When_Call_Legal_Or_Illegal_With_Primitive_Type_Then_Matches_Expected_Type()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("checkPrimitives",
        int.class, long.class, double.class, float.class);

    Optional annotation = (Optional) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];
    Metadata<Optional> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((int) optionalMockery.illegal(metadata), is(0));
    assertThat((int) optionalMockery.legal(metadata), is(0));

    annotation = (Optional) method.getParameterAnnotations()[1][0];
    type = method.getGenericParameterTypes()[1];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((long) optionalMockery.illegal(metadata), is(0l));
    assertThat((long) optionalMockery.legal(metadata), is(0l));

    annotation = (Optional) method.getParameterAnnotations()[2][0];
    type = method.getGenericParameterTypes()[2];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((double) optionalMockery.illegal(metadata), is(0d));
    assertThat((double) optionalMockery.legal(metadata), is(0d));

    annotation = (Optional) method.getParameterAnnotations()[3][0];
    type = method.getGenericParameterTypes()[3];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((float) optionalMockery.illegal(metadata), is(0f));
    assertThat((float) optionalMockery.legal(metadata), is(0f));
  }

  @Test public void When_Call_Legal_Or_Illegal_With_No_Primitive_Type_Then_Illegal_Is_0_Or_Empty()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("checkNotPrimitives",
        String.class, Integer.class, Long.class, Double.class, Float.class);

    Optional annotation = (Optional) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];
    Metadata<Optional> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertTrue(((String)optionalMockery.illegal(metadata))
        .isEmpty());
    assertTrue(((String)optionalMockery.legal(metadata))
        .isEmpty());

    annotation = (Optional) method.getParameterAnnotations()[1][0];
    type = method.getGenericParameterTypes()[1];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Integer)optionalMockery.illegal(metadata)), is(0));
    assertThat(((Integer)optionalMockery.legal(metadata)), is(0));

    annotation = (Optional) method.getParameterAnnotations()[2][0];
    type = method.getGenericParameterTypes()[2];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Long)optionalMockery.illegal(metadata)), is(0l));
    assertThat(((Long)optionalMockery.legal(metadata)), is(0l));

    annotation = (Optional) method.getParameterAnnotations()[3][0];
    type = method.getGenericParameterTypes()[3];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Double)optionalMockery.illegal(metadata)), is(0d));
    assertThat(((Double)optionalMockery.legal(metadata)), is(0d));

    annotation = (Optional) method.getParameterAnnotations()[4][0];
    type = method.getGenericParameterTypes()[4];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Float)optionalMockery.illegal(metadata)), is(0f));
    assertThat(((Float)optionalMockery.legal(metadata)), is(0f));
  }

  @Test public void When_Call_Legal_Or_Illegal_With_Custom_Object_Return_Null()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("checkCustomObject", Model.class);

    Optional annotation = (Optional) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];
    Metadata<Optional> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertNull(optionalMockery.illegal(metadata));
    assertNull(optionalMockery.legal(metadata));
  }

  @Test public void When_Support_Type_Retrieved_Then_Is_The_Same_as_The_Type_Of_The_Param() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("checkCustomObject", Model.class);
    Optional annotation = (Optional) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];
    Metadata<Optional> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Type[] supportedTypes = optionalMockery.supportedTypes(metadata);
    assertThat(supportedTypes.length, is(1));
    assertTrue(Arrays.asList(supportedTypes).contains(Model.class));
  }

  private interface Providers {
    void checkPrimitives(@Optional int i1, @Optional long l1,
        @Optional double d1, @Optional float f1);

    void checkNotPrimitives(@Optional String s1, @Optional Integer i1,
        @Optional Long l1, @Optional Double d1, @Optional Float f1);

    void checkCustomObject(@Optional Model model);
  }

  private static class Model {

  }
}
