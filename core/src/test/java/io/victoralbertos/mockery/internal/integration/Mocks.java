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

package io.victoralbertos.mockery.internal.integration;

import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import java.util.Arrays;
import java.util.List;

public final class Mocks {

  public static class MockModel implements DTO.Behaviour<Model> {
    @Override public Model legal() {
      return new Model(Model.class.getName());
    }

    @Override public void validate(Model candidate) throws AssertionError {
      if (candidate == null) throw new AssertionError("model can not be null");
      if (!candidate.getS1().equals(Model.class.getName())) {
        throw new AssertionError("model.s1 must be equal to " + Model.class.getName());
      }
    }
  }

  public static class MockModels implements DTO.Behaviour<List<Model>> {
    @Override public List<Model> legal() {
      return Arrays.asList(new Model(Model.class.getName()));
    }

    @Override public void validate(List<Model> candidate) throws AssertionError {
      if (candidate == null) throw new AssertionError("models can not be null");
      if (candidate.size() != 1) throw new AssertionError("models must has one entry");
      if (!candidate.get(0).getS1().equals(Model.class.getName())) {
        throw new AssertionError("model.s1 must be equal to " + Model.class.getName());
      }
    }
  }
}
