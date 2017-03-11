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

import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;

public final class MockeryProcessorTest {
  @Test public void When_Class_With_Interceptor_Has_Not_Mockery_On_Method_Then_Fail_Compilation() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("RestApi", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import java.util.List;\n"
        + "\n"
        + "import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;\n"
        + "\n"
        + "@Bypass\n"
        + "public interface NoMethodMockery {\n"
        + "  List<String> method(@Valid(STRING) String s1);\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .failsToCompile();
  }

  @Test public void When_Class_With_Interceptor_Has_Not_Mockery_On_Param_Then_Fail_Compilation() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("RestApi", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import java.util.List;\n"
        + "\n"
        + "import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;\n"
        + "\n"
        + "@Bypass\n"
        + "interface NoParamsMockery {\n"
        + "  @Valid(STRING)\n"
        + "  List<String> method(String s1, Integer[] integers, double d1);\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .failsToCompile();
  }

  @Test public void When_Class_Without_Interceptor_Generate_Nothing() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("RestApi", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import java.util.List;\n"
        + "\n"
        + "import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;\n"
        + "\n"
        + "interface RestApi {\n"
        + "  @Valid(STRING)\n"
        + "  List<String> method(@Valid(STRING) String email);\n"
        + "\n"
        + "  List<String> method2();\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .compilesWithoutError();
  }

