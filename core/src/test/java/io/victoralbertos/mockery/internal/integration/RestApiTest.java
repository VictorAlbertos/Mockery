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

import io.victoralbertos.mockery.api.Mockery;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class RestApiTest {
  @Rule public final ExpectedException exception = ExpectedException.none();

  private RestApi restApi;

  @Before public void init() {
    restApi = new Mockery.Builder<RestApi>()
        .mock(RestApi.class)
        .build();
  }

  @Test public void modelWithoutDTO() {
    try {
      restApi.modelWithoutDTO();
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking method RestApi#modelWithoutDTO \n"
          + "No @Mockery annotation for return method was found.\n"
          + "To fix it, annotate method with one.\n"));
    }
  }

  @Test public void modelWithWrongDTO() {
    try {
      restApi.modelWithWrongDTO();
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking DTOMockery on method RestApi#modelWithWrongDTO \n"
          + " an attempt to use it with class io.victoralbertos.mockery.internal.integration.Model was found. But it is not a supported type for DTOMockery.\n"
          + "To fix it, use DTOMockery with: [java.util.List<io.victoralbertos.mockery.internal.integration.Model>].\n"));
    }
  }

  @Test public void model() {
    Model model = restApi.model();
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelWithParams() {
    Model model = new Model(Model.class.getName());
    Model response = restApi
        .modelWithParams(model, 0, "{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}");
    assertThat(response.getS1(), is(model.getS1()));
  }

  @Test public void modelWithParamsFailsWhenInvalidJson() {
    Model model = new Model(Model.class.getName());
    exception.expect(AssertionError.class);
    restApi.modelWithParams(model, 0, "{\"s1\":\"\"}");
  }

  @Test public void modelWithWrongMockeryParam() {
    Model model = new Model(Model.class.getName());
    exception.expect(RuntimeException.class);
    restApi.modelWithWrongMockeryParam(model);
  }

  @Test public void models() {
    List<Model> models = restApi.models();

    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(),
        is(Model.class.getName()));
  }

  @Test public void modelsWithParams() {
    String email = "foo@foo.bar";
    List<Model> models = restApi.modelsWithParams(email, null);

    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(),
        is(Model.class.getName()));
  }

  @Test public void modelsWithParamsFailsWhenInvalidEmail() {
    exception.expect(AssertionError.class);
    restApi.modelsWithParams("", null);
  }

  @Test public void modelsWithParamWithoutMockery() {
    exception.expect(RuntimeException.class);
    restApi.modelsWithParamWithoutMockery(3);
  }

  @Test public void modelWithEnum() {
    String order = "asc";
    Model model = restApi.modelWithEnum(order);

    assertThat(model.getS1(),
        is(Model.class.getName()));
  }

  @Test public void modelWithEnumFails() {
    String order = "invalid";
    exception.expect(AssertionError.class);
    restApi.modelWithEnum(order);
  }

}
