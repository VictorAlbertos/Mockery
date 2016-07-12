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

/**
 * Used as an abstraction layer for a more readable experience
 * when running auto generated unit tests.
 */
public interface Robot {
  /**
   * Retrieves a legal value based on the mockery annotation associated with the param.
   * @param paramPosition the position of the param method.
   * @param <T> the {@code type} of the param.
   * @return a legal value for the param.
   */
  <T> T getLegalForParam(int paramPosition);

  /**
   * Retrieves an illegal value based on the mockery annotation associated with the param.
   * @param paramPosition the position of the param method.
   * @param <T> the {@code type} of the param.
   * @return an illegal value for the param.
   */
  <T> T getIllegalForParam(int paramPosition);

  /**
   * Validate the response supplied. If the response does not meet the criteria specified
   * by the using interceptor or ist annotation mockery method, an {@code AssertionError}
   * will be thrown.
   * @param response the response candidate to be validate.
   */
  void validateResponse(Object response) throws AssertionError;
}
