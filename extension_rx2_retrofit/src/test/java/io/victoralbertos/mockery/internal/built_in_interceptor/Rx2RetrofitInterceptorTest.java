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

package io.victoralbertos.mockery.internal.built_in_interceptor;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.victoralbertos.jolyglot.Types;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_interceptor.ErrorResponseAdapter;
import io.victoralbertos.mockery.api.built_in_interceptor.Rx2Retrofit;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import retrofit2.HttpException;
import retrofit2.Response;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class Rx2RetrofitInterceptorTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private Rx2RetrofitInterceptor rx2RetrofitInterceptor;

  @Before public void init() {
    rx2RetrofitInterceptor = new Rx2RetrofitInterceptor();
  }

  @Test
  public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Not_Single_Or_Completable_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("mock");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
  }

  @Test
  public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Not_Parameterized_Single_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("notParameterizedSingle");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
  }

  @Test
  public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Not_Single_Or_Completable_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("mock");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rx2RetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
  }

  @Test public void When_Mock_Seed_Is_Type_Single_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rx2RetrofitInterceptor.onLegalMock(Single.just(new Mock()), metadata);
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Single_Then_Get_Object()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
    TestObserver<Mock> subscriber = single.test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Mock mock = subscriber.values().get(0);
    assertNotNull(mock);
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Completable_Then_Get_Completion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("completable");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Completable completable =
        (Completable) rx2RetrofitInterceptor.onLegalMock(null, metadata);
    TestObserver<Void> subscriber = completable.test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertComplete();
  }

  @Test
  public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Single_Response_Then_Get_Response()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMock");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
    TestObserver<Response<Mock>> subscriber = single.test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Mock> response = subscriber.values().get(0);
    assertTrue(response.isSuccessful());
    assertNotNull(response.body());
  }

  @Test public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Single_Then_Get_Error_Single()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    TestObserver<List<Mock>> subscriber = single.test();

    subscriber.awaitTerminalEvent();
    subscriber.assertNoValues();

    HttpException httpException = (HttpException) subscriber.errors().get(0);
    assertThat(httpException.getMessage(), is("HTTP 404 null"));
  }

  @Test
  public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Single_Response_Then_Get_Response_Body_Null()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMock");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor
        .onIllegalMock(new AssertionError("BOOM!"), metadata);
    TestObserver<Response<Mock>> subscriber = single.test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Mock> response = subscriber.values().get(0);
    assertNull(response.body());
    assertFalse(response.isSuccessful());
    assertThat(response.errorBody().string(), is("BOOM!"));
  }

  @Test public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Completable_Then_Get_Error_Completable()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("completable");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Completable completable = (Completable) rx2RetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    TestObserver<Void> subscriber = completable.test();

    subscriber.awaitTerminalEvent();
    subscriber.assertNotComplete();

    HttpException httpException = (HttpException) subscriber.errors().get(0);
    assertThat(httpException.getMessage(), is("HTTP 404 null"));
  }

  @Test public void When_Call_OnLegalMock_Single_With_Delay_Then_Delay()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation =
        PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
    checkDelaySingle(single, 100);
  }

  @Test public void When_Call_OnLegalMock_Response_With_Delay_Then_Delay()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMock");
    Rx2Retrofit annotation =
        PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
    checkDelaySingle(single, 100);
  }

  @Test public void When_Call_OnLegalMock_Completable_With_Delay_Then_Delay()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("completable");
    Rx2Retrofit annotation =
        PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Completable completable = (Completable) rx2RetrofitInterceptor.onLegalMock(null, metadata);
    checkDelayCompletable(completable, 100);
  }

  @Test public void When_Call_OnIllegalMock_Single_With_Delay_Then_Delay() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation =
        PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    checkDelaySingle(single, 100);
  }

  @Test public void When_Call_OnIllegalMock_Response_With_Delay_Then_Delay()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMock");
    Rx2Retrofit annotation =
        PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single = (Single) rx2RetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    checkDelaySingle(single, 100);
  }

  @Test public void When_Call_OnIllegalMock_Completable_With_Delay_Then_Delay() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("completable");
    Rx2Retrofit annotation =
        PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Completable completable = (Completable) rx2RetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    checkDelayCompletable(completable, 100);
  }

  @Test public void When_Call_OnIllegalMock_Response_With_Custom_Response_Adapter_Adapt_It()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMock");
    Rx2Retrofit annotation =
        PlaceholderRetrofitErrorResponseAdapterAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single single =
        (Single) rx2RetrofitInterceptor.onIllegalMock(new AssertionError("BOOM!"), metadata);
    TestObserver<Response<Mock>> subscriber = single.test();
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Mock> response = subscriber.values().get(0);
    assertNull(response.body());
    assertThat(response.errorBody().string(), is("{'message':'BOOM!'}"));
  }

  private void checkDelaySingle(Single single, long millisDelayed) {
    long startNanos = System.nanoTime();

    TestObserver subscriber = new TestObserver();
    single.subscribe(subscriber);
    subscriber.awaitTerminalEvent();

    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

    assertTrue("Mismatch delayed. TookMs: " + tookMs
        + " MillisDelayed: " + millisDelayed, tookMs >= millisDelayed);
  }

  private void checkDelayCompletable(Completable single, long millisDelayed) {
    long startNanos = System.nanoTime();

    TestObserver subscriber = new TestObserver();
    single.subscribe(subscriber);
    subscriber.awaitTerminalEvent();

    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

    assertTrue("Mismatch delayed. TookMs: " + tookMs
        + " MillisDelayed: " + millisDelayed, tookMs >= millisDelayed);
  }

  @Test public void When_Call_Validate_With_Single_Then_Do_Not_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    rx2RetrofitInterceptor.validate(Single.just(new Mock()), metadata);
  }

  @Test public void When_Call_Validate_With_Single_Error_Then_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(AssertionError.class);
    rx2RetrofitInterceptor.validate(Single.error(new AssertionError("BOOM!")),
        metadata);
  }

  @Test public void When_Call_Validate_With_Single_Response_Then_Do_Not_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    rx2RetrofitInterceptor.validate(Single.just(Response.success(new Mock())), metadata);
  }

  @Test public void When_Call_Validate_With_Single_Response_Error_Then_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    ResponseBody responseBody = ResponseBody
        .create(MediaType.parse("application/json"), "BOOM!");
    Response<Mock> response = Response.error(404, responseBody);

    exception.expect(AssertionError.class);
    rx2RetrofitInterceptor.validate(Single.just(response), metadata);
  }

  @Test public void When_Call_Validate_With_Completable_Then_Do_Not_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("completable");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    rx2RetrofitInterceptor.validate(Completable.complete(), metadata);
  }

  @Test public void When_Call_Validate_With_Completable_Error_Then_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("completable");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(AssertionError.class);
    rx2RetrofitInterceptor.validate(Single.error(new AssertionError("BOOM!")),
        metadata);
  }

  @Test public void When_Call_Adapt_Response_With_Single_Then_Unwrap_Its_Value()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single<Mock> oMock =
        (Single<Mock>) rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
    Mock mock = (Mock) rx2RetrofitInterceptor.adaptResponse(oMock, metadata);
    assertNotNull(mock);
  }

  @Test public void When_Call_Adapt_Response_With_Single_Response_Then_Unwrap_Its_Value()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMock");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Single<Mock> oMock =
        (Single<Mock>) rx2RetrofitInterceptor.onLegalMock(new Mock(), metadata);
    Mock mock = (Mock) rx2RetrofitInterceptor.adaptResponse(oMock, metadata);
    assertNotNull(mock);
  }

  @Test public void When_Call_Adapt_Response_With_Completable_Then_Get_Null()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("completable");
    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Completable completable = (Completable) rx2RetrofitInterceptor.onLegalMock(null, metadata);
    Object nothing = rx2RetrofitInterceptor.adaptResponse(completable, metadata);
    assertNull(nothing);
  }

  @Test public void When_Call_Adapt_Type_With_Single_Mock_Then_Unwrap_Its_Value()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("single");

    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Mock.class;

    Type adaptedType = rx2RetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Single_List_Mock_Then_Unwrap_Its_Value()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("singleMocks");

    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Types.newParameterizedType(List.class, Mock.class);

    Type adaptedType = rx2RetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Single_Response_Mock_Then_Unwrap_Its_Value()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMock");

    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Mock.class;

    Type adaptedType = rx2RetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Single_Response_List_Mock_Then_Unwrap_Its_Value()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("singleResponseMocks");

    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Types.newParameterizedType(List.class, Mock.class);

    Type adaptedType = rx2RetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Completable_Then_Get_Object()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("completable");

    Rx2Retrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(Rx2Retrofit.class);
    Metadata<Rx2Retrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type adaptedType = rx2RetrofitInterceptor.adaptType(methodType, metadata);
    assertEquals(Object.class, adaptedType);
  }

  private static class Mock {
  }

  private interface Providers {
    Response response();

    Mock mock();

    Single notParameterizedSingle();

    Single<Mock> single();

    Single<Response<Mock>> singleResponseMock();

    Single<List<Mock>> singleMocks();

    Single<Response<List<Mock>>> singleResponseMocks();

    Completable completable();
  }

  @Rx2Retrofit(delay = 0, failurePercent = 0,
      variancePercentage = 0)
  private interface PlaceholderRetrofitAnnotation {
  }

  @Rx2Retrofit(delay = 100, failurePercent = 0,
      variancePercentage = 0)
  private interface PlaceholderRetrofitDelayedAnnotation {
  }

  @Rx2Retrofit(delay = 0, failurePercent = 0,
      variancePercentage = 0, errorResponseAdapter = JsonMessageErrorResponseAdapter.class)
  private interface PlaceholderRetrofitErrorResponseAdapterAnnotation {
  }

  private static class JsonMessageErrorResponseAdapter implements ErrorResponseAdapter {
    @Override public String adapt(String error) {
      String json = "{'message':'%s'}";
      return String.format(json, error);
    }
  }
}
