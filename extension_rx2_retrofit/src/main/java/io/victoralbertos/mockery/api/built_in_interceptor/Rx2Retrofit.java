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

package io.victoralbertos.mockery.api.built_in_interceptor;

import io.reactivex.Single;
import io.victoralbertos.mockery.api.Interceptor;
import io.victoralbertos.mockery.internal.built_in_interceptor.ByPassErrorResponseAdapter;
import io.victoralbertos.mockery.internal.built_in_interceptor.Rx2RetrofitInterceptor;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Interceptor(Rx2RetrofitInterceptor.class)

/**
 * An {@link Interceptor} to supply a canonical behaviour expected by any http-server when handling responses using Retrofit, both success and failure ones.
 * Every declared method has to return a {@link Single} object and any DTO annotated has to limit its creation scope to the DTO itself,
 * that’s mean that this interceptor is responsible for creating and managing the {@link Single} objects, not the client.
 * To use it, annotate Retrofit interface with this RxRetrofit annotation.
 */
public @interface Rx2Retrofit {

  /**
   * Set the network round trip delay in milliseconds
   * The amount must be positive value.
   * By default, network calls will take 2 seconds.
   */
  long delay() default 2000;

  /**
   * Set the percentage of calls to fail.
   * Failure percentage must be between 0 and 100.
   * By default, 3% of network calls will fail.
   */
  int failurePercent() default 3;

  /**
   * Set the plus-or-minus variancePercentage percentage of the network round trip delay.
   * Variance percentage must be between 0 and 100.
   * By default, network delay varies by ±40%.
   */
  int variancePercentage() default 40;

  /**
   * Adapts the error message. By default {@link ByPassErrorResponseAdapter} is used.
   * @see ErrorResponseAdapter
   */
  Class<? extends ErrorResponseAdapter> errorResponseAdapter() default ByPassErrorResponseAdapter.class;
}
