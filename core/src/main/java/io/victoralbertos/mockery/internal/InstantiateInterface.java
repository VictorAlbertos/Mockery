package io.victoralbertos.mockery.internal;

import java.lang.reflect.Constructor;

public final class InstantiateInterface {

  public <T> T from(Class<T> aClass) {
    try {
      return aClass.newInstance();
    } catch (Exception ignore) {
      try {
        Constructor<T> constructor = aClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

}
