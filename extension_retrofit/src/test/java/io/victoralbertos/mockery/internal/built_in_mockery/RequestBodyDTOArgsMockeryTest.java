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

import io.victoralbertos.jolyglot.JacksonSpeaker;
import io.victoralbertos.mockery.api.JsonConverter;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.RequestBodyDTO;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okio.Buffer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class RequestBodyDTOArgsMockeryTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private RequestBodyDTOMockery requestBodyDTOMockery;

  @Before public void init() {
    requestBodyDTOMockery = new RequestBodyDTOMockery();
  }

  @Test public void When_Class_Has_Not_Json_Converter_Then_Throw_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("mock", Mock.class);
    RequestBodyDTO annotation = (RequestBodyDTO) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyDTO> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    exception.expect(RuntimeException.class);
    requestBodyDTOMockery.legal(metadata);
  }

  @Test public void When_Call_Legal_Then_Return_RequestBody_Legal()
      throws NoSuchMethodException, IOException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", Mock.class);
    RequestBodyDTO annotation = (RequestBodyDTO) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyDTO> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyDTOMockery.legal(metadata);
    assertNotNull(requestBody);
    assertThat(requestBody.contentType().toString(), is("text/plain; charset=utf-8"));

    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    String json = buffer.readUtf8();
    assertThat(json, is(jsonMockSample()));
  }

  @Test public void When_Call_Illegal_Then_Return_RequestBody_Illegal()
      throws NoSuchMethodException, IOException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", Mock.class);
    RequestBodyDTO annotation = (RequestBodyDTO) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyDTO> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyDTOMockery.illegal(metadata);
    assertNotNull(requestBody);
    assertThat(requestBody.contentType().toString(), is("text/plain; charset=utf-8"));

    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    String illegalJson = buffer.readUtf8();
    assertTrue(illegalJson.isEmpty());
  }

  @Test public void When_Pass_Validation_Then_Do_Not_Throw_Assertion_Error()
      throws NoSuchMethodException, IOException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", Mock.class);
    RequestBodyDTO annotation = (RequestBodyDTO) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyDTO> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyDTOMockery.legal(metadata);
    requestBodyDTOMockery.validate(metadata, requestBody);
  }

  @Test public void When_Not_Pass_Validation_Then_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", Mock.class);
    RequestBodyDTO annotation = (RequestBodyDTO) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyDTO> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    exception.expect(AssertionError.class);
    requestBodyDTOMockery.validate(metadata, null);
  }

  @Test public void When_Support_Type_Retrieved_Then_Is_Request_Body()
      throws NoSuchMethodException {
    Method method = ProvidersJsonConverter.class.getDeclaredMethod("mock", Mock.class);
    RequestBodyDTO annotation = (RequestBodyDTO) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyDTO> metadata = new Metadata<>(ProvidersJsonConverter.class,
        method, null, annotation, type);

    Type[] types = requestBodyDTOMockery.supportedTypes(metadata);
    assertThat(types.length, is(1));

    Class clazz = (Class) types[0];
    assertEquals(clazz, RequestBody.class);
  }

  private String jsonMockSample() {
    return "{\"s1\":\"s1\"}";
  }

  private interface Providers {
    void mock(@RequestBodyDTO(MockCreator.class) Mock mock);
  }

  @JsonConverter(JacksonSpeaker.class)
  private interface ProvidersJsonConverter {
    void mock(@RequestBodyDTO(MockCreator.class) Mock mock);
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
