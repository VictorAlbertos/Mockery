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

import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.junit.Before;
import org.junit.Test;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class GetMockeryMetadataTest {
  private GetMockeryMetadata getMockeryMetadata;

  @Before public void init() {
    getMockeryMetadata = new GetMockeryMetadata();
  }

  @Test public void When_Get_Mockery_Metadata_From_Method_Without_Mockery_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("noMockeryMethod");
    Type type = method.getGenericReturnType();

    try {
      getMockeryMetadata.fromMethod(Providers.class, method, type, null);
      fail();
    } catch (RuntimeException e) {
      String expectedMessage = Messages
          .noMockeryFoundOnMethod(Providers.class, method);
      assertThat(expectedMessage, is(e.getMessage()));
    }
  }

  @Test public void When_Get_Mockery_Metadata_From_Method_With_Multiple_Mockery_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("multipleMockeryMethod");
    Type type = method.getGenericReturnType();

    try {
      getMockeryMetadata.fromMethod(Providers.class, method, type, null);
      fail();
    } catch (RuntimeException e) {
      String expectedMessage = Messages
          .multipleMockeryOnMethodOrParam(Providers.class, method);
      assertThat(expectedMessage, is(e.getMessage()));
    }
  }

  @Test public void When_Get_Mockery_Metadata_From_Method_With_One_Mockery_Get_Mockery_Metadata()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("oneMockeryMethod");
    Type type = method.getGenericReturnType();

    MockeryMetadata mockeryMetadata = getMockeryMetadata
        .fromMethod(Providers.class, method, type, null);
    mockeryMetadata.legal();
    assertNotNull(mockeryMetadata);
  }

  @Test public void When_Get_Mockery_Metadata_From_Param_Without_Mockery_Get_Null()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("mockeryParams",
        String.class, String.class, String.class);

    MockeryMetadata mockeryMetadata = getMockeryMetadata
        .fromParam(Providers.class, method, null, 0);

    assertNull(mockeryMetadata);
  }

  @Test public void When_Get_Mockery_Metadata_From_Param_With_Multiple_Mockery_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("mockeryParams",
        String.class, String.class, String.class);
    try {
      getMockeryMetadata.fromParam(Providers.class, method, null, 1);
      fail();
    } catch (RuntimeException e) {
      String expectedMessage = Messages
          .multipleMockeryOnMethodOrParam(Providers.class, method);
      assertThat(expectedMessage, is(e.getMessage()));
    }
  }

  @Test public void When_Get_Mockery_Metadata_From_Param_With_One_Mockery_Get_Mockery_Metadata()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("mockeryParams",
        String.class, String.class, String.class);

    MockeryMetadata mockeryMetadata = getMockeryMetadata
        .fromParam(Providers.class, method, null, 2);

    assertNotNull(mockeryMetadata);
  }



  private interface Providers {
    String noMockeryMethod();

    @Valid(STRING)
    @DTOArgs(MockString.class)
    String multipleMockeryMethod();

    @Valid(STRING)
    String oneMockeryMethod();

    @Valid(STRING)
    String mockeryParams(String noOne,
        @Valid(STRING) @DTO(MockStringParam.class) String multiple,
        @Valid(STRING) String one);
  }

  private static class MockString implements DTOArgs.Behaviour<String> {
    @Override public String legal(Object[] args) { return null; }

    @Override public void validate(String candidate) throws AssertionError {}
  }

  private static class MockStringParam implements DTO.Behaviour<String> {
    @Override public String legal() { return null; }

    @Override public void validate(String candidate) throws AssertionError {}
  }

}
