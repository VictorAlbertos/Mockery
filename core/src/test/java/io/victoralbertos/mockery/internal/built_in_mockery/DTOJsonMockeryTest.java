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

import io.victoralbertos.jolyglot.MoshiSpeaker;
import io.victoralbertos.mockery.api.JsonConverter;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_mockery.DTOJson;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class DTOJsonMockeryTest {
  private DTOJsonMockery dtoJsonMockery;
  @Rule public final ExpectedException exception = ExpectedException.none();

  @Before public void init() {
    dtoJsonMockery = new DTOJsonMockery();
  }

  @Test public void When_Class_Has_Not_Json_Converter_Then_Throw_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("mock", String.class);
    DTOJson annotation = (DTOJson) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<DTOJson> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    exception.expect(RuntimeException.class);
    dtoJsonMockery.legal(metadata);
  }

  @Test public void When_Call_Legal_Then_Get_Legal()
      throws NoSuchMethodException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", String.class);
    DTOJson annotation = (DTOJson) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<DTOJson> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    String json = (String) dtoJsonMockery.legal(metadata);
    assertThat(json, is(jsonMockSample()));
  }

  @Test public void When_Call_Illegal_Then_Then_Get_Empty_String()
      throws NoSuchMethodException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", String.class);
    DTOJson annotation = (DTOJson) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<DTOJson> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    String illegal = (String) dtoJsonMockery.illegal(metadata);
    assertTrue(illegal.isEmpty());
  }

  @Test public void When_Pass_Validation_Then_Do_Not_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", String.class);
    DTOJson annotation = (DTOJson) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<DTOJson> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    String json = (String) dtoJsonMockery.legal(metadata);
    dtoJsonMockery.validate(metadata, json);
  }

  @Test public void When_Not_Pass_Validation_Then_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", String.class);
    DTOJson annotation = (DTOJson) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<DTOJson> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    exception.expect(AssertionError.class);
    dtoJsonMockery.validate(metadata, null);
  }

  @Test public void When_Support_Type_Retrieved_Then_Is_String() {
    Type[] types = dtoJsonMockery.supportedTypes(null);
    assertThat(types.length, is(1));

    Class clazz = (Class) types[0];
    assertEquals(clazz, String.class);
  }

  private String jsonMockSample() {
    return "{\"s1\":\"s1\"}";
  }

  private interface Providers {
    void mock(@DTOJson(MockCreator.class) String mock);
  }

  @JsonConverter(MoshiSpeaker.class)
  private interface ProvidersJsonConverter {
    void mock(@DTOJson(MockCreator.class) String mock);
  }

  private static class MockCreator implements DTO.Behaviour<Mock> {
    @Override public Mock legal() {
      return new Mock();
    }

    @Override public void validate(Mock candidate) throws AssertionError {
      assertNotNull(candidate);
      assertThat(candidate.getS1(), is("s1"));
    }
  }

  private static class Mock {
    private final String s1;

    public Mock() {
      this.s1 = "s1";
    }

    public String getS1() {
      return s1;
    }

  }

}
