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

package io.victoralbertos.mockery.internal.built_in_mockery;

import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public final class DTOArgsMockeryTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private DTOMockeryArgs dtoMockeryArgs;

  @Before public void init() {
    dtoMockeryArgs = new DTOMockeryArgs();
  }

  @Test public void When_Call_Legal_Then_Get_Legal()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTOArgs");
    DTOArgs annotation = method.getAnnotation(DTOArgs.class);
    Type type = method.getGenericReturnType();
    Object[] args = {DTOArgsPass.class.getName()};

    Metadata<DTOArgs> metadata = new Metadata<>(Providers.class,
        method, args, annotation, type);

    Mock mock = (Mock) dtoMockeryArgs.legal(metadata);
    assertThat(mock.s1, is(DTOArgsPass.class.getName()));
  }

  @Test public void When_Call_Illegal_Then_Get_Null()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTOArgs");
    DTOArgs annotation = method.getAnnotation(DTOArgs.class);
    Type type = method.getGenericReturnType();

    Metadata<DTOArgs> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Object illegal = dtoMockeryArgs.illegal(metadata);
    assertNull(illegal);
  }

  private interface Providers {
    @DTOArgs(DTOArgsPass.class)
    String DTOArgs();
  }

  private static class Mock {
    private final String s1;

    public Mock(String s1) {
      this.s1 = s1;
    }
  }

  static class DTOArgsPass implements DTOArgs.Behaviour<Mock> {

    @Override public Mock legal(Object[] args) {
      return new Mock((String) args[0]);
    }

    @Override public void validate(Mock candidate) throws AssertionError {
      if (!candidate.s1.equals(DTOArgsPass.class.getName())) throw new AssertionError();
    }

  }

}
