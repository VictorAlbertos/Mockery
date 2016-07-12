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
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.EMAIL;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.ID;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.INT;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.NUMBER;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.PHONE;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.URL;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Theories.class)
public final class ValidMockeryTest {
  private ValidMockery validMockery;

  @DataPoints public static Data[] regex() {
    return new Data[]{
        string(), email(), phone(), integer(), id(), number(), url()
    };
  }

  @Before public void init() {
    validMockery = new ValidMockery();
  }

  @Theory
  @Test public void When_Call_Legal_Then_Get_Legal(Data data) throws NoSuchMethodException {
    String methodName = data.methodName;

    Method method = Providers.class.getDeclaredMethod(methodName, data.classParam);
    Valid annotation = (Valid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Valid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    for (int i = 0; i < 50; i++) {
      Object result = validMockery.legal(metadata);
      assertThat(result, instanceOf(data.classParam));
      validMockery.validate(metadata, result);
    }
  }

  @Theory
  @Test public void When_Legal_With_Default_Value_Then_Get_Default(Data data)
      throws NoSuchMethodException {
    String methodName = data.methodName + "WithDefault";
    Method method = Providers.class.getDeclaredMethod(methodName, data.classParam);
    Valid annotation = (Valid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Valid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    String result = String
        .valueOf(validMockery.legal(metadata));
    assertThat(result, is(data.defaultSeed));
  }

  @Theory
  @Test public void When_Pass_Validation_Then_Do_Not_Throw_Assertion_Error(Data data)
      throws NoSuchMethodException {
    String methodName = data.methodName;
    Method method = Providers.class.getDeclaredMethod(methodName, data.classParam);
    Valid annotation = (Valid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Valid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    for (Object legal : data.legal) {
      validMockery.validate(metadata, legal);
    }
  }

  @Theory
  @Test public void When_Not_Pass_Validation_Then_Throw_Assertion_Error(Data data)
      throws NoSuchMethodException {
    String methodName = data.methodName;
    Method method = Providers.class.getDeclaredMethod(methodName, data.classParam);
    Valid annotation = (Valid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<Valid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    for (Object illegal : data.illegal) {
      try {
        validMockery.validate(metadata, illegal);
        fail();
      } catch (AssertionError e) {
        String expectedMessage = validMockery.errorMessage(String.valueOf(illegal),
            metadata.getAnnotation().value());
        assertThat(expectedMessage, is(e.getMessage()));
      }
    }
  }

  @Test public void When_Support_Type_Retrieved_Then_Is_Numeric_And_Text() {
    Type[] supportedTypes = validMockery.supportedTypes(null);
    assertThat(supportedTypes.length, is(10));

    Type[] expectedTypes = SupportedTypes.concat(SupportedTypes.NUMERIC, SupportedTypes.TEXT);

    for (Type expected : expectedTypes) {
      assertTrue(Arrays.asList(supportedTypes).contains(expected));
    }
  }

  private static Data string() {
    return new Data(String.class, "string", "74hrun",
        new Object[]{"9h4uefjno"},
        new Object[]{""});
  }

  private static Data email() {
    return new Data(String.class, "email", "email@example.com",
        new Object[]{"email@example.com", "firstname.lastname@example.com", "1234567890@example.com"},
        new Object[]{"#@%^%#$@#$@#.com", "@example.com", "Joe Smith <email@example.com>"});
  }

  private static Data phone() {
    return new Data(String.class, "phone", "9653425132",
        new Object[]{"+34661234532", "664 123 4567", "202-555-0182", "+1-202-555-0151"},
        new Object[]{"", "+3fd61234532", "664B123z4567", "20dws2-555-0182", "+1-202-5f3255-0151"});
  }

  private static Data integer() {
    return new Data(int.class, "integer", "1",
        new Object[]{0, 101, -5, 2324},
        new Object[]{"", 0.34, 5.32, "ddsass"});
  }

  private static Data id() {
    return new Data(int.class, "id", "1",
        new Object[]{101, 1, 4},
        new Object[]{"", 0.34, 5.32, "ddsass", -3, 0});
  }

  private static Data number() {
    return new Data(double.class, "number", "1.34",
        new Object[]{-101, -0.11, 1.23, 0, 0.00, -34.343, 2},
        new Object[]{"", "fff"});
  }

  private static Data url() {
    return new Data(String.class, "url", "http://foo.com/blah_blah",
        new Object[]{"http://foo.com/blah_blah", "http://foo.com/blah_blah",
            "http://www.example.com/wpstyle/?p=364"},
        new Object[]{"", "foo.com", "rdar://1234", "http:// shouldfail.com",
            "    :// should fail", "http://foo.bar/foo(bar)baz quux"});
  }

  private static class Data {
    private final Class<?> classParam;
    private final String methodName, defaultSeed;
    private final Object[] legal, illegal;

    public Data(Class<?> classParam, String methodName, String defaultSeed, Object[] legals,
        Object[] illegal) {
      this.classParam = classParam;
      this.methodName = methodName;
      this.defaultSeed = defaultSeed;
      this.legal = legals;
      this.illegal = illegal;
    }
  }

  private interface Providers {
    void string(@Valid(STRING) String string);
    void stringWithDefault(@Valid(value = STRING, legal = "74hrun") String string);

    void email(@Valid(EMAIL) String email);
    void emailWithDefault(@Valid(value = EMAIL, legal = "email@example.com") String email);

    void phone(@Valid(PHONE) String phone);
    void phoneWithDefault(@Valid(value = PHONE, legal = "9653425132") String phone);

    void integer(@Valid(INT) int integer);
    void integerWithDefault(@Valid(value = INT, legal = "1") int integer);

    void id(@Valid(ID) int integer);
    void idWithDefault(@Valid(value = ID, legal = "1") int integer);

    void number(@Valid(NUMBER) double number);
    void numberWithDefault(@Valid(value = NUMBER, legal = "1.34") double number);

    void url(@Valid(URL) String url);
    void urlWithDefault(@Valid(value = URL, legal = "http://foo.com/blah_blah") String url);
  }

  @Test public void When_Illegal_With_Default_Value_Then_Get_Default()
      throws NoSuchMethodException {
    Method method = ProvidersIllegal.class
        .getDeclaredMethod("checkIllegalDefault", String.class);

    Valid annotation = (Valid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];
    Metadata<Valid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat(((String)validMockery.illegal(metadata)), is("illegal"));
  }

  @Test public void When_Call_Illegal_With_Primitive_Type_Then_Illegal_Matches_Expected_Type()
      throws NoSuchMethodException {
    Method method = ProvidersIllegal.class.getDeclaredMethod("checkIllegalsPrimitives",
        int.class, long.class, double.class, float.class);

    Valid annotation = (Valid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];
    Metadata<Valid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((int) validMockery.illegal(metadata), is(0));

    annotation = (Valid) method.getParameterAnnotations()[1][0];
    type = method.getGenericParameterTypes()[1];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((long) validMockery.illegal(metadata), is(0l));

    annotation = (Valid) method.getParameterAnnotations()[2][0];
    type = method.getGenericParameterTypes()[2];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((double) validMockery.illegal(metadata), is(0d));

    annotation = (Valid) method.getParameterAnnotations()[3][0];
    type = method.getGenericParameterTypes()[3];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    assertThat((float) validMockery.illegal(metadata), is(0f));
  }

  @Test public void When_Call_Illegal_With_No_Primitive_Type_Then_Illegal_Is_0_Or_Empty()
      throws NoSuchMethodException {
    Method method = ProvidersIllegal.class.getDeclaredMethod("checkIllegalsNotPrimitives",
        String.class, Integer.class, Long.class, Double.class, Float.class);

    Valid annotation = (Valid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];
    Metadata<Valid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertTrue(((String)validMockery.illegal(metadata))
        .isEmpty());

    annotation = (Valid) method.getParameterAnnotations()[1][0];
    type = method.getGenericParameterTypes()[1];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Integer)validMockery.illegal(metadata)), is(0));

    annotation = (Valid) method.getParameterAnnotations()[2][0];
    type = method.getGenericParameterTypes()[2];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Long)validMockery.illegal(metadata)), is(0l));

    annotation = (Valid) method.getParameterAnnotations()[3][0];
    type = method.getGenericParameterTypes()[3];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Double)validMockery.illegal(metadata)), is(0d));

    annotation = (Valid) method.getParameterAnnotations()[4][0];
    type = method.getGenericParameterTypes()[4];
    metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);
    assertThat(((Float)validMockery.illegal(metadata)), is(0f));
  }

  private interface ProvidersIllegal {
    void checkIllegalDefault(@Valid(value = STRING, illegal = "illegal") String s1);

    void checkIllegalsPrimitives(@Valid(NUMBER) int i1, @Valid(NUMBER) long l1,
        @Valid(NUMBER) double d1, @Valid(NUMBER) float f1);

    void checkIllegalsNotPrimitives(@Valid(STRING) String s1, @Valid(NUMBER) Integer i1,
        @Valid(NUMBER) Long l1, @Valid(NUMBER) Double d1, @Valid(NUMBER) Float f1);
  }

}
