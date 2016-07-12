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

import io.victoralbertos.mockery.api.Interceptor;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class GetInterceptorMetadataTest {
  private GetInterceptorMetadata getInterceptorMetadata;

  @Before public void init() {
    getInterceptorMetadata = new GetInterceptorMetadata();
  }

  @Test public void When_No_Interceptor_Annotated_Then_Throw_Exception()
      throws NoSuchMethodException {
    Method method = ProvidersWithoutInterceptor.class.getDeclaredMethod("string");
    try {
      getInterceptorMetadata.with(ProvidersWithoutInterceptor.class, method, null);
      fail();
    } catch (RuntimeException e) {
      String expectedMessage = Messages
          .noInterceptorsFoundOnClass(ProvidersWithoutInterceptor.class);
      assertThat(expectedMessage, is(e.getMessage()));
    }
  }

  @Test public void When_Multiple_Interceptor_Annotated_Then_Throw_Exception()
      throws NoSuchMethodException {
    Method method = ProvidersMultipleInterceptor.class.getDeclaredMethod("string");
    try {
      getInterceptorMetadata.with(ProvidersMultipleInterceptor.class, method, null);
      fail();
    } catch (RuntimeException e) {
      String expectedMessage = Messages
          .multipleInterceptorsFoundOnClass(ProvidersMultipleInterceptor.class);
      assertThat(expectedMessage, is(e.getMessage()));
    }
  }

  @Test public void When_One_Interceptor_Annotated_Then_Get_Interceptor_Metadata()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("string");
    InterceptorMetadata interceptorMetadata =
        getInterceptorMetadata.with(Providers.class, method, null);
    assertNotNull(interceptorMetadata);
  }

  @TestInterceptor
  @Bypass
  private interface ProvidersMultipleInterceptor {
    String string();
  }

  private interface ProvidersWithoutInterceptor {
    String string();
  }

  @TestInterceptor
  private interface Providers {
    String string();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Interceptor(TestBehaviour.class)
  public @interface TestInterceptor {}

  private static class TestBehaviour implements Interceptor.Behaviour<TestInterceptor> {

    @Override public Object onLegalMock(Object mock, Metadata<TestInterceptor> metadata) {
      return null;
    }

    @Override public Object onIllegalMock(AssertionError assertionError, Metadata<TestInterceptor> metadata) {
      return "";
    }

    @Override public void validate(Object candidate, Metadata<TestInterceptor> metadata) throws AssertionError {

    }

    @Override public Object adaptResponse(Object response, Metadata<TestInterceptor> metadata) {
      return null;
    }

    @Override public Type adaptType(Type responseType,
        Metadata<TestInterceptor> metadata) {
      return null;
    }
  }

}
