package at.favre.app.bankathon16.parentsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import at.favre.app.bankathon16.BankathonApplication;
import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.bus.EventBus;
import at.favre.app.bankathon16.misc.bus.Result;
import at.favre.app.bankathon16.model.User;
import at.favre.app.bankathon16.model.UserType;
import at.favre.app.bankathon16.network.gcm.GcmIntentService;
import at.favre.app.bankathon16.network.gcm.GcmManager;
import at.favre.app.bankathon16.parentsapp.adapter.ChildListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class ParentMainActivity extends AppCompatActivity {

    private static final String TAG = ParentMainActivity.class.getName();

    private static final int GIEV_MONEYS_REQUEST = 7499;

    private Toolbar toolbar;
    private ChildListAdapter mAdapter;
    private RecyclerView recyclerView;
    private TextView recyclerChildrenEmpty;
    private EventBus.EventHandler eventHandler = new EventBus.EventHandler() {
        @Override
        public void onEventReceive(String topic, @Result.Type int resultType, Intent rawIntent) {
            loadChildren();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meine Kinder");

        recyclerChildrenEmpty = (TextView) findViewById(R.id.recyclerChildrenEmpty);
        recyclerChildrenEmpty.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerChildren);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddChildDialog.show(getFragmentManager());
            }
        });

        initRecyclerView();
        loadChildren();

        EventBus.get(this).registerHandler(eventHandler, GcmIntentService.CHILD_PAYMENT);

    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChildListAdapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    public void addNewChild(@NonNull String name) {
        Log.wtf(TAG, "Adding new kid " + name);

        User user = new User();
        user.setName(name);

        ((BankathonApplication) getApplication()).getDataAccess().createChild(user).enqueue(
                new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.body() != null) {
                            mAdapter.clear();
                            User userResponse = response.body();
                            NfcRegisterChildActivity.start(ParentMainActivity.this, userResponse.getName(), userResponse.getId());
                            loadChildren();
                        } else if (response.errorBody() != null) {
                            try {
                                Log.e(TAG, "Server error: " + response.errorBody().string());
                            } catch (IOException e) {
                                Log.e(TAG, "Server error: cannot read errorBody", e);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(ParentMainActivity.this, R.string.server_error_create_child, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "addNewChild failed", t);
                    }
                }
        );

    }

    private void loadChildren() {
        ((BankathonApplication) getApplication()).getDataAccess().getAllChilds().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    mAdapter.clear();
                    for (User user : response.body()) {
                        if (UserType.CHILD.equals(user.getUserType())) {
                            mAdapter.add(user);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                    recyclerChildrenEmpty.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);

                } else if (response.errorBody() != null) {
                    try {
                        Log.e(TAG, "Server error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "Server error: cannot read errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "Network error", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BankathonApplication) getApplication()).initGcm(new GcmManager.GetTokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                ((BankathonApplication) getApplication()).getDataAccess().setGcmId(1, token).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.body() != null) {
                            Log.d(TAG, "gcm for parent successfully send to server");
                        } else if (response.errorBody() != null) {
                            try {
                                Log.e(TAG, "Server error: " + response.errorBody().string());
                            } catch (IOException e) {
                                Log.e(TAG, "Server error: cannot read errorBody", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Network error", t);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "error token call", e);
            }
        });
    }


    public void onChildTouched(User user) {
        Intent intent = EnterAmountActivity.start(this, user.getId(), user.getName());
        startActivityForResult(intent, GIEV_MONEYS_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GIEV_MONEYS_REQUEST) {
            loadChildren();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get(this).unregisterHandler(eventHandler);
    }
}
