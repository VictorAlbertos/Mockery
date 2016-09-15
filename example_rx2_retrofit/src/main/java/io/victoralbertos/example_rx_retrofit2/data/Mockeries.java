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

package io.victoralbertos.example_rx_retrofit2.data;

import io.victoralbertos.example_rx_retrofit2.domain.Repo;
import io.victoralbertos.example_rx_retrofit2.domain.User;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;

public class Mockeries {

  public static class UserDTO implements DTOArgs.Behaviour<User> {
    @Override public User legal(Object[] args) {
      String userName = (String) args[0];
      User user = new User(1, userName);
      return user;
    }

    @Override public void validate(User candidate) throws AssertionError {
      MatcherAssert.assertThat(candidate, notNullValue());
      MatcherAssert.assertThat(candidate.getId(), is(not(0)));
      MatcherAssert.assertThat(candidate.getLogin().isEmpty(), is(not(true)));
    }
  }

  public static class UsersDTO implements DTOArgs.Behaviour<List<User>> {
    @Override public List<User> legal(Object[] args) {
      List<User> users = new ArrayList<>();

      int perPage = (int) args[1];
      for (int i = 0; i <= perPage; i++) {
        users.add(new User(i, "User "+ i));
      }

      return users;
    }

    @Override public void validate(List<User> candidate) throws AssertionError {
      MatcherAssert.assertThat(candidate, notNullValue());
      MatcherAssert.assertThat(candidate.isEmpty(), is(not(true)));

      User user = candidate.get(0);
      MatcherAssert.assertThat(user.getId(), is(not(0)));
      MatcherAssert.assertThat(user.getLogin().isEmpty(), is(not(true)));
    }
  }

  public static class ReposDTO implements DTO.Behaviour<List<Repo>> {
    @Override public List<Repo> legal() {
      List<Repo> repos = new ArrayList<>();
      repos.add(new Repo(1, "Repo 1 "));
      repos.add(new Repo(2, "Repo 2 "));
      repos.add(new Repo(3, "Repo 3 "));
      repos.add(new Repo(4, "Repo 4 "));
      return repos;
    }

    @Override public void validate(List<Repo> candidate) throws AssertionError {
      MatcherAssert.assertThat(candidate, notNullValue());
      MatcherAssert.assertThat(candidate.isEmpty(), is(not(true)));

      Repo repo = candidate.get(0);
      MatcherAssert.assertThat(repo.getId(), is(not(0)));
      MatcherAssert.assertThat(repo.getName().isEmpty(), is(not(true)));
    }
  }

}
