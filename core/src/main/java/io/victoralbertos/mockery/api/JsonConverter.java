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

package io.victoralbertos.mockery.api;

import io.victoralbertos.jolyglot.JolyglotGenerics;
import io.victoralbertos.mockery.api.built_in_mockery.DTOJson;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Set a {@link JolyglotGenerics} implementation to allow Mockery to convert from object to json and vice versa.
 * This annotation is mandatory to be used if {@link DTOJson} is present on any method param.
 * To use it, decorate the interface with this annotation, supplying a built-in {@link JolyglotGenerics} implementation.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface JsonConverter {
  /**
   * The {@code class} of the implementation of {@link JolyglotGenerics}
   */
  Class<? extends JolyglotGenerics> value();
}
