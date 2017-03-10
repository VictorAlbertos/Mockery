package io.victoralbertos.mockery.internal.built_in_interceptor;

import io.victoralbertos.mockery.api.Interceptor;
import io.victoralbertos.mockery.api.Metadata;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class BypassInterceptor implements Interceptor.Behaviour<Annotation> {

  @Override public Object onLegalMock(Object mock, Metadata<Annotation> metadata) {
    return mock;
  }

  @Override public Object onIllegalMock(AssertionError assertionError, Metadata<Annotation> metadata) {
    throw assertionError;
  }

  @Override public void validate(Object response, Metadata<Annotation> metadata) throws AssertionError {
    if (response == null) throw new AssertionError("response can not be null");
  }

  @Override public Object adaptResponse(Object response, Metadata<Annotation> metadata) {
    return response;
  }

  @Override public Type adaptType(Type responseType, Metadata<Annotation> metadata) {
    return responseType;
  }
}
