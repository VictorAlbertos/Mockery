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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.mock.NetworkBehavior;

final class CallAdapter {
  private final retrofit2.Retrofit retrofit;

  public CallAdapter(retrofit2.Retrofit retrofit) {
    this.retrofit = retrofit;
  }

  <T> T adapt(Method method, NetworkBehavior networkBehavior, Call<?> call) {
    try {
      Class<?> behaviorCallClass = Class.forName("retrofit2.mock.BehaviorCall");

      Constructor<?> constructor = behaviorCallClass.getDeclaredConstructor(NetworkBehavior.class,
          ExecutorService.class, Call.class);
      constructor.setAccessible(true);

      Call<?> behaviorCall = (Call<?>) constructor.newInstance(networkBehavior,
          Executors.newCachedThreadPool(),
          call);

      return (T) retrofit
          .callAdapter(method.getGenericReturnType(), method.getAnnotations())
          .adapt(behaviorCall);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
