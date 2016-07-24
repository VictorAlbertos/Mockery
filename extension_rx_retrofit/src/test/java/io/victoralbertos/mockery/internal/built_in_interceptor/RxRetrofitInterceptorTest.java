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

import io.victoralbertos.jolyglot.Types;
import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_interceptor.*;
import io.victoralbertos.mockery.api.built_in_interceptor.ErrorResponseAdapter;
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
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class RxRetrofitInterceptorTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private RxRetrofitInterceptor rxRetrofitInterceptor;

  @Before public void init() {
    rxRetrofitInterceptor = new RxRetrofitInterceptor();
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Not_Observable_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("mock");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Not_Parameterized_Observable_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("notParameterizedObservable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
  }

  @Test public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Not_Observable_Then_Get_Exception()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("mock");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rxRetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
  }

  @Test public void When_Mock_Seed_Is_Type_Observable_Then_Get_Exception() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(RuntimeException.class);
    rxRetrofitInterceptor.onLegalMock(Observable.just(new Mock()), metadata);
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Observable_Then_Get_Object()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
    TestSubscriber<Mock> subscriber = new TestSubscriber();
    observable.subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Mock mock = subscriber.getOnNextEvents().get(0);
    assertNotNull(mock);
  }

  @Test public void When_Call_OnLegalMock_If_Method_Return_Type_Is_Observable_Response_Then_Get_Response()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMock");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
    TestSubscriber<Response<Mock>> subscriber = new TestSubscriber();
    observable.subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Mock> response = subscriber.getOnNextEvents().get(0);
    assertTrue(response.isSuccessful());
    assertNotNull(response.body());
  }

  @Test public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Observable_Then_Get_Error_Observable()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    TestSubscriber<List<Mock>> subscriber = new TestSubscriber();
    observable.subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertNoValues();

    HttpException httpException = (HttpException) subscriber.getOnErrorEvents().get(0);
    assertThat(httpException.getMessage(), is("HTTP 404 null"));
  }

  @Test public void When_Call_OnIllegalMock_If_Method_Return_Type_Is_Observable_Response_Then_Get_Response_Body_Null()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMock");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor
        .onIllegalMock(new AssertionError("BOOM!"), metadata);
    TestSubscriber<Response<Mock>> subscriber = new TestSubscriber();
    observable.subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Mock> response = subscriber.getOnNextEvents().get(0);
    assertNull(response.body());
    assertFalse(response.isSuccessful());
    assertThat(response.errorBody().string(), is("BOOM!"));
  }

  @Test public void When_Call_OnLegalMock_With_Delay_Then_Delay()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
    checkDelay(observable, 100);
  }

  @Test public void When_Call_OnLegalMock_Response_With_Delay_Then_Delay() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMock");
    RxRetrofit annotation = PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
    checkDelay(observable, 100);
  }

  @Test public void When_Call_OnIllegalMock_With_Delay_Then_Delay() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    checkDelay(observable, 100);
  }

  @Test public void When_Call_OnIllegalMock_Response_With_Delay_Then_Delay() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMock");
    RxRetrofit annotation = PlaceholderRetrofitDelayedAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onIllegalMock(new AssertionError(), metadata);
    checkDelay(observable, 100);
  }

  @Test public void When_Call_OnIllegalMock_Response_With_Custom_Response_Adapter_Adapt_It()
      throws NoSuchMethodException, IOException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMock");
    RxRetrofit annotation =
        PlaceholderRetrofitErrorResponseAdapterAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable observable = rxRetrofitInterceptor.onIllegalMock(new AssertionError("BOOM!"), metadata);
    TestSubscriber<Response<Mock>> subscriber = new TestSubscriber();
    observable.subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Response<Mock> response = subscriber.getOnNextEvents().get(0);
    assertNull(response.body());
    assertThat(response.errorBody().string(), is("{'message':'BOOM!'}"));
  }

  private void checkDelay(Observable observable, long millisDelayed) {
    long startNanos = System.nanoTime();

    TestSubscriber subscriber = new TestSubscriber();
    observable.subscribe(subscriber);
    subscriber.awaitTerminalEvent();

    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

    assertTrue("Mismatch delayed. TookMs: " + tookMs
        + " MillisDelayed: " + millisDelayed, tookMs >= millisDelayed);
  }

  @Test public void When_Call_Validate_With_Observable_Then_Do_Not_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    rxRetrofitInterceptor.validate(Observable.just(new Mock()), metadata);
  }

  @Test public void When_Call_Validate_With_Observable_Error_Then_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    exception.expect(AssertionError.class);
    rxRetrofitInterceptor.validate(Observable.error(new AssertionError("BOOM!")),
        metadata);
  }

  @Test public void When_Call_Validate_With_Observable_Response_Then_Do_Not_Throw_Assertion()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    rxRetrofitInterceptor.validate(Observable.just(Response.success(new Mock())), metadata);
  }

  @Test public void When_Call_Validate_With_Observable_Response_Error_Then_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    ResponseBody responseBody = ResponseBody
        .create(MediaType.parse("application/json"), "BOOM!");
    Response<Mock> response = Response.error(404, responseBody);

    exception.expect(AssertionError.class);
    rxRetrofitInterceptor.validate(Observable.just(response), metadata);
  }

  @Test public void When_Call_Adapt_Response_With_Observable_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable<Mock> oMock =
        (Observable<Mock>) rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
    Mock mock = (Mock) rxRetrofitInterceptor.adaptResponse(oMock, metadata);
    assertNotNull(mock);
  }

  @Test public void When_Call_Adapt_Response_With_Observable_Response_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMock");
    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Observable<Mock> oMock =
        (Observable<Mock>) rxRetrofitInterceptor.onLegalMock(new Mock(), metadata);
    Mock mock = (Mock) rxRetrofitInterceptor.adaptResponse(oMock, metadata);
    assertNotNull(mock);
  }

  @Test public void When_Call_Adapt_Type_With_Observable_Mock_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observable");

    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Mock.class;

    Type adaptedType = rxRetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Observable_List_Mock_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observableMocks");

    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Types.newParameterizedType(List.class, Mock.class);

    Type adaptedType = rxRetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Observable_Response_Mock_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMock");

    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Mock.class;

    Type adaptedType = rxRetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  @Test public void When_Call_Adapt_Type_With_Observable_Response_List_Mock_Then_Unwrap_Its_Value() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("observableResponseMocks");

    RxRetrofit annotation = PlaceholderRetrofitAnnotation.class.getAnnotation(RxRetrofit.class);
    Metadata<RxRetrofit> metadata = new Metadata(Providers.class,
        method, null, annotation, method.getGenericReturnType());

    Type methodType = method.getGenericReturnType();
    Type expectedType = Types.newParameterizedType(List.class, Mock.class);

    Type adaptedType = rxRetrofitInterceptor
        .adaptType(methodType, metadata);

    assertEquals(expectedType, adaptedType);
  }

  private static class Mock {}

  private interface Providers {
    Response response();
    Mock mock();
    Observable notParameterizedObservable();

    Observable<Mock> observable();
    Observable<Response<Mock>> observableResponseMock();
    Observable<List<Mock>> observableMocks();
    Observable<Response<List<Mock>>> observableResponseMocks();
  }


  @RxRetrofit(delay = 0, failurePercent = 0,
      variancePercentage = 0)
  private interface PlaceholderRetrofitAnnotation {}

  @RxRetrofit(delay = 100, failurePercent = 0,
      variancePercentage = 0)
  private interface PlaceholderRetrofitDelayedAnnotation {}

  @RxRetrofit(delay = 0, failurePercent = 0,
      variancePercentage = 0, errorResponseAdapter = JsonMessageErrorResponseAdapter.class)
  private interface PlaceholderRetrofitErrorResponseAdapterAnnotation {}

  private static class JsonMessageErrorResponseAdapter implements ErrorResponseAdapter {
    @Override public String adapt(String error) {
      String json = "{'message':'%s'}";
      return String.format(json, error);
    }
  }
}
