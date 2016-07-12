package io.victoralbertos.mockery.internal.built_in_interceptor;

import io.victoralbertos.mockery.api.built_in_interceptor.ErrorResponseAdapter;

/**
 * Default implementation of {@link ErrorResponseAdapter}. It simply bypasses the error message.
 */
public final class ByPassErrorResponseAdapter implements ErrorResponseAdapter {

  /**
   * {@inheritDoc}
   */
  @Override public String adapt(String error) {
    return error;
  }

}
