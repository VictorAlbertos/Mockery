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
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class RestApiTest {
  @Rule public final ExpectedException exception = ExpectedException.none();

  private RestApi restApi;

  @Before public void init() {
    restApi = new Mockery.Builder<RestApi>()
        .mock(RestApi.class)
        .build();
  }

  @Test public void modelWithoutCall() {
    try {
      restApi.modelWithoutCall();
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking return type of method RestApi#modelWithoutCall \n"
          + "class io.victoralbertos.mockery.internal.integration.Model was found. But only Call<T> is supported as method return type.\n"
          + "To fix it, change the return type to Call<T>.\n"));
    }
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

  @Test public void modelSync() throws IOException {
    Response<Model> responseModel = restApi.model().execute();
    assertTrue(responseModel.isSuccessful());

    Model model = responseModel.body();
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelAsync() throws Exception {
    final AtomicReference<Response<Model>> atomicReference = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    restApi.model().enqueue(new Callback<Model>() {
      @Override public void onResponse(Call<Model> call,
          Response<Model> response) {
        atomicReference.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Model> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    Response<Model> responseModel = atomicReference.get();
    assertTrue(responseModel.isSuccessful());

    Model model = responseModel.body();
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelWithParamsSync() throws IOException {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}");

    Response<Model> responseModel = restApi
        .modelWithParams(requestBody, 0).execute();
    assertTrue(responseModel.isSuccessful());

    Model model = responseModel.body();
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelWithParamsFailsWhenInvalidRequestBodySync() throws IOException {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "{\"s1\":\"\"}");

    Response<Model> responseModel = restApi
        .modelWithParams(requestBody, 0).execute();
    assertFalse(responseModel.isSuccessful());

    ResponseBody responseBody = responseModel.errorBody();
    assertThat(responseBody.string(), is("model.s1 must be equal "
        + "to io.victoralbertos.mockery.internal.integration.Model"));
  }

  @Test public void modelWithParamsAsync() throws Exception {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}");

    final AtomicReference<Response<Model>> atomicReference = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    restApi.modelWithParams(requestBody, 0).enqueue(new Callback<Model>() {
      @Override public void onResponse(Call<Model> call,
          Response<Model> response) {
        atomicReference.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Model> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    Response<Model> responseModel = atomicReference.get();
    assertTrue(responseModel.isSuccessful());

    Model model = responseModel.body();
    assertThat(model.getS1(), is(Model.class.getName()));
  }

  @Test public void modelWithParamsFailsWhenInvalidRequestBodyAsync() throws Exception {
    RequestBody requestBody = null;

    final AtomicReference<Response<Model>> atomicReference = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    restApi.modelWithParams(requestBody, 0).enqueue(new Callback<Model>() {
      @Override public void onResponse(Call<Model> call,
          Response<Model> response) {
        atomicReference.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Model> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    Response<Model> responseModel = atomicReference.get();
    assertFalse(responseModel.isSuccessful());

    ResponseBody responseBody = responseModel.errorBody();
    assertThat(responseBody.string(), is("model can not be null"));
  }


  @Test public void modelWithWrongMockeryParam() {
    Model model = new Model(Model.class.getName());
    exception.expect(RuntimeException.class);
    restApi.modelWithWrongMockeryParam(model);
  }

  @Test public void modelsSync() throws IOException {
    Response<List<Model>> responseModels =
        restApi.models("[{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}]").execute();

    assertTrue(responseModels.isSuccessful());

    List<Model> models = responseModels.body();
    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(),
        is(Model.class.getName()));
  }

  @Test public void modelsAsync() throws Exception {
    final AtomicReference<Response<List<Model>>> atomicReference = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    restApi.models("[{\"s1\":\"io.victoralbertos.mockery.internal.integration.Model\"}]")
        .enqueue(new Callback<List<Model>>() {
      @Override public void onResponse(Call<List<Model>> call,
          Response<List<Model>> response) {
        atomicReference.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<List<Model>> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    Response<List<Model>> responseModels = atomicReference.get();
    assertTrue(responseModels.isSuccessful());

    List<Model> models = responseModels.body();
    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(),
        is(Model.class.getName()));
  }

  @Test public void modelsWithParamsSync() throws IOException {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "foo@foo.bar");

    Response<List<Model>> responseModels = restApi.modelsWithParams(requestBody, null).execute();
    assertTrue(responseModels.isSuccessful());

    List<Model> models = responseModels.body();
    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(),
        is(Model.class.getName()));
  }

  @Test public void modelsWithParamsAsync() throws Exception {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "foo@foo.bar");

    final AtomicReference<Response<List<Model>>> atomicReference = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    restApi.modelsWithParams(requestBody, null)
        .enqueue(new Callback<List<Model>>() {
          @Override public void onResponse(Call<List<Model>> call,
              Response<List<Model>> response) {
            atomicReference.set(response);
            latch.countDown();
          }

          @Override public void onFailure(Call<List<Model>> call, Throwable t) {
            t.printStackTrace();
          }
        });

    assertTrue(latch.await(10, SECONDS));

    Response<List<Model>> responseModels = atomicReference.get();
    assertTrue(responseModels.isSuccessful());

    List<Model> models = responseModels.body();
    assertThat(models.size(), is(1));
    assertThat(models.get(0).getS1(),
        is(Model.class.getName()));
  }

  @Test public void modelsWithParamsFailsWhenInvalidEmailSync() throws IOException {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "");

    Response<List<Model>> responseModels = restApi.modelsWithParams(requestBody, null).execute();
    assertFalse(responseModels.isSuccessful());

    ResponseBody responseBody = responseModels.errorBody();
    assertThat(responseBody.string(), is("empty does not match with regex EMAIL"));
  }

  @Test public void modelsWithParamsFailsWhenInvalidEmailAsync() throws Exception {
    RequestBody requestBody = RequestBody
        .create(MediaType.parse("text/plain"), "");

    final AtomicReference<Response<List<Model>>> atomicReference = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    restApi.modelsWithParams(requestBody, null)
        .enqueue(new Callback<List<Model>>() {
          @Override public void onResponse(Call<List<Model>> call,
              Response<List<Model>> response) {
            atomicReference.set(response);
            latch.countDown();
          }

          @Override public void onFailure(Call<List<Model>> call, Throwable t) {
            t.printStackTrace();
          }
        });

    assertTrue(latch.await(10, SECONDS));

    Response<List<Model>> responseModels = atomicReference.get();
    assertFalse(responseModels.isSuccessful());

    ResponseBody responseBody = responseModels.errorBody();
    assertThat(responseBody.string(), is("empty does not match with regex EMAIL"));
  }

  @Test public void modelsWithParamWithoutMockery() {
    exception.expect(RuntimeException.class);
    restApi.modelsWithParamWithoutMockery(3);
  }

  @Test public void integerSync() throws IOException {
    Response<Integer> integerResponse = restApi.integer().execute();
    assertTrue(integerResponse.isSuccessful());

    Integer integer = integerResponse.body();
    assertThat(integer, is(30));
  }

  @Test public void integerAsync() throws Exception {
    final AtomicReference<Response<Integer>> atomicReference = new AtomicReference<>();
    final CountDownLatch latch = new CountDownLatch(1);

    restApi.integer().enqueue(new Callback<Integer>() {
      @Override public void onResponse(Call<Integer> call,
          Response<Integer> response) {
        atomicReference.set(response);
        latch.countDown();
      }

      @Override public void onFailure(Call<Integer> call, Throwable t) {
        t.printStackTrace();
      }
    });

    assertTrue(latch.await(10, SECONDS));

    Response<Integer> integerResponse = atomicReference.get();
    assertTrue(integerResponse.isSuccessful());

    Integer integer = integerResponse.body();
    assertThat(integer, is(30));
  }

}
