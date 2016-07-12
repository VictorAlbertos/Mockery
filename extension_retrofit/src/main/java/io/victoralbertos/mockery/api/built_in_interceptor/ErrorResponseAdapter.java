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

/**
 * Accommodate the error message of a failure Response to mimic the expected payload returned by the server.
 * To use it, pass the implementation to {@link Retrofit#errorResponseAdapter}.
 */
public interface ErrorResponseAdapter {
  /**
   * Adapt error messages to suit them for specific parsing needs.
   * For example:
   * <pre>
   * {@code
   * String json = "{'message':'%s'}";
   * String.format(json, error);
   * }
   * </pre>
   * @param error the original message with the error.
   * @return the adapted one.
   */
  String adapt(String error);
}
