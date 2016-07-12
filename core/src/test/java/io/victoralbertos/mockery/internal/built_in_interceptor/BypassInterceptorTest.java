package io.victoralbertos.mockery.internal.built_in_interceptor;

import java.lang.reflect.Type;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class BypassInterceptorTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private BypassInterceptor bypassInterceptor;

  @Before public void init() {
    bypassInterceptor = new BypassInterceptor();
  }

  @Test public void When_Call_On_Legal_Then_Retrieve_The_Same_Response() {
    String response = "response";

    assertThat((String) bypassInterceptor.onLegalMock(response, null),
        is(response));
  }

  @Test public void When_Call_On_Illegal_Then_Throw_Error() {
    AssertionError assertionError = new AssertionError("error");

    try {
      bypassInterceptor.onIllegalMock(assertionError, null);
      fail();
    } catch (AssertionError e) {
      assertThat("error", is(e.getMessage()));
    }
  }

  @Test public void When_Call_On_Adapt_Response_Then_Retrieve_The_Same_Response() {
    String response = "response";

    assertThat((String) bypassInterceptor.adaptResponse(response, null),
        is(response));
  }

  @Test public void When_Call_On_Adapt_Type_Then_Retrieve_The_Same_Response() {
    Type stringType = String.class;

    assertThat(bypassInterceptor.adaptType(stringType, null),
        is(stringType));
  }

  @Test public void When_Call_Validate_With_Candidate_Then_Do_Not_Throw_Exception() {
    String response = "response";
    bypassInterceptor.validate(response, null);
  }

  @Test public void When_Call_Validate_With_Null_Candidate_Then_Throw_Exception() {
    exception.expect(AssertionError.class);
    bypassInterceptor.validate(null, null);
  }

}
