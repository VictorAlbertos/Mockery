/*
 * Copyright 2017 Victor Albertos
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

package io.victoralbertos.mockery.internal.built_in_mockery;

import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.api.built_in_mockery.NoDTO;
import java.lang.reflect.Type;

/**
 * To provide an 'empty' implementation of Mockery.Behaviour
 */
public final class NoDTOMockery implements Mockery.Behaviour<NoDTO> {
  @Override public Object legal(Metadata<NoDTO> metadata) {
    return null;
  }

  @Override public Object illegal(Metadata<NoDTO> metadata) {
    return null;
  }

  @Override public void validate(Metadata<NoDTO> metadata, Object candidate) throws AssertionError {

  }

  @Override public Type[] supportedTypes(Metadata<NoDTO> metadata) {
    return new Type[] {Object.class};
  }

  @Override public boolean isOptional() {
    return false;
  }
}
