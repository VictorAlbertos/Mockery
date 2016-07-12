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

import io.victoralbertos.mockery.api.Order;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public final class OrderedRunnerTest {
  private OrderedRunner orderedRunner;

  @Before public void init() throws InitializationError {
    orderedRunner = new OrderedRunner(Providers.class);
  }

  @Test public void When_Test_Methods_Are_Annotated_With_Order_Then_They_Are_Sorted_By_Them() {
    List<FrameworkMethod> frameworkMethods = orderedRunner.computeTestMethods();

    assertThat(frameworkMethods.size(), is(4));
    assertThat(frameworkMethods.get(0).getName(), is("test1"));
    assertThat(frameworkMethods.get(1).getName(), is("test2"));
    assertThat(frameworkMethods.get(2).getName(), is("test3"));
    assertThat(frameworkMethods.get(3).getName(), is("test4"));
  }

  @RunWith(OrderedRunner.class)
  public static class Providers {

    @Order(3)
    @Test public void test3() {}

    @Order(4)
    @Test public void test4() {}

    @Order(1)
    @Test public void test1() {}

    @Order(2)
    @Test public void test2() {}
  }

}
