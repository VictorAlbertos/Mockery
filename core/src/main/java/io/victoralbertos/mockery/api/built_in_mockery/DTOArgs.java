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
import io.victoralbertos.mockery.internal.built_in_mockery.DTOMockeryArgs;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
@Mockery(DTOMockeryArgs.class)
/**
 * Same as {@link DTO}, but only allowed to be annotated on methods.
 * This limitation allow us to access the associated array of objects containing the values of the
 * arguments passed in the method invocation.
 * To use it, decorate the param with this annotation, supplying a valid {@link DTOArgs.Behaviour} implementation.
 */
public @interface DTOArgs {
  /**
   * The {@code class} of the implementation of {@link Behaviour}
   */
  Class<? extends DTOArgs.Behaviour> value();

  /**
   * @param <T> The DTO {@code type}
   */
  interface Behaviour<T> {
    /**
     * Return a legal instance of the DTO {@code type}.
     */
    T legal(Object[] args);

    /**
     * Validate the object and throw an AssertionError in case it does not fulfill the requirements.
     */
    void validate(T candidate) throws AssertionError;
  }

}
