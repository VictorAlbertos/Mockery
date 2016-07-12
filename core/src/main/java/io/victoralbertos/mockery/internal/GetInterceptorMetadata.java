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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class GetInterceptorMetadata {
  private final InstantiateInterface instantiateInterface;

  public GetInterceptorMetadata() {
    this.instantiateInterface = new InstantiateInterface();
  }

  public InterceptorMetadata with(Class<?> mockingClass, Method method, Object[] args) {
    Annotation[] annotations = mockingClass.getAnnotations();
    List<InterceptorMetadata> interceptorsMetadata = new ArrayList<>();

    for (Annotation annotation : annotations) {
      Interceptor interceptorAnnotation = annotation.annotationType()
          .getAnnotation(Interceptor.class);
      if (interceptorAnnotation == null) continue;

      Metadata metadata = new Metadata(mockingClass,
          method, args, annotation, method.getGenericReturnType());
      Interceptor.Behaviour interceptor = instantiateInterface
          .from(interceptorAnnotation.value());
      interceptorsMetadata.add(new InterceptorMetadata(metadata, interceptor));
    }

    if (interceptorsMetadata.size() > 1) {
      String message = Messages.multipleInterceptorsFoundOnClass(mockingClass);
      throw new RuntimeException(message);
    }

    if (interceptorsMetadata.isEmpty()) {
      String message = Messages.noInterceptorsFoundOnClass(mockingClass);
      throw new RuntimeException(message);
    }

    return interceptorsMetadata.get(0);
  }

}
