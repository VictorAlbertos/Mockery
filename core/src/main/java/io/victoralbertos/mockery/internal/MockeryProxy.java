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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class MockeryProxy implements InvocationHandler {
  private final Class<?> mockingClass;
  private final GetInterceptorMetadata getInterceptorMetadata;
  private final GetMockeryMetadata getMockeryMetadata;
  private final CheckSupportType checkSupportType;

  public MockeryProxy(Class<?> mockingClass) {
    this.mockingClass = mockingClass;
    this.getInterceptorMetadata = new GetInterceptorMetadata();
    this.getMockeryMetadata = new GetMockeryMetadata();
    this.checkSupportType = new CheckSupportType();
  }

  @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getDeclaringClass() == Object.class) {
      return method.invoke(this, args);
    }

    InterceptorMetadata interceptor = getInterceptorMetadata.
        with(mockingClass, method, args);

    try {
      applyValidationsToParams(method, args);

      Type adaptedTypeMethod = interceptor
          .adaptType(method.getGenericReturnType());

      MockeryMetadata mockery = getMockeryMetadata
          .fromMethod(mockingClass, method, adaptedTypeMethod, args);

      checkSupportType.from(mockingClass, method,
          mockery, adaptedTypeMethod);

      Object response = mockery.legal();
      return interceptor.onLegalMock(response);
    } catch (AssertionError e) {
      return interceptor.onIllegalMock(e);
    }
  }

  private void applyValidationsToParams(Method method, Object[] args) {
    Type[] typeParams = method.getGenericParameterTypes();

    for (int i = 0; i < typeParams.length; i++) {
      Type typeParam = typeParams[i];

      MockeryMetadata mockery = getMockeryMetadata
          .fromParam(mockingClass, method, args, i);

      if (mockery == null) {
        String message = Messages.noMockeryFoundOnParam(mockingClass, method, i);
        throw new RuntimeException(message);
      }

      checkSupportType.from(mockingClass, method,
          mockery, typeParam);

      Object valueParam = args[i];
      mockery.validate(valueParam);
    }
  }

}
