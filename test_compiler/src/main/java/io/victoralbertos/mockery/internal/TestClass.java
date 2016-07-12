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
import com.squareup.javapoet.TypeName;
import java.util.List;
import javax.lang.model.element.Element;

final class TestClass {
  final ClassName className;
  final Element element;
  final List<Method> methods;

  TestClass(ClassName className, Element element,
      List<Method> methods) {
    this.className = className;
    this.element = element;
    this.methods = methods;
  }

  static class Method {
    final String name;
    final Element element;
    final TypeName returnType;
    final List<Param> params;
    final boolean hasMockery;

    Method(String name, Element element, TypeName returnType,
        List<Param> params, boolean hasMockery) {
      this.name = name;
      this.element = element;
      this.returnType = returnType;
      this.params = params;
      this.hasMockery = hasMockery;
    }

    static class Param {
      final String name;
      final Element element;
      final int positionOnMethod;
      final TypeName type;
      final boolean hasMockery;
      final boolean isOptional;


      Param(String name, Element element, int positionOnMethod, TypeName type,
          boolean hasMockery, boolean isOptional) {
        this.name = name;
        this.element = element;
        this.positionOnMethod = positionOnMethod;
        this.type = type;
        this.hasMockery = hasMockery;
        this.isOptional = isOptional;
      }

    }

  }

}
