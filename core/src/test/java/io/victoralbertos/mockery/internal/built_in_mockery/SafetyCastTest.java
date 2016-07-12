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

package io.victoralbertos.mockery.internal.built_in_mockery;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public final class SafetyCastTest {
  private SafetyCast safetyCast;

  @Before public void init() {
    safetyCast = new SafetyCast();
  }

  @Test public void When_int_Then_Get_int() {
    Object object = "4";
    int result = (int) safetyCast.with(object, int.class);
    assertThat(result, is(4));
  }

  @Test public void When_Integer_Then_Get_Integer() {
    Object object = "12";
    Integer result = (Integer) safetyCast.with(object, Integer.class);
    assertThat(result, is(12));
  }

  @Test public void When_long_Then_Get_long() {
    Object object = "98";
    long result = (long) safetyCast.with(object, long.class);
    assertThat(result, is(98l));
  }

  @Test public void When_Long_Then_Get_Long() {
    Object object = "34";
    Long result = (Long) safetyCast.with(object, Long.class);
    assertThat(result, is(34l));
  }

  @Test public void When_float_Then_Get_float() {
    Object object = "66";
    float result = (float) safetyCast.with(object, float.class);
    assertThat(result, is(66f));
  }

  @Test public void When_Float_Then_Get_Float() {
    Object object = "43";
    Float result = (Float) safetyCast.with(object, Float.class);
    assertThat(result, is(43f));
  }

  @Test public void When_double_Then_Get_double() {
    Object object = "2";
    double result = (double) safetyCast.with(object, double.class);
    assertThat(result, is(2d));
  }

  @Test public void When_Double_Then_Get_Double() {
    Object object = "23";
    Double result = (Double) safetyCast.with(object, Double.class);
    assertThat(result, is(23d));
  }

  @Test public void When_String_Then_Get_String() {
    Object object = "345";
    String result = (String) safetyCast.with(object, String.class);
    assertThat(result, is("345"));
  }

}
