package io.victoralbertos.mockery.internal;

import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.ID;
import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class RobotTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private Robot robot;

  @Before public void init() {
    robot = new RobotBuilder()
        .test(Providers.class)
        .onMethod("mocks")
        .build();
  }

  @Test public void When_Name_Method_Not_Found_Throws_Exception() {
    try {
      robot = new RobotBuilder()
          .test(Providers.class)
          .onMethod("notFound")
          .build();
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("When checking Providers no method with "
          + "name notFound was found.\n"));
    }
  }


  @Test public void When_Call_Legal_For_Param_Position_Then_Get_Legal() {
    String s1 = robot.getLegalForParam(0);
    assertThat(s1, is("legal"));

    Integer i1 = robot.getLegalForParam(1);
    assertThat(i1, is(1));
  }

  @Test public void When_Call_Illegal_For_Param_Position_Then_Get_Legal() {
    String s1 = robot.getIllegalForParam(0);
    assertThat(s1, is("illegal"));

    int i1 = robot.getIllegalForParam(1);
    assertThat(i1, is(-1));
  }

  @Test public void When_Call_Validate_Response_Validate_Response() {
    robot.validateResponse(Arrays.asList(new Mock()));

    exception.expect(AssertionError.class);
    robot.validateResponse(Arrays.asList());

    exception.expect(AssertionError.class);
    robot.validateResponse(null);
  }

  @Bypass private interface Providers {
    @DTOArgs(DTOListMock.class)
    List<Mock> mocks(@Valid(value = STRING, legal = "legal", illegal = "illegal") String s1,
        @Valid(value = ID, legal = "1", illegal = "-1") Integer i1);
  }

  static class Mock {

  }

  static class DTOListMock implements DTOArgs.Behaviour<List<Mock>> {

    @Override public List<Mock> legal(Object[] args) {
      return null;
    }

    @Override public void validate(List<Mock> candidate) throws AssertionError {
      assert candidate != null && !candidate.isEmpty();
    }

  }
}
