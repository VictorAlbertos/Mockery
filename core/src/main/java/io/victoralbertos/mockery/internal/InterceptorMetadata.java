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

import io.victoralbertos.mockery.api.Interceptor;
import io.victoralbertos.mockery.api.Metadata;
import java.lang.reflect.Type;

public final class InterceptorMetadata {
  private final Metadata metadata;
  private final Interceptor.Behaviour interceptor;

  public InterceptorMetadata(Metadata metadata, Interceptor.Behaviour interceptor) {
    this.metadata = metadata;
    this.interceptor = interceptor;
  }

  public Object onLegalMock(Object mock) {
    return interceptor.onLegalMock(mock, metadata);
  }

  public Object onIllegalMock(AssertionError assertionError) {
    return interceptor.onIllegalMock(assertionError, metadata);
  }

  public void validate(Object candidate) throws AssertionError {
    interceptor.validate(candidate, metadata);
  }

  public Object adaptResponse(Object response) {
    return interceptor.adaptResponse(response, metadata);
  }

  public Type adaptType(Type responseType) {
    return interceptor.adaptType(responseType, metadata);
  }
}

