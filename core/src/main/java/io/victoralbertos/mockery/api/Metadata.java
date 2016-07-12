package io.victoralbertos.mockery.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Wrap the data related with the current method call being process.
 */
public final class Metadata<A extends Annotation> {
  /**
   * The class been mocked.
   */
  private final Class<?> mockingClass;
  /**
   * The associated method where the mockery was annotated.
   */
  private final Method method;

  /**
   * An array of objects containing the values of the
   * arguments passed in the method where the mockery was annotated.
   * Note that this array will be null when it be called from an auto generated unit test.
   */
  private final Object[] args;

  /**
   * The associated annotation.
   */
  private final A annotation;

  /**
   * The associated type,
   *  if mockery was annotated on type param, the type of the param.
   *  if mockery was annotated on type method, the type of the method.
   */
  private final Type type;

  public Metadata(Class<?> mockingClass, Method method, Object[] args, A annotation, Type type) {
    this.mockingClass = mockingClass;
    this.method = method;
    this.args = args;
    this.annotation = annotation;
    this.type = type;
  }

  public Class<?> getMockingClass() {
    return mockingClass;
  }

  public Method getMethod() {
    return method;
  }

  public Object[] getArgs() {
    return args;
  }

  public A getAnnotation() {
    return annotation;
  }

  public Type getType() {
    return type;
  }
}
