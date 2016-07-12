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
import java.util.ArrayList;
import java.util.List;

public final class Messages {

  static String multipleInterceptorsFoundOnClass(Class<?> aClass) {
    String message = "When checking %s multiple @Interceptor annotated were found.\n"
        + "To fix it, keep %s annotated with only one @Interceptor.\n";

    return String.format(message, aClass.getSimpleName(),
        aClass.getSimpleName());
  }

  static String noInterceptorsFoundOnClass(Class<?> aClass) {
    String message = "When checking %s no @Interceptor annotated was found.\n"
        + "To fix it, annotate %s with one of the built-in @Interceptor or create a custom one.\n";

    return String.format(message, aClass.getSimpleName(),
        aClass.getSimpleName());
  }

  public static String noMethodFoundForMethodName(Class<?> aClass, String methodName) {
    String message = "When checking %s no method with name %s was found.\n";
    return String.format(message, aClass.getSimpleName(), methodName);
  }

  static String multipleMockeryOnMethodOrParam(Class mockingClass, Method method) {
    String message = "When checking %s#%s \n"
        + "Multiple @Mockery annotations were found.\n"
        + "To fix it, keep method annotated with only one.\n";

    return String.format(message, mockingClass.getSimpleName(), method.getName());
  }

  static String noMockeryFoundOnMethod(Class mockingClass, Method method) {
    String message = "When checking method %s#%s \n"
        + "No @Mockery annotation for return method was found.\n"
        + "To fix it, annotate method with one.\n";

    return String.format(message, mockingClass.getSimpleName(), method.getName());
  }

  static String noMockeryFoundOnParam(Class mockingClass, Method method, int positionParam) {
    String message = "When checking method %s#%s \n"
        + "No @Mockery annotation for param with position %s was found.\n"
        + "To fix it, annotate this param with one.\n";

    return String.format(message, mockingClass.getSimpleName(), method.getName(), ++positionParam);
  }

  static String notSupportedTypeForMockery(Class mockingClass, Method method, MockeryMetadata mockeryMetadata,
      Type illegalType, Type[] supportedTypes) {
    List<String> supportedTypesNames = new ArrayList<>(supportedTypes.length);

    for (Type type : supportedTypes) {
      supportedTypesNames.add(type.toString());
    }

    String message = "When checking %s on method %s#%s \n "
        + "an attempt to use it with %s was found. But it is not a supported type for %s.\n"
        + "To fix it, use %s with: %s.\n";

    return String.format(message, mockeryMetadata, mockingClass.getSimpleName(), method.getName(),
        illegalType, mockeryMetadata, mockeryMetadata,
        supportedTypesNames);
  }

  public static String noJsonConverterFound(Class mockingClass, Method method,
      Class annotationRequiresConverter) {
    String message = "When checking method method %s#%s \n"
        + "an attempt to use %s were found, but %s is not annotated with @JsonConverter.\n"
        + "To fix it, annotate %s with @JsonConverter supplying a JolyglotGenerics implementation.\n";

    return String.format(message, mockingClass.getSimpleName(), method.getName(),
        annotationRequiresConverter, mockingClass.getSimpleName(),
        mockingClass.getSimpleName());
  }

  public static String emptyEnumArray(Class mockingClass, Method method) {
    String message = "When checking method %s#%s \n"
        + "An empty array was found as value for @Enum annotation.\n"
        + "To fix it, at least one value has to be specified.\n";
    return String.format(message, mockingClass.getSimpleName(), method.getName());
  }

}
