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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.victoralbertos.mockery.api.Order;
import io.victoralbertos.mockery.internal.TestClass.Method;
import io.victoralbertos.mockery.internal.TestClass.Method.Param;
import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static com.squareup.javapoet.MethodSpec.methodBuilder;

final class BrewJavaFile {

  JavaFile from(TestClass testClass) throws IOException {
    List<MethodSpec> methodSpecs = new ArrayList<>();

    for (int i = 0; i < testClass.methods.size(); i++) {
      Method method = testClass.methods.get(i);
      methodSpecs
          .addAll(methods(testClass.className, method, i));
    }

    TypeSpec typeSpec = classTest(testClass.className, methodSpecs);

    return JavaFile.builder(testClass.className.packageName(), typeSpec)
        .build();
  }

  private List<MethodSpec> methods(ClassName className,
      Method method, int orderTest) {
    List<MethodSpec> methodSpecs = new ArrayList<>();

    for (Param param : method.params) {
      if (!param.isOptional) {
        methodSpecs
            .add(methodOneIllegalParam(className, method, param, orderTest));
      }
    }

    methodSpecs
        .add(methodAllParamLegals(className, method, orderTest));

    return methodSpecs;
  }

  private MethodSpec methodOneIllegalParam(ClassName className,
      Method method, Param param, int orderTest) {
    String methodName = String
        .format("When_Call_%s_With_Illegal_%s_Then_Get_Exception", method.name, param.name);

    MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
        .addAnnotation(Test.class)
        .addAnnotation(AnnotationSpec.builder(Order.class)
            .addMember("value", "$L", orderTest)
            .build())
        .addModifiers(Modifier.PUBLIC)
        .returns(void.class);

    initializeRobot(className, method, builder);
    variablesForParams(method.params, param, builder);
    response(className, method, true, builder);
    return builder.build();
  }

  private MethodSpec methodAllParamLegals(ClassName className,
      Method method, int orderTest) {
    String methodName = String
        .format("When_Call_%s_Then_Get_Response", method.name);

    MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
        .addAnnotation(Test.class)
        .addAnnotation(AnnotationSpec.builder(Order.class)
            .addMember("value", "$L", orderTest)
            .build())
        .addModifiers(Modifier.PUBLIC)
        .returns(void.class);

    initializeRobot(className, method, builder);
    variablesForParams(method.params, null, builder);
    response(className, method, false, builder);

    return builder.build();
  }

  private void initializeRobot(ClassName className, Method method,
      MethodSpec.Builder builder) {
    builder.addCode("// Init robot tester \n");

    List<Object> types = new ArrayList<>();
    types.add(Robot.class);
    types.add(RobotBuilder.class);
    types.add(className);
    types.add(method.name);

    builder
        .addStatement("$T robot = $T\n"
            + "        .test($T.class)\n"
            + "        .onMethod($S)\n"
            + "        .build()", types.toArray(new Object[types.size()]));

  }

  private void variablesForParams(List<Param> params, Param illegalTarget,
      MethodSpec.Builder builder) {
    if (!params.isEmpty()) {
      builder.addCode("\n // Declare value params \n");
    }

    for (Param param : params) {
      if (illegalTarget != null && illegalTarget == param) {
        builder.addStatement("$T $L = robot.getIllegalForParam($L)",
            param.type, param.name, param.positionOnMethod);
      } else {
        builder.addStatement("$T $L = robot.getLegalForParam($L)",
            param.type, param.name, param.positionOnMethod);
      }
    }
  }

  private void response(ClassName className, Method method, boolean expectError,
      MethodSpec.Builder builder) {
    builder.addCode("\n // Perform and validate response \n");

    String params = "";

    List<Object> args = new ArrayList<>();
    args.add(method.returnType);
    args.add(Introspector
        .decapitalize(className.simpleName()));
    args.add(method.name);

    for (int i = 0; i < method.params.size(); i++) {
      Param param = method.params.get(i);
      if (i == method.params.size() -1) params += param.name;
      else params += param.name + ", ";
    }

    args.add(params);


    builder.addStatement("$T response = $L().$L($L)",
        args.toArray(new Object[args.size()]));

    if (expectError) {
      builder.addStatement("exception.expect($T.class)", AssertionError.class);
    }

    builder.addStatement("robot.validateResponse(response)");
  }

  private TypeSpec classTest(ClassName className, List<MethodSpec> methodSpecs) {
    String methodName = Introspector
        .decapitalize(className.simpleName());

    MethodSpec abstractMethodInstanceToTest = methodBuilder(methodName)
        .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
        .returns(className)
        .build();

    FieldSpec exception = FieldSpec.builder(ExpectedException.class, "exception")
        .addAnnotation(Rule.class)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .initializer("$T.none()", ExpectedException.class)
        .build();

    return TypeSpec.classBuilder(className.simpleName() + "Test_")
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        .addMethod(abstractMethodInstanceToTest)
        .addField(exception)
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", MockeryProcessor.class.getCanonicalName())
            .addMember("comments", "$S", CMessages.codeGenerateWarning())
            .build())
        .addAnnotation(AnnotationSpec.builder(RunWith.class)
            .addMember("value", "$T.class", OrderedRunner.class)
            .build())
        .addMethods(methodSpecs)
        .build();
  }

}
