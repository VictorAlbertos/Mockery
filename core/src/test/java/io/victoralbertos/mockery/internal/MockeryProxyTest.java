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

package io.victoralbertos.mockery.internal;

import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class MockeryProxyTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private final static String RESPONSE = "response";
  private final static String RESPONSE_ERROR = "response_error";
  private final static AssertionError ASSERTION_ERROR = new AssertionError(RESPONSE_ERROR);
  private MockeryProxy mockeryProxy;

  @Before public void init() {
    mockeryProxy = new MockeryProxy(Providers.class);
  }

  @Test public void When_Method_Type_Is_Not_Supported_Then_Get_Exception()
      throws Throwable {
    Method method = Providers.class.getMethod("notSupportedTypeMethod");
    Object[] args = {};

    exception.expect(RuntimeException.class);
    mockeryProxy.invoke(null, method, args);
  }

  @Test public void When_Param_Type_Is_Not_Supported_Then_Get_Exception()
      throws Throwable {
    Method method = Providers.class.getMethod("notSupportedTypeParam", Object.class);
    Object[] args = {};

    exception.expect(RuntimeException.class);
    mockeryProxy.invoke(null, method, args);
  }

  @Test public void When_Method_Has_Not_Params_Then_Get_Response()
      throws Throwable {
    Method method = Providers.class.getMethod("noParams");
    Object[] args = {};

    String response = (String) mockeryProxy.invoke(null, method, args);
    assertThat(response, is(RESPONSE));
  }

  @Test public void When_No_Mockery_Params_Then_Get_Exception()
      throws Throwable {
    Method method = Providers.class.getMethod("noMockery", String.class, Integer.class);
    Object[] args = {"s1", 1};

    try {
      mockeryProxy.invoke(null, method, args);
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking method Providers#noMockery \n"
          + "No @Mockery annotation for param with position 2 was found.\n"
          + "To fix it, annotate this param with one.\n"));
    }
  }

  @Test public void When_Mockery_Params_Pass_Validation_Then_Get_Response()
      throws Throwable {
    Method method = Providers.class.getMethod("mockeryPass", String.class, String.class);
    Object[] args = {"s1", "s2"};

    String response = (String) mockeryProxy.invoke(null, method, args);
    assertThat(response, is(RESPONSE));
  }

  @Test public void When_Mockery_Params_Do_Not_Pass_Validation_Then_Get_Assertion_Error()
      throws Throwable {
    Method method = Providers.class.getMethod("mockeryNotPass", String.class, String.class);
    Object[] args = {"s1", "s2"};

    try {
      mockeryProxy.invoke(null, method, args);
      fail();
    } catch (AssertionError e) {
      assertThat(e.getMessage(), is(RESPONSE_ERROR));
    }
  }

  @Bypass
  private interface Providers {
    @DTOArgs(DTOResponse.class)
    String noParams();

    @DTOArgs(DTOResponse.class)
    String noMockery(@DTO(DTOParamPass.class) String s1, Integer i1);

    @DTOArgs(DTOResponse.class)
    String mockeryPass(@DTO(DTOParamPass.class) String s1,
        @DTO(DTOParamPass.class) String s2);

    @DTOArgs(DTOResponse.class)
    String mockeryNotPass(@DTO(DTOParamNotPass.class) String s1,
        @DTO(DTOParamNotPass.class) String s2);

    @Valid(STRING)
    Object notSupportedTypeMethod();

    @Valid(STRING)
    String notSupportedTypeParam(@Valid(STRING) Object notSupportedType);
  }

  private static class DTOResponse implements DTOArgs.Behaviour<String> {

    @Override public void validate(String candidate) throws AssertionError {}

    @Override public String legal(Object[] args) { return RESPONSE; }

  }

  private static class DTOParamPass implements DTO.Behaviour<String> {

    @Override public void validate(String candidate) throws AssertionError {}

    @Override public String legal() { return null; }

  }

  private static class DTOParamNotPass implements DTO.Behaviour<String> {

    @Override public void validate(String candidate) throws AssertionError {
      throw ASSERTION_ERROR;
    }

    @Override public String legal() { return null; }

  }

}
