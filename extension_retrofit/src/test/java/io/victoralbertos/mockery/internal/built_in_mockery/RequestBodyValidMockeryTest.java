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
import io.victoralbertos.mockery.api.built_in_mockery.RequestBodyValid;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public final class RequestBodyValidMockeryTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private RequestBodyValidMockery requestBodyValidMockery;

  @Before public void init() {
    requestBodyValidMockery = new RequestBodyValidMockery();
  }

  @Test public void When_Call_Legal_Then_Get_RequestBody_Legal()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("string", String.class);
    RequestBodyValid annotation = (RequestBodyValid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyValid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyValidMockery.legal(metadata);
    assertNotNull(requestBody);
    assertThat(requestBody.contentType().toString(), is("text/plain; charset=utf-8"));

    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    RequestBody candidate = RequestBody.create(MediaType.parse("text/plain"),
        buffer.readUtf8());
    requestBodyValidMockery.validate(metadata, candidate);
  }

  @Test public void When_Call_Illegal_Then_Get_RequestBody_Illegal()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("string", String.class);
    RequestBodyValid annotation = (RequestBodyValid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyValid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyValidMockery.illegal(metadata);
    assertNotNull(requestBody);
    assertThat(requestBody.contentType().toString(), is("text/plain; charset=utf-8"));

    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    RequestBody candidate = RequestBody.create(MediaType.parse("text/plain"),
        buffer.readUtf8());
    exception.expect(AssertionError.class);
    requestBodyValidMockery.validate(metadata, candidate);
  }

  @Test public void When_Call_Legal_With_Default_Then_Get_RequestBody_With_Default_Legal()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("stringWithDefault", String.class);
    RequestBodyValid annotation = (RequestBodyValid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyValid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyValidMockery.legal(metadata);
    assertNotNull(requestBody);
    assertThat(requestBody.contentType().toString(), is("text/plain; charset=utf-8"));

    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    assertThat(buffer.readUtf8(), is("s1"));
  }

  @Test public void When_Call_Illegal_With_Default_Then_Get_RequestBody_With_Default_Illegal()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("stringWithDefault", String.class);
    RequestBodyValid annotation = (RequestBodyValid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyValid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyValidMockery.illegal(metadata);
    assertNotNull(requestBody);
    assertThat(requestBody.contentType().toString(), is("text/plain; charset=utf-8"));

    Buffer buffer = new Buffer();
    requestBody.writeTo(buffer);
    assertThat(buffer.readUtf8(), is("s1"));
  }

  @Test public void When_Pass_Validation_Then_Do_Not_Throw_Assertion_Error()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("string", String.class);
    RequestBodyValid annotation = (RequestBodyValid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyValid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    RequestBody requestBody = (RequestBody) requestBodyValidMockery.legal(metadata);
    requestBodyValidMockery.validate(metadata, requestBody);
  }

  @Test public void When_Not_Pass_Validation_Then_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("string", String.class);
    RequestBodyValid annotation = (RequestBodyValid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyValid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    RequestBody requestBodySeed = RequestBody.create(MediaType.parse("text/plain"), "");
    exception.expect(AssertionError.class);
    requestBodyValidMockery.validate(metadata, requestBodySeed);
  }

  @Test public void When_Support_Type_Retrieved_Then_Is_Request_Body()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("string", String.class);
    RequestBodyValid annotation = (RequestBodyValid) method.getParameterAnnotations()[0][0];
    Type type = method.getGenericParameterTypes()[0];

    Metadata<RequestBodyValid> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Type[] types = requestBodyValidMockery.supportedTypes(metadata);
    assertThat(types.length, is(1));

    Class clazz = (Class) types[0];
    assertEquals(clazz, RequestBody.class);
  }


  private interface Providers {
    String string(@RequestBodyValid(STRING) String s1);
    String stringWithDefault(@RequestBodyValid(value = STRING, legal = "s1", illegal = "s1") String s1);
  }


}
