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

package io.victoralbertos.mockery.api.built_in_mockery;

import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.internal.built_in_mockery.EnumMockery;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(PARAMETER)
@Mockery(EnumMockery.class)
/**
 * Decorate a param with this annotation to supply mocking and validation specs for a serie of enumerated values.
 * The supported types as listed on {@link EnumMockery#supportedTypes(Metadata)}
 * To use it, decorate the param with this annotation, supplying an array of string with the legal values.
 */
public @interface Enum {
  /**
   * The string array containing the valid values.
   */
  String[] value();

  /**
   * If set, it will be the legal value provided from {@link EnumMockery#legal(Metadata)}
   */
  String legal() default "";

  /**
   * If set, it will be the illegal value provided from {@link EnumMockery#illegal(Metadata)}
   */
  String illegal() default "";
}
