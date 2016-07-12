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

import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import io.victoralbertos.mockery.api.built_in_mockery.DTOJson;
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import io.victoralbertos.mockery.internal.MockeryProxy;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)

/**
 * To annotate another annotations which will be identified as a Mockery annotation, for both params and methods.
 * That way, Mockery API is fully open, allowing to extend it creating new Mockery annotation according to the specifics needs.
 * Actually, {@link DTO}, {@link DTOArgs}, {@link DTOJson}, {@link Optional} and {@link Valid} annotations has been created using this Mockery annotation.
 * To use it, create a new annotation and annotated it with Mockery supplying a valid {@link Mockery.Builder} implementation.
 */
public @interface Mockery {
  Class<? extends Behaviour> value();

  /**
   * Define how the Mockery annotation should behave.
   * @param <A> the type of the associated annotation.
   */
  interface Behaviour<A extends Annotation> {
    /**
     * Given some criteria, returns a legal value which conforms with that criteria.
     * @param metadata the metadata associated with the calling method.
     * @return a legal value.
     */
    Object legal(Metadata<A> metadata);

    /**
     * Given some criteria, returns an illegal value which does not conform with that criteria.
     * @param metadata the metadata associated with the calling method.
     * @return an illegal value.
     */
    Object illegal(Metadata<A> metadata);

    /**
     * Validate if the current object meets some criteria. If not, an AssertionError is thrown.
     * @param metadata the metadata associated with the calling method.
     * @param candidate the object to be checked.
     */
    void validate(Metadata<A> metadata, Object candidate) throws AssertionError;

    /**
     * Return an array containing the supported types for this specific implementation.
     * @param metadata the metadata associated with the calling method.
     */
    Type[] supportedTypes(Metadata<A> metadata);

    /**
     * If true, no unit test asserting for failure would be generated for the param
     * annotated with this mockery annotation.
     */
    boolean isOptional();
  }

  /**
   * The entry point to build the mocked interface.
   * @param <T> the type of the interface to be mocked.
   */
  class Builder<T> {
    private Class<T> mockingClass;

    public Builder<T> mock(Class<T> mockingClass) {
      this.mockingClass = mockingClass;
      return this;
    }

    public T build() {
      return (T) Proxy.newProxyInstance(
          mockingClass.getClassLoader(),
          new Class<?>[]{mockingClass},
          new MockeryProxy(mockingClass));
    }

  }

}
