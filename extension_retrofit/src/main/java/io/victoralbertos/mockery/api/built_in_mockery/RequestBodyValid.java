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

import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.internal.built_in_mockery.RequestBodyValidMockery;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import okhttp3.RequestBody;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(PARAMETER)
@Mockery(RequestBodyValidMockery.class)
/**
 * Same as {@link Valid}, but this mockery wraps and unwraps the value object inside a {@link RequestBody}.
 * To use it, decorate the {@link RequestBody} param with this annotation, supplying one valid regex fom the built-in {@link Valid.Template}.
 */
public @interface RequestBodyValid {
  /**
   * Same as {@link Valid#value()}
   */
  String value();

  /**
   * Same as {@link Valid#legal()}
   */
  String legal() default "";

  /**
   * Same as {@link Valid#illegal()}
   */
  String illegal() default "";
}