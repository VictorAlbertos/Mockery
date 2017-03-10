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

import io.reactivex.observers.TestObserver;
import io.victoralbertos.mockery.api.Mockery;
import java.io.IOException;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class Rx2RestApiTest {
  @Rule public final ExpectedException exception = ExpectedException.none();

  private Rx2RestApi restApi;

  @Before public void init() {
    restApi = new Mockery.Builder<Rx2RestApi>()
        .mock(Rx2RestApi.class)
        .build();
  }

  @Test public void modelWithoutCall() {
    try {
      restApi.modelWithoutSingle();
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking return type of method Rx2RestApi#modelWithoutSingle \n"
          + "class io.victoralbertos.mockery.internal.integration.Model was found. But only Single<T> and Completable are supported as method return type.\n"
          + "To fix it, change the return type to Single<T> or Completable.\n"));
    }
  }

  @Test public void modelWithoutDTO() {
    try {
      restApi.modelWithoutDTO();
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking method Rx2RestApi#modelWithoutDTO \n"
          + "No @Mockery annotation for return method was found.\n"
          + "To fix it, annotate method with one.\n"));
    }
  }

  @Test public void modelWithWrongDTO() {
    try {
      restApi.modelWithWrongDTO();
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking DTOMockery on method Rx2RestApi#modelWithWrongDTO \n"
          + " an attempt to use it with class io.victoralbertos.mockery.internal.integration.Model was found. But it is not a supported type for DTOMockery.\n"
          + "To fix it, use DTOMockery with: [java.util.List<io.victoralbertos.mockery.internal.integration.Model>].\n"));
    }
  }

  @Test public void model() {
    TestObserver<Model> subscriber = restApi.model().test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Model model = subscriber.values().get(0);
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void completableModel() {
    TestObserver<Void> subscriber = restApi.completableModel().test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertComplete();
  }

  @Test public void modelResponse() {
    TestObserver<Response<Model>> subscriber = restApi.modelResponse().test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Model> modelResponse = subscriber.values().get(0);
    assertTrue(modelResponse.isSuccessful());

    Model model = modelResponse.body();
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelWithParams() {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}");

    TestObserver<Model> subscriber = restApi.modelWithParams(requestBody, 0).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Model model = subscriber.values().get(0);
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelWithParamsResponse() {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}");

    TestObserver<Response<Model>> subscriber = restApi.modelWithParamsResponse(requestBody, 0).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Model> modelResponse = subscriber.values().get(0);
    assertTrue(modelResponse.isSuccessful());

    Model model = modelResponse.body();
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelWithParamsFailsWhenInvalidRequestBody() {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "{\"s1\":\"\"}");

    TestObserver<Model> subscriber = restApi.modelWithParams(requestBody, 0).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoValues();
    subscriber.assertError(HttpException.class);

    Throwable error = subscriber.errors().get(0);
    assertThat(error.getMessage(), is("HTTP 404 null"));
  }

  @Test public void modelWithParamsResponseFailsWhenInvalidRequestBody() throws IOException {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "{\"s1\":\"\"}");

    TestObserver<Response<Model>> subscriber = restApi.modelWithParamsResponse(requestBody, 0).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Model> modelResponse = subscriber.values().get(0);
    assertFalse(modelResponse.isSuccessful());
    assertNull(modelResponse.body());
    assertThat(modelResponse.errorBody().string(), is("model.s1 must be equal "
        + "to io.victoralbertos.mockery.internal.integration.Model"));
  }


  @Test public void modelWithWrongMockeryParam() {
    Model model = new Model(Model.class.getName());
    exception.expect(RuntimeException.class);
    restApi.modelWithWrongMockeryParam(model);
  }

  @Test public void modelsWithParam() {
    String json = "[{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}]";

    TestObserver<List<Model>> subscriber = restApi.modelsWithParam(json).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    List<Model> models = subscriber.values().get(0);
    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(), is(Model.class.getName()));
  }

  @Test public void modelsWithParamResponse() {
    String json = "[{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}]";

    TestObserver<Response<List<Model>>> subscriber = restApi.modelsWithParamResponse(json).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<List<Model>> modelsResponse = subscriber.values().get(0);
    assertTrue(modelsResponse.isSuccessful());

    List<Model> models = modelsResponse.body();
    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(), is(Model.class.getName()));
  }

  @Test public void modelsWithParamFailsWhenInvalidJson() {
    String json = "";

    TestObserver<List<Model>> subscriber = restApi.modelsWithParam(json).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoValues();
    subscriber.assertError(HttpException.class);

    Throwable error = subscriber.errors().get(0);
    assertThat(error.getMessage(), is("HTTP 404 null"));
  }

  @Test public void modelsWithParamResponseFailsWhenInvalidJson() throws IOException {
    String json = "";

    TestObserver<Response<List<Model>>> subscriber = restApi.modelsWithParamResponse(json).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<List<Model>> modelResponse = subscriber.values().get(0);
    assertFalse(modelResponse.isSuccessful());
    assertNull(modelResponse.body());
    assertThat(modelResponse.errorBody().string(),
        is("models can not be null"));
  }

  @Test public void modelsWithParamWithoutMockery() {
    exception.expect(RuntimeException.class);
    restApi.modelsWithParamWithoutMockery(3);
  }

  @Test public void integer() {
    TestObserver<Integer> subscriber = restApi.integer().test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Integer integer = subscriber.values().get(0);
    assertThat(integer, is(30));
  }

  @Test public void modelsWithIdResponseFailsWhenInvalidId() throws IOException {
    TestObserver<Response<Model>> subscriber = restApi.id(-1).test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();

    Response<Model> modelResponse = subscriber.values().get(0);
    assertFalse(modelResponse.isSuccessful());
    assertNull(modelResponse.body());
    assertThat(modelResponse.errorBody().string(),
        is("-1 does not match with regex ID"));
  }

}
