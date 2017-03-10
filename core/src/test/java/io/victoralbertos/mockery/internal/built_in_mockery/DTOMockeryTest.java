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

import io.victoralbertos.mockery.api.Metadata;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public final class DTOMockeryTest {
  @Rule public final ExpectedException exception = ExpectedException.none();
  private DTOMockery dtoMockery;

  @Before public void init() {
    dtoMockery = new DTOMockery();
  }

  @Test public void When_Call_Legal_Then_Get_Legal()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTO");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();
    Object[] args = {DTOArgsPass.class.getName()};

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, args, annotation, type);

    Mock mock = (Mock) dtoMockery.legal(metadata);
    assertThat(mock.s1, is(DTOArgsPass.class.getName()));
  }

  @Test public void When_Call_Illegal_Then_Get_Null()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTO");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Object illegal = dtoMockery.illegal(metadata);
    assertNull(illegal);
  }

  @Test public void When_Pass_Validation_Then_Do_Not_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTO");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();
    Object[] args = {DTOArgsPass.class.getName()};

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, args, annotation, type);

    Mock mock = (Mock) dtoMockery.legal(metadata);
    dtoMockery.validate(metadata, mock);
  }

  @Test public void When_Not_Pass_Validation_Then_Throw_Assertion_Error()
      throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTO");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Object object = new Mock("");
    exception.expect(AssertionError.class);
    dtoMockery.validate(metadata, object);
  }

  @Test public void When_Support_Type_Object_Then_Get_Its_Type() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTO");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Type[] types = dtoMockery.supportedTypes(metadata);
    assertThat(types.length, is(1));

    Class clazz = (Class) types[0];
    assertEquals(clazz, Mock.class);
  }

  @Test public void When_Support_Type_List_Then_Get_Its_Type() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTOListMock");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Type[] types = dtoMockery.supportedTypes(metadata);
    assertThat(types.length, is(1));

    ParameterizedType listType = (ParameterizedType) types[0];
    assertEquals(listType.getRawType(), List.class);

    Class mockType = (Class) listType.getActualTypeArguments()[0];
    assertEquals(mockType, Mock.class);
  }

  @Test public void When_Support_Type_Raw_List_Then_Get_Its_Type() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTORawListMock");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Type[] types = dtoMockery.supportedTypes(metadata);
    assertThat(types.length, is(1));

    Class clazz = (Class) types[0];
    assertEquals(clazz, List.class);
  }

  @Test public void When_Support_Type_Map_Then_Get_Its_Type() throws NoSuchMethodException {
    Method method = Providers.class.getDeclaredMethod("DTOMapMockMock");
    DTO annotation = method.getAnnotation(DTO.class);
    Type type = method.getGenericReturnType();

    Metadata<DTO> metadata = new Metadata<>(Providers.class,
        method, null, annotation, type);

    Type[] types = dtoMockery.supportedTypes(metadata);
    assertThat(types.length, is(1));

    ParameterizedType mapType = (ParameterizedType) types[0];
    assertEquals(mapType.getRawType(), Map.class);

    Class stringType = (Class) mapType.getActualTypeArguments()[0];
    assertEquals(stringType, String.class);

    Class mockType = (Class) mapType.getActualTypeArguments()[1];
    assertEquals(mockType, Mock.class);
  }

  private interface Providers {
    @DTOArgs(DTOArgsPass.class)
    String DTOArgs();

    @DTO(DTOPass.class)
    String DTO();

    @DTO(DTOListMock.class)
    String DTOListMock();

    @DTO(DTORawListMock.class)
    String DTORawListMock();

    @DTO(DTOMapMock.class)
    String DTOMapMockMock();
  }

  private static class Mock {
    private final String s1;

    public Mock(String s1) {
      this.s1 = s1;
    }
  }

  static class DTOArgsPass implements DTOArgs.Behaviour<Mock> {

    @Override public Mock legal(Object[] args) {
      return new Mock((String) args[0]);
    }

    @Override public void validate(Mock candidate) throws AssertionError {
      if (!candidate.s1.equals(DTOArgsPass.class.getName())) throw new AssertionError();
    }

  }

  static class DTOPass implements DTO.Behaviour<Mock> {

    @Override public Mock legal() {
      return new Mock(DTOArgsPass.class.getName());
    }

    @Override public void validate(Mock candidate) throws AssertionError {
      if (!candidate.s1.equals(DTOArgsPass.class.getName())) throw new AssertionError();
    }

  }

  static class DTOListMock implements DTO.Behaviour<List<Mock>> {

    @Override public List<Mock> legal() {
      return new ArrayList<>();
    }

    @Override public void validate(List<Mock> candidate) throws AssertionError {}

  }

  static class DTORawListMock implements DTO.Behaviour<List> {

    @Override public List legal() {
      return new ArrayList<>();
    }

    @Override public void validate(List candidate) throws AssertionError {}

  }

  static class DTOMapMock implements DTO.Behaviour<Map<String, Mock>> {

    @Override public Map<String, Mock> legal() { return new HashMap<>();}

    @Override public void validate(Map<String, Mock> candidate) throws AssertionError {}

  }

}
