package io.victoralbertos.example_rx_retrofit.presentation;

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
import io.victoralbertos.example_rx_retrofit.R;
import io.victoralbertos.example_rx_retrofit.data.Repository;
import io.victoralbertos.example_rx_retrofit.domain.Repo;
import io.victoralbertos.example_rx_retrofit.domain.User;
import java.util.List;
import miguelbcr.ok_adapters.recycler_view.OkRecyclerViewAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<User>() {
              @Override public void call(User user) {
                tv_output_user.setText(user.toString());
              }
            }, new Action1<Throwable>() {
              @Override public void call(Throwable throwable) {
                Toast.makeText(MainActivity.this,
                    throwable.getMessage(), Toast.LENGTH_LONG).show();
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<User>>() {
              @Override public void call(List<User> users) {
                setUpRecyclerUsers(users);
              }
            }, new Action1<Throwable>() {
              @Override public void call(Throwable throwable) {
                Toast.makeText(MainActivity.this,
                    throwable.getMessage(), Toast.LENGTH_LONG).show();
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Repo>>() {
              @Override public void call(List<Repo> repos) {
                setUpRecyclerRepos(repos);
              }
            }, new Action1<Throwable>() {
              @Override public void call(Throwable throwable) {
                Toast.makeText(MainActivity.this,
                    throwable.getMessage(), Toast.LENGTH_LONG).show();
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

}
