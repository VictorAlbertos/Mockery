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

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
/**
 * Provide the entry point to perform global customizations on Mockery behaviour, allowing to intercept any response
 * to adapt it to a set of specifications.
 * Mockery has 2 built-in interceptors -@Retrofit and @RxRetrofit- responsible for adapting the agnostic core of Mockery
 * to Retrofit and RxJava-Retrofit networking libraries, respectively.
 * This annotation is meant to be used for extending the core of Mockery to support new networking libraries.
 */
public @interface Interceptor {
  Class<? extends Behaviour> value();

  interface Behaviour<A extends Annotation> {
    /**
     * Called every time a call to the proxy interface happens,
     * and all mockery params had been fulfilled.
     * Here is where the response tuning should be performed.
     * @param mock the object being mocked.
     * @param metadata the metadata associated with the calling method.
     * @return the response accommodate to the idiosyncrasy of the underlying network library.
     */
    Object onLegalMock(Object mock, Metadata<A> metadata);

    /**
     * Called every time a call to the proxy interface happens,
     * and one of mockery params thrown an exception when it was been validated.
     * Here is where the response tuning should be performed.
     * @param assertionError the assertion error resulting from an illegal value.
     * @param metadata the metadata associated with the calling method.
     * @return the response accommodate to the idiosyncrasy of the underlying network library.
     */
    Object onIllegalMock(AssertionError assertionError, Metadata<A> metadata);

    /**
     * Called in auto generated unit tests to determine if the response dispatched by the server
     * meets the requirements to be qualified as a legal one.
     * If it isn't, then an assertion error is thrown.
     * @param candidate the candidate to check if it qualifies as a legal one.
     */
    void validate(Object candidate, Metadata<A> metadata) throws AssertionError;

    /**
     * Unwrap the value from its networking encapsulation object.
     * For instance, when using an observable, blocking it is the only way to consume its value on an synchronous way.
     * Called in auto generated unit tests to unwrap the DTO response prior to validate it.
     * @param response the object to unwrap.
     * @param metadata the metadata associated with the calling method.
     * @return the response accommodate to the idiosyncrasy of the underlying network library.
     */
    Object adaptResponse(Object response, Metadata<A> metadata);

    /**
     * Given a type, adapt it to the expected one (the DTO expected one indeed).
     * Used prior to check the integrity between a mockery annotation and the method return type.
     * @param responseType the type to adapt.
     * @param metadata the metadata associated with the calling method.
     * @return the type accommodate to the idiosyncrasy of the underlying network library.
     */
    Type adaptType(Type responseType, Metadata<A> metadata);
  }

}
