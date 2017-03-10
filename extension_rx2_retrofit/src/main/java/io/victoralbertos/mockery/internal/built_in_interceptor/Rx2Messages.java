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

package io.victoralbertos.mockery.internal.built_in_interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class Rx2Messages {

  static String illegalMethodReturnType(Class mockingClass, Method method, Type gotType) {
    String message = "When checking return type of method %s#%s \n"
        + "%s was found. But only Single<T> and Completable are supported as method return type.\n"
        + "To fix it, change the return type to Single<T> or Completable.\n";

    return String.format(message, mockingClass.getSimpleName(),
        method.getName(), gotType);
  }

  static String illegalMockType(Class mockingClass, Method method) {
    String message = "When checking mock value of method %s#%s \n"
        + "Single<T> was found. But when using RxRetrofit as interceptor Single<T> is not allowed as mock value.\n"
        + "That's because RxRetrofit interceptor handles itself all the cases related with Single<T>/Single<Response> type.\n"
        + "To fix it, change the mock value to a meaningful DTO, without wrapping it inside an Single<T>/Single<Response>.\n";

    return String.format(message, mockingClass.getSimpleName(),
        method.getName());
  }

}
