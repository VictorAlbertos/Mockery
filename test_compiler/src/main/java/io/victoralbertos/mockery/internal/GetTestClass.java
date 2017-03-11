package io.victoralbertos.mockery.internal;

import com.google.auto.common.SuperficialValidation;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.tools.javac.code.Symbol;
import io.victoralbertos.mockery.api.Interceptor;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.api.SkipTest;
import io.victoralbertos.mockery.internal.TestClass.Method;
import io.victoralbertos.mockery.internal.TestClass.Method.Param;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

final class GetTestClass {
  private final InstantiateInterface instantiateInterface;
  private final javax.lang.model.util.Types types;

  GetTestClass(javax.lang.model.util.Types types) {
    this.instantiateInterface = new InstantiateInterface();
    this.types = types;
  }

  TestClass from(Element element) throws ValidationException {
    if (!SuperficialValidation.validateElement(element)) return null;
    if (element.getKind() != ElementKind.INTERFACE) return null;

    ClassName className = ClassName.get((TypeElement) element);
    if (!isAnnotatedWithInterceptor(element)) {
      return null;
    }

    List<Method> methods = getMethods(element);

    for (Method method : methods) {
      if (!method.hasMockery) {
        String message = CMessages
            .noMockeryFoundOnMethod(className, method.name);
        throw new ValidationException(method.element, message);
      }

      for (Param param : method.params) {
        if (!param.hasMockery) {
          String message = CMessages
              .noMockeryFoundOnParam(className, method.name, param.name);
          throw new ValidationException(method.element, message);
        }
      }
    }

    List<String> namesMethods = new ArrayList<>();
    for (Method method : methods) {
      for (String name : namesMethods) {
        if (name.equals(method.name)) {
          String message = CMessages
              .noSupportedOverloadingMethod(className, method.name);
          throw new ValidationException(method.element, message);
        }
      }

      namesMethods.add(method.name);
    }

    return new TestClass(className, element, methods);
  }

  private List<Method> getMethods(Element classElement) {
    List<? extends Element> enclosedElements = classElement.getEnclosedElements();
    List<Method> methods = new ArrayList<>();

    for (Element methodElement : enclosedElements) {
      if (methodElement.getKind() != ElementKind.METHOD) continue;
      Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) methodElement;

      if (skipTest(methodSymbol)) continue;

      String name = methodSymbol.getSimpleName().toString();
      TypeName returnType = TypeName.get(methodSymbol.getReturnType());
      List<Param> params = getParams(methodSymbol);

      Mockery mockery = getAnnotation(methodSymbol, Mockery.class);

      methods.add(new Method(name, methodElement,
          returnType, params, mockery != null));
    }

    return methods;
  }

  private List<Param> getParams(Symbol.MethodSymbol methodSymbol) {
    List<Param> params = new ArrayList<>();
    List<Symbol.VarSymbol> paramsSymbols = methodSymbol.getParameters();

    for (int position = 0; position < paramsSymbols.size(); position++) {
      Symbol.VarSymbol varSymbolParam = paramsSymbols.get(position);
      String name = varSymbolParam.getSimpleName().toString();
      TypeName type = TypeName.get(varSymbolParam.asType());

      boolean isOptional = true;
      Mockery mockery = getAnnotation(varSymbolParam, Mockery.class);

      if (mockery != null) {
        Mockery.Behaviour behaviour = getBehaviourMockery(mockery);
        isOptional = behaviour.isOptional();
      }

      params.add(new Param(name, varSymbolParam,
          position, type, mockery != null, isOptional));
    }

    return params;
  }

  private Mockery.Behaviour getBehaviourMockery(Mockery mockery) {
    try {
      mockery.value();
    } catch (MirroredTypeException e) {
      TypeElement typeElement = (TypeElement) types.asElement(e.getTypeMirror());
      try {
        String className = typeElement.getQualifiedName().toString();
        Class<?> behaviourClass = Class.forName(className);
        Mockery.Behaviour behaviour = (Mockery.Behaviour) instantiateInterface.from(behaviourClass);
        return behaviour;
      } catch (ClassNotFoundException e1) {
        e1.printStackTrace();
      }
    }
    return null;
  }

  private boolean isAnnotatedWithInterceptor(Element element) {
    List<? extends AnnotationMirror> annotationMirrors =
        element.getAnnotationMirrors();

    for (AnnotationMirror annotationMirror : annotationMirrors) {
      Interceptor interceptor = annotationMirror
          .getAnnotationType()
          .asElement()
          .getAnnotation(Interceptor.class);
      if (interceptor != null) return true;
    }

    return false;
  }

  private <A extends Annotation> A getAnnotation(Element element, Class<A> classAnnotation) {
    List<? extends AnnotationMirror> annotationMirrors =
        element.getAnnotationMirrors();

    for (AnnotationMirror annotationMirror : annotationMirrors) {
      A annotation = annotationMirror
          .getAnnotationType()
          .asElement()
          .getAnnotation(classAnnotation);
      if (annotation != null) return annotation;
    }

    return null;
  }

  private boolean skipTest(Element element) {
    List<? extends AnnotationMirror> annotationMirrors =
        element.getAnnotationMirrors();

    for (AnnotationMirror annotationMirror : annotationMirrors) {
      boolean skip = annotationMirror.getAnnotationType()
          .asElement()
          .toString()
          .equals(SkipTest.class.getCanonicalName());

      if (skip) return true;
    }

    return false;
  }

  static class ValidationException extends Exception {
    private final Element element;

    public ValidationException(Element element, String msg) {
      super(msg);
      this.element = element;
    }

    public Element getElement() {
      return element;
    }
  }
}
