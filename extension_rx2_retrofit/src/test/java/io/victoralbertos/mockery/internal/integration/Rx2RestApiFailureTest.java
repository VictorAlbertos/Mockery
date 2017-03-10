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

package io.victoralbertos.mockery.internal.integration;

import io.reactivex.observers.TestObserver;
import io.victoralbertos.mockery.api.Mockery;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public final class Rx2RestApiFailureTest {
  private Rx2RestApiFailure restApi;

  @Before public void init() {
    restApi = new Mockery.Builder<Rx2RestApiFailure>()
        .mock(Rx2RestApiFailure.class)
        .build();
  }

  @Test public void model_fails() {
    fails(restApi.model().test());
  }

  @Test public void completableModel_fails() {
    fails(restApi.completableModel().test());
  }

  @Test public void modelResponse_fails() {
    fails(restApi.modelResponse().test());
  }

  private void fails(TestObserver<?> testObserver) {
    testObserver.awaitTerminalEvent();
    testObserver.assertNoValues();
    testObserver.assertNotComplete();
    testObserver.assertError(IOException.class);
  }
}
