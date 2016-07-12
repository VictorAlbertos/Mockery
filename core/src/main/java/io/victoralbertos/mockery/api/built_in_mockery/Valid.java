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

package io.victoralbertos.mockery.api.built_in_mockery;

import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.internal.built_in_mockery.ValidMockery;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, METHOD})
@Mockery(ValidMockery.class)
/**
 * Decorate a method/param with this annotation to supply mocking and validation specs for those types
 * listed as supported on {@link ValidMockery#supportedTypes(Metadata)}.
 * To use it, decorate the method/param with this annotation.
 */
public @interface Valid {
  /**
   * The regular expression for creating and validating objects. See {@link Template} for built-in patterns.
   */
  String value();

  /**
   * If set, it will be the legal value provided from {@link ValidMockery#legal(Metadata)}
   */
  String legal() default "";

  /**
   * If set, it will be the illegal value provided from {@link ValidMockery#illegal(Metadata)}
   */
  String illegal() default "";

  interface Template {
    String EMAIL ="[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+";

    String PHONE ="(\\+[0-9]+[\\- \\.]*)?"
        + "(\\([0-9]+\\)[\\- \\.]*)?"
        + "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])";


    String URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    /**
     * A legal string
     */
    String STRING = "^[a-zA-Z0-9]+$";

    /**
     * A legal int
     */
    String INT = "^^[-+]?\\d+$";

    /**
     * A legal positive number excluding 0.
     */
    String ID = "^[1-9]\\d*$";

    /**
     * A legal positive number or negative number:
     */
    String NUMBER = "^-?\\d*\\.{0,1}\\d+$";
  }

}
