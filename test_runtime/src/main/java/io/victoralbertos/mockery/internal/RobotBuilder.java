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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class RobotBuilder {

  public static Build test(Class<?> providersClass) {
    return new Build(providersClass);
  }

  public static class Build {
    private final Class<?> providersClass;
    private String nameMethod;

    private Build(Class<?> providersClass) {
      this.providersClass = providersClass;
    }

    public Build onMethod(String nameMethod) {
      this.nameMethod = nameMethod;
      return this;
    }

    public Robot build() {
      return new RealRobot(providersClass, nameMethod);
    }

  }

  private static class RealRobot implements Robot {
    private final Class<?> providersClass;
    private Method method;
    private final InterceptorMetadata interceptorMetadata;
    private final GetMockeryMetadata mockeryMetadata;
    private final CheckSupportType checkSupportType;

    private RealRobot(Class<?> providersClass, String nameMethod) {
      this.providersClass = providersClass;

      for (Method method : providersClass.getDeclaredMethods()) {
        if (method.getName().equals(nameMethod))
          this.method = method;
      }

      if (method == null) {
        String message = Messages
            .noMethodFoundForMethodName(providersClass, nameMethod);
        throw new RuntimeException(message);
      }

      providersClass.getDeclaredMethods();
      this.interceptorMetadata = new GetInterceptorMetadata()
          .with(providersClass, method, null);
      this.mockeryMetadata = new GetMockeryMetadata();
      this.checkSupportType = new CheckSupportType();
    }

    @Override public <T> T getLegalForParam(int paramPosition) {
      Type type = method.
          getGenericParameterTypes()[paramPosition];

      MockeryMetadata mockery = mockeryMetadata
          .fromParam(providersClass, method, null, paramPosition);

      checkSupportType.from(providersClass, method,
          mockery, type);

      return (T) mockery.legal();
    }

    @Override public <T> T getIllegalForParam(int paramPosition) {
      Type type = method.
          getGenericParameterTypes()[paramPosition];

      MockeryMetadata mockery = mockeryMetadata
          .fromParam(providersClass, method, null, paramPosition);

      checkSupportType.from(providersClass, method,
          mockery, type);

      return (T) mockery.illegal();
    }

    @Override public void validateResponse(Object response) {
      interceptorMetadata.validate(response);

      Type typeMethod = method.getGenericReturnType();
      Type adaptedTypeMethod = interceptorMetadata.adaptType(typeMethod);
      Object adaptedResponse = interceptorMetadata.adaptResponse(response);

      MockeryMetadata mockeryMethod = mockeryMetadata
          .fromMethod(providersClass, method, adaptedTypeMethod, null);

      checkSupportType.from(providersClass, method,
          mockeryMethod, adaptedTypeMethod);

      mockeryMethod.validate(adaptedResponse);
    }

  }

}
