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

import com.squareup.javapoet.ClassName;

final class CMessages {

  static String noMockeryFoundOnMethod(ClassName className, String methodName) {
    String message = "When checking %s#%s @Interceptor annotation on class was found.\n"
        + "But method %s is not annotated with @Mockery.\n"
        + "To fix it, annotate %s with one @Mockery.\n";

    return String.format(message, className, methodName, methodName, methodName);
  }

  static String noMockeryFoundOnParam(ClassName className, String methodName, String paramName) {
    String message = "When checking method %s#%s \n"
        + "No @Mockery annotation for param %s was found.\n"
        + "To fix it, annotate this param with one.\n";

    return String.format(message, className, methodName, paramName);
  }

  static String noSupportedOverloadingMethod(ClassName className, String methodName) {
    String message = "When checking %s 2 methods with name '%s' were found.\n"
        + "But method overloading is not supported by Mockery interfaces.\n"
        + "To fix it, rename one of the previous methods.\n";

    return String.format(message, className, methodName);
  }

  static String codeGenerateWarning() {
    return "Generated code from Mockery. Don't modify. Or modify. It doesn't matter.";
  }

}