  @Test public void With_No_Params() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("NoParams", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import java.util.List;\n"
        + "\n"
        + "import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;\n"
        + "\n"
        + "@Bypass\n"
        + "interface NoParams {\n"
        + "  @Valid(STRING)\n"
        + "  List<String> method();\n"
        + "}");

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/NoParamsTest_", "" +
        "package test;\n"
        + "import io.victoralbertos.mockery.api.Order;\n"
        + "import io.victoralbertos.mockery.internal.OrderedRunner;\n"
        + "import io.victoralbertos.mockery.internal.Robot;\n"
        + "import io.victoralbertos.mockery.internal.RobotBuilder;\n"
        + "import java.lang.String;\n"
        + "import java.util.List;\n"
        + "import javax.annotation.Generated;\n"
        + "import org.junit.Rule;\n"
        + "import org.junit.Test;\n"
        + "import org.junit.rules.ExpectedException;\n"
        + "import org.junit.runner.RunWith;\n"
        + "\n"
        + "@Generated(\n"
        + "    value = \"io.victoralbertos.mockery.internal.MockeryProcessor\",\n"
        + "    comments = \"Generated code from Mockery. Don't modify. Or modify. It doesn't matter.\"\n"
        + ")\n"
        + "@RunWith(OrderedRunner.class)\n"
        + "public abstract class NoParamsTest_ {\n"
        + "  @Rule\n"
        + "  public final ExpectedException exception = ExpectedException.none();\n"
        + "\n"
        + "  protected abstract NoParams noParams();\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method_Then_Get_Response() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(NoParams.class)\n"
        + "                .onMethod(\"method\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = noParams().method();\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void With_Params() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("Params", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.DTO;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import io.victoralbertos.mockery.internal.Mockeries;\n"
        + "import io.victoralbertos.mockery.internal.Model;\n"
        + "import java.util.List;\n"
        + "\n"
        + "import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;\n"
        + "\n"
        + "@Bypass\n"
        + "public interface Params {\n"
        + "  @Valid(STRING)\n"
        + "  List<String> method(@Valid(STRING) String s1,\n"
        + "      @DTO(Mockeries.MockModel.class) List<Model> models);\n"
        + "}");

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/ParamsTest_", "" +
        "package test;\n"
        + "import io.victoralbertos.mockery.api.Order;\n"
        + "import io.victoralbertos.mockery.internal.Model;\n"
        + "import io.victoralbertos.mockery.internal.OrderedRunner;\n"
        + "import io.victoralbertos.mockery.internal.Robot;\n"
        + "import io.victoralbertos.mockery.internal.RobotBuilder;\n"
        + "import java.lang.AssertionError;\n"
        + "import java.lang.String;\n"
        + "import java.util.List;\n"
        + "import javax.annotation.Generated;\n"
        + "import org.junit.Rule;\n"
        + "import org.junit.Test;\n"
        + "import org.junit.rules.ExpectedException;\n"
        + "import org.junit.runner.RunWith;\n"
        + "\n"
        + "@Generated(\n"
        + "    value = \"io.victoralbertos.mockery.internal.MockeryProcessor\",\n"
        + "    comments = \"Generated code from Mockery. Don't modify. Or modify. It doesn't matter.\"\n"
        + ")\n"
        + "@RunWith(OrderedRunner.class)\n"
        + "public abstract class ParamsTest_ {\n"
        + "  @Rule\n"
        + "  public final ExpectedException exception = ExpectedException.none();\n"
        + "\n"
        + "  protected abstract Params params();\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method_With_Illegal_s1_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(Params.class)\n"
        + "                .onMethod(\"method\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getIllegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = params().method(s1, models);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method_With_Illegal_models_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(Params.class)\n"
        + "                .onMethod(\"method\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getIllegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = params().method(s1, models);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method_Then_Get_Response() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(Params.class)\n"
        + "                .onMethod(\"method\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = params().method(s1, models);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void With_Optional_Params() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("OptionalParams", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.DTO;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Optional;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import io.victoralbertos.mockery.internal.Mockeries;\n"
        + "import io.victoralbertos.mockery.internal.Model;\n"
        + "import java.util.List;\n"
        + "import org.junit.FixMethodOrder;\n"
        + "import org.junit.runners.MethodSorters;\n"
        + "\n"
        + "import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;\n"
        + "\n"
        + "@Bypass\n"
        + "@FixMethodOrder(MethodSorters.NAME_ASCENDING)\n"
        + "public interface OptionalParams {\n"
        + "  @Valid(STRING)\n"
        + "  List<String> method(@Valid(STRING) String s1,\n"
        + "      @DTO(Mockeries.MockModel.class) List<Model> models,\n"
        + "      @Optional Integer i1);\n"
        + "}");

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/OptionalParamsTest_", "" +
        "package test;\n"
        + "import io.victoralbertos.mockery.api.Order;\n"
        + "import io.victoralbertos.mockery.internal.Model;\n"
        + "import io.victoralbertos.mockery.internal.OrderedRunner;\n"
        + "import io.victoralbertos.mockery.internal.Robot;\n"
        + "import io.victoralbertos.mockery.internal.RobotBuilder;\n"
        + "import java.lang.AssertionError;\n"
        + "import java.lang.Integer;\n"
        + "import java.lang.String;\n"
        + "import java.util.List;\n"
        + "import javax.annotation.Generated;\n"
        + "import org.junit.Rule;\n"
        + "import org.junit.Test;\n"
        + "import org.junit.rules.ExpectedException;\n"
        + "import org.junit.runner.RunWith;\n"
        + "\n"
        + "@Generated(\n"
        + "    value = \"io.victoralbertos.mockery.internal.MockeryProcessor\",\n"
        + "    comments = \"Generated code from Mockery. Don't modify. Or modify. It doesn't matter.\"\n"
        + ")\n"
        + "@RunWith(OrderedRunner.class)\n"
        + "public abstract class OptionalParamsTest_ {\n"
        + "  @Rule\n"
        + "  public final ExpectedException exception = ExpectedException.none();\n"
        + "\n"
        + "  protected abstract OptionalParams optionalParams();\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method_With_Illegal_s1_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(OptionalParams.class)\n"
        + "                .onMethod(\"method\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getIllegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "    Integer i1 = robot.getLegalForParam(2);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = optionalParams().method(s1, models, i1);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method_With_Illegal_models_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(OptionalParams.class)\n"
        + "                .onMethod(\"method\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getIllegalForParam(1);\n"
        + "    Integer i1 = robot.getLegalForParam(2);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = optionalParams().method(s1, models, i1);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method_Then_Get_Response() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(OptionalParams.class)\n"
        + "                .onMethod(\"method\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "    Integer i1 = robot.getLegalForParam(2);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = optionalParams().method(s1, models, i1);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void With_Several_Methods() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("SeveralMethods", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.DTO;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import io.victoralbertos.mockery.internal.Mockeries;\n"
        + "import io.victoralbertos.mockery.internal.Model;\n"
        + "import java.util.List;\n"
        + "\n"
        + "import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;\n"
        + "\n"
        + "@Bypass\n"
        + "public interface SeveralMethods {\n"
        + "  @Valid(STRING)\n"
        + "  List<String> method1(@Valid(STRING) String s1,\n"
        + "      @DTO(Mockeries.MockModel.class) List<Model> models);\n"
        + "\n"
        + "  @Valid(STRING)\n"
        + "  List<String> method2(@Valid(STRING) String s1,\n"
        + "      @DTO(Mockeries.MockModel.class) List<Model> models);\n"
        + "}");

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/SeveralMethodsTest_", "" +
        "package test;\n"
        + "import io.victoralbertos.mockery.api.Order;\n"
        + "import io.victoralbertos.mockery.internal.Model;\n"
        + "import io.victoralbertos.mockery.internal.OrderedRunner;\n"
        + "import io.victoralbertos.mockery.internal.Robot;\n"
        + "import io.victoralbertos.mockery.internal.RobotBuilder;\n"
        + "import java.lang.AssertionError;\n"
        + "import java.lang.String;\n"
        + "import java.util.List;\n"
        + "import javax.annotation.Generated;\n"
        + "import org.junit.Rule;\n"
        + "import org.junit.Test;\n"
        + "import org.junit.rules.ExpectedException;\n"
        + "import org.junit.runner.RunWith;\n"
        + "\n"
        + "@Generated(\n"
        + "    value = \"io.victoralbertos.mockery.internal.MockeryProcessor\",\n"
        + "    comments = \"Generated code from Mockery. Don't modify. Or modify. It doesn't matter.\"\n"
        + ")\n"
        + "@RunWith(OrderedRunner.class)\n"
        + "public abstract class SeveralMethodsTest_ {\n"
        + "  @Rule\n"
        + "  public final ExpectedException exception = ExpectedException.none();\n"
        + "\n"
        + "  protected abstract SeveralMethods severalMethods();\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method1_With_Illegal_s1_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(SeveralMethods.class)\n"
        + "                .onMethod(\"method1\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getIllegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = severalMethods().method1(s1, models);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method1_With_Illegal_models_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(SeveralMethods.class)\n"
        + "                .onMethod(\"method1\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getIllegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = severalMethods().method1(s1, models);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method1_Then_Get_Response() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(SeveralMethods.class)\n"
        + "                .onMethod(\"method1\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = severalMethods().method1(s1, models);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(1)\n"
        + "  public void When_Call_method2_With_Illegal_s1_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(SeveralMethods.class)\n"
        + "                .onMethod(\"method2\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getIllegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = severalMethods().method2(s1, models);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(1)\n"
        + "  public void When_Call_method2_With_Illegal_models_Then_Get_Exception() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(SeveralMethods.class)\n"
        + "                .onMethod(\"method2\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getIllegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = severalMethods().method2(s1, models);\n"
        + "    exception.expect(AssertionError.class);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(1)\n"
        + "  public void When_Call_method2_Then_Get_Response() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(SeveralMethods.class)\n"
        + "                .onMethod(\"method2\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Declare value params \n"
        + "    String s1 = robot.getLegalForParam(0);\n"
        + "    List<Model> models = robot.getLegalForParam(1);\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = severalMethods().method2(s1, models);\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void With_Skip() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("Skip", "" +
        "package test;\n"
        +"import io.victoralbertos.mockery.api.SkipTest;\n"
        + "import io.victoralbertos.mockery.api.built_in_interceptor.Bypass;\n"
        + "import io.victoralbertos.mockery.api.built_in_mockery.Valid;\n"
        + "import java.util.List;\n"
        + "\n"
        + "@Bypass\n"
        + "interface Skip {\n"
        + "  @SkipTest\n"
        + "  @Valid(Valid.Template.STRING)\n"
        + "  List<String> method();\n"
        + "\n"
        + "  @Valid(Valid.Template.STRING)\n"
        + "  List<String> method2();\n"
        + "}");

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/SkipTest_", "" +
        "package test;\n"
        + "import io.victoralbertos.mockery.api.Order;\n"
        + "import io.victoralbertos.mockery.internal.OrderedRunner;\n"
        + "import io.victoralbertos.mockery.internal.Robot;\n"
        + "import io.victoralbertos.mockery.internal.RobotBuilder;\n"
        + "import java.lang.String;\n"
        + "import java.util.List;\n"
        + "import javax.annotation.Generated;\n"
        + "import org.junit.Rule;\n"
        + "import org.junit.Test;\n"
        + "import org.junit.rules.ExpectedException;\n"
        + "import org.junit.runner.RunWith;\n"
        + "\n"
        + "@Generated(\n"
        + "    value = \"io.victoralbertos.mockery.internal.MockeryProcessor\",\n"
        + "    comments = \"Generated code from Mockery. Don't modify. Or modify. It doesn't matter.\"\n"
        + ")\n"
        + "@RunWith(OrderedRunner.class)\n"
        + "public abstract class SkipTest_ {\n"
        + "  @Rule\n"
        + "  public final ExpectedException exception = ExpectedException.none();\n"
        + "\n"
        + "  protected abstract Skip skip();\n"
        + "\n"
        + "  @Test\n"
        + "  @Order(0)\n"
        + "  public void When_Call_method2_Then_Get_Response() {\n"
        + "    // Init robot tester \n"
        + "    Robot robot = RobotBuilder\n"
        + "                .test(Skip.class)\n"
        + "                .onMethod(\"method2\")\n"
        + "                .build();\n"
        + "\n"
        + "     // Perform and validate response \n"
        + "    List<String> response = skip().method2();\n"
        + "    robot.validateResponse(response);\n"
        + "  }\n"
        + "}");

    assertAbout(JavaSourceSubjectFactory
        .javaSource()).that(source)
        .processedWith(new MockeryProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

}
