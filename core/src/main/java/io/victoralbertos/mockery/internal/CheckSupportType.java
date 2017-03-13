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

public final class CheckSupportType {

  public void from(Class mockingClass, Method method,
      MockeryMetadata mockery, Type candidate) {
    Type[] supportedTypes = mockery.supportedTypes();

    for (Type supportedType : supportedTypes) {
      if (supportedType.toString().replace("? extends ", "")
          .equals(candidate.toString())) {
        return;
      }
    }

    String message = Messages.notSupportedTypeForMockery(mockingClass, method,
        mockery, candidate, supportedTypes);
    throw new RuntimeException(message);
  }
}
