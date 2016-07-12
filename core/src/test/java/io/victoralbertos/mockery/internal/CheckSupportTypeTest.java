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

import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.junit.Before;
import org.junit.Test;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class CheckSupportTypeTest {
  private CheckSupportType checkSupportTypeUT;
  private GetMockeryMetadata getMockeryMetadata;

  @Before public void init() {
    checkSupportTypeUT = new CheckSupportType();
    getMockeryMetadata = new GetMockeryMetadata();
  }

  @Test public void When_Get_Mockery_Metadata_From_Param_But_Param_Type_Is_Not_Supported_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("notSupportedTypeParam", Object.class);
    Type type = method.getGenericParameterTypes()[0];

    try {
      MockeryMetadata mockery = getMockeryMetadata
          .fromParam(Providers.class, method, null, 0);
      checkSupportTypeUT.from(Providers.class, method, mockery, type);
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking ValidMockery on method Providers#notSupportedTypeParam \n"
          + " an attempt to use it with class java.lang.Object was found. But it is not a supported type for ValidMockery.\n"
          + "To fix it, use ValidMockery with: [double, class java.lang.Double, float, class java.lang.Float, int, class java.lang.Integer, long, class java.lang.Long, class java.lang.String, class java.lang.Character].\n"));
    }
  }

  @Test public void When_Get_Mockery_Metadata_From_Param_And_Type_Is_Supported_Then_Not_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("supportedTypeParam", String.class);
    Type type = method.getGenericParameterTypes()[0];

    MockeryMetadata mockery = getMockeryMetadata
        .fromParam(Providers.class, method, null, 0);
    checkSupportTypeUT.from(Providers.class, method, mockery, type);
  }

  @Test public void When_Get_Mockery_Metadata_From_Method_But_Method_Type_Is_Not_Supported_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("notSupportedTypeMethod");
    Type type = method.getGenericReturnType();
    try {
      MockeryMetadata mockery =  getMockeryMetadata
          .fromMethod(Providers.class, method, type, null);
      checkSupportTypeUT.from(Providers.class, method, mockery, type);
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking ValidMockery on method Providers#notSupportedTypeMethod \n"
          + " an attempt to use it with class java.lang.Object was found. But it is not a supported type for ValidMockery.\n"
          + "To fix it, use ValidMockery with: [double, class java.lang.Double, float, class java.lang.Float, int, class java.lang.Integer, long, class java.lang.Long, class java.lang.String, class java.lang.Character].\n"));
    }
  }

  @Test public void When_Get_Mockery_Metadata_From_Method_And_Type_Is_Supported_Then_Not_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getMethod("supportedTypeMethod");
    Type type = method.getGenericReturnType();
    MockeryMetadata mockery =  getMockeryMetadata
        .fromMethod(Providers.class, method, type, null);
    checkSupportTypeUT.from(Providers.class, method, mockery, type);
  }

  private interface Providers {
    @Valid(STRING)
    Object notSupportedTypeMethod();

    @Valid(STRING)
    String supportedTypeMethod();

    @Valid(STRING)
    String notSupportedTypeParam(@Valid(STRING) Object o1);

    @Valid(STRING)
    String supportedTypeParam(@Valid(STRING) String s1);
  }

}
