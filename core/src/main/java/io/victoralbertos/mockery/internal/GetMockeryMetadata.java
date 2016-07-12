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

import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.Mockery;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class GetMockeryMetadata {
  private final InstantiateInterface instantiateInterface;

  public GetMockeryMetadata() {
    this.instantiateInterface = new InstantiateInterface();
  }

  public MockeryMetadata fromMethod(Class<?> mockingClass, Method method, Type typeMethod, Object[] args) {
    Annotation[] annotationsMethod = method.getAnnotations();

    MockeryMetadata mockery = mockeryMetadata(mockingClass, method, args,
        typeMethod, annotationsMethod);

    if (mockery == null) {
      String message = Messages
          .noMockeryFoundOnMethod(mockingClass, method);
      throw new RuntimeException(message);
    }

    return mockery;
  }

  public MockeryMetadata fromParam(Class<?> mockingClass, Method method, Object[] args, int paramPosition) {
    Type typeParam = method.getGenericParameterTypes()[paramPosition];
    Annotation[] annotationsParam = method.getParameterAnnotations()[paramPosition];

    MockeryMetadata mockery = mockeryMetadata(mockingClass, method, args,
        typeParam, annotationsParam);

    return mockery;
  }

  private MockeryMetadata mockeryMetadata(Class<?> mockingClass, Method method, Object[] args, Type type,
      Annotation[] annotations) {
    List<MockeryMetadata> mockeriesMetadata = new ArrayList<>();

    for (Annotation annotation : annotations) {
      Mockery mockeryAnnotation = annotation.annotationType()
          .getAnnotation(Mockery.class);
      if (mockeryAnnotation == null) continue;

      Metadata metadata = new Metadata<>(mockingClass, method, args, annotation, type);
      Mockery.Behaviour mockery = instantiateInterface.from(mockeryAnnotation.value());
      mockeriesMetadata.add(new MockeryMetadata(metadata, mockery));
    }

    if (mockeriesMetadata.isEmpty()) return null;

    if (mockeriesMetadata.size() > 1) {
      String message = Messages
          .multipleMockeryOnMethodOrParam(mockingClass, method);
      throw new IllegalArgumentException(message);
    }

    return mockeriesMetadata.get(0);
  }

}
