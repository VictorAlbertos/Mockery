package io.victoralbertos.example_retrofit.presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.victoralbertos.example_retrofit.R;
import io.victoralbertos.example_retrofit.data.Repository;
import io.victoralbertos.example_retrofit.domain.Repo;
import io.victoralbertos.example_retrofit.domain.User;
import java.io.IOException;
import java.util.List;
import library.recycler_view.OkRecyclerViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    setUpSwitchApiMode();
    setUpUser();
    setUpUsers();
    setUpRepos();
  }

  private void setUpSwitchApiMode() {
    findViewById(R.id.bt_real).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Repository.POOL.useReal();
      }
    });

    findViewById(R.id.bt_mock).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Repository.POOL.useMock();
      }
    });
  }

  private void setUpUser() {
    final EditText editText = (EditText)findViewById(R.id.et_user_name);
    final TextView tv_output_user = (TextView)findViewById(R.id.tv_output_user);

    findViewById(R.id.bt_user_call).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        tv_output_user.setText("");
        String userName = editText.getText().toString();

        Repository.POOL
            .getUserByName(userName)
            .enqueue(new Callback<User>() {
              @Override public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() != null) {
                  tv_output_user
                      .setText(response.body().toString());
                } else {
                  showError(response);
                }
              }

              @Override public void onFailure(Call<User> call, Throwable error) {
                Toast.makeText(MainActivity.this,
                    error.getMessage(), Toast.LENGTH_LONG).show();
              }
            });
      }
    });
  }

  private void setUpUsers() {
    final EditText et_last_id_queried = (EditText)findViewById(R.id.et_last_id_queried);
    final EditText et_per_page = (EditText)findViewById(R.id.et_per_page);

    findViewById(R.id.bt_users_call).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        String lastIdQueriedInput = et_last_id_queried.getText().toString();
        int lastIdQueried = lastIdQueriedInput.isEmpty() ? 0
            : Integer.parseInt(lastIdQueriedInput);

        String perPageInput = et_per_page.getText().toString();
        int perPage = perPageInput.isEmpty() ? 50
            : Integer.parseInt(perPageInput);

        Repository.POOL
            .getUsers(lastIdQueried, perPage)
            .enqueue(new Callback<List<User>>() {
              @Override public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                  setUpRecyclerUsers(response.body());
                } else {
                  showError(response);
                }
              }

              @Override public void onFailure(Call<List<User>> call, Throwable error) {
                Toast.makeText(MainActivity.this,
                    error.getMessage(), Toast.LENGTH_LONG).show();
              }
            });
      }
    });
  }

  private void setUpRecyclerUsers(List<User> users) {
    RecyclerView rv_users = (RecyclerView) findViewById(R.id.rv_users);
    LinearLayoutManager layoutManager
        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rv_users.setLayoutManager(layoutManager);

    OkRecyclerViewAdapter<User, UserViewGroup> adapter = new OkRecyclerViewAdapter<User, UserViewGroup>() {
      @Override protected UserViewGroup onCreateItemView(ViewGroup parent, int viewType) {
        return new UserViewGroup(parent.getContext());
      }
    };

    adapter.setAll(users);
    rv_users.setAdapter(adapter);
  }

  private static class UserViewGroup extends FrameLayout implements OkRecyclerViewAdapter.Binder<User> {

    public UserViewGroup(Context context) {
      super(context);

      LayoutInflater
          .from(getContext())
          .inflate(R.layout.item_view_group, this, true);
    }

    @Override public void bind(User user, int position, int count) {
      ((TextView)findViewById(R.id.tv_content))
          .setText(user.toString());
    }

  }

  private void setUpRepos() {
    final EditText et_user_name_repo = (EditText)findViewById(R.id.et_user_name_repo);
    final EditText et_type = (EditText)findViewById(R.id.et_type);
    final EditText et_direction = (EditText)findViewById(R.id.et_direction);

    findViewById(R.id.bt_repos_call).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        String userName = et_user_name_repo.getText().toString();
        String type = et_type.getText().toString();
        String direction = et_direction.getText().toString();

        Repository.POOL
            .getRepos(userName, type, direction)
            .enqueue(new Callback<List<Repo>>() {
              @Override public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
                if (response.body() != null) {
                  setUpRecyclerRepos(response.body());
                } else {
                  showError(response);
                }
              }
              @Override public void onFailure(Call<List<Repo>> call, Throwable error) {
                Toast.makeText(MainActivity.this,
                    error.getMessage(), Toast.LENGTH_LONG).show();
              }
            });
      }
    });
  }

  private void setUpRecyclerRepos(List<Repo> repos) {
    RecyclerView rv_repos = (RecyclerView) findViewById(R.id.rv_repos);
    LinearLayoutManager layoutManager
        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rv_repos.setLayoutManager(layoutManager);

    OkRecyclerViewAdapter<Repo, RepoViewGroup> adapter = new OkRecyclerViewAdapter<Repo, RepoViewGroup>() {
      @Override protected RepoViewGroup onCreateItemView(ViewGroup parent, int viewType) {
        return new RepoViewGroup(parent.getContext());
      }
    };

    adapter.setAll(repos);
    rv_repos.setAdapter(adapter);
  }

  private static class RepoViewGroup extends FrameLayout implements OkRecyclerViewAdapter.Binder<Repo> {

    public RepoViewGroup(Context context) {
      super(context);

      LayoutInflater
          .from(getContext())
          .inflate(R.layout.item_view_group, this, true);
    }

    @Override public void bind(Repo repo, int position, int count) {
      ((TextView)findViewById(R.id.tv_content))
          .setText(repo.toString());
    }
  }

  private void showError(Response<?> response) {
    try {
      Toast.makeText(MainActivity.this,
          response.errorBody().string(), Toast.LENGTH_LONG).show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
