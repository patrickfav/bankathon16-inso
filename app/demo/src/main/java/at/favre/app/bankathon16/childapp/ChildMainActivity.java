package at.favre.app.bankathon16.childapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import at.favre.app.bankathon16.BankathonApplication;
import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.Util;
import at.favre.app.bankathon16.misc.bus.EventBus;
import at.favre.app.bankathon16.misc.bus.Result;
import at.favre.app.bankathon16.model.User;
import at.favre.app.bankathon16.network.gcm.GcmIntentService;
import at.favre.app.bankathon16.network.gcm.GcmManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildMainActivity extends AppCompatActivity {
    private static final String TAG = ChildMainActivity.class.getSimpleName();

    private static final String STATE_AMOUNT = ChildMainActivity.class.getSimpleName() + "_STATE_AMOUNT";

    private long userId;
    private User user;
    private Integer amountInCent = 0;

    private EventBus.EventHandler handler = new EventBus.EventHandler() {
        @Override
        public void onEventReceive(String topic, @Result.Type int resultType, Intent rawIntent) {
            updateUser(true);
        }
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, ChildMainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_main);

        if (savedInstanceState != null) {
            amountInCent = savedInstanceState.getInt(STATE_AMOUNT);
        }

        userId = ((BankathonApplication) getApplication()).getPreferencesHandler().getChildUserId();

        EventBus.get(this).registerHandler(handler, GcmIntentService.SEND_MONEY);

//        ((TextView) findViewById(R.id.tv_sum)).setText(Util.formatWithCurrencyCode(0L, "€"));

        findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildPaymentActivity.start(ChildMainActivity.this, userId);
            }
        });
        Log.i(TAG, "start as child " + userId);

        findViewById(R.id.sum_label).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((BankathonApplication) getApplication()).getPreferencesHandler().clear();
                finish();
                ChildSplashActivity.start(ChildMainActivity.this);
                return true;
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((BankathonApplication) getApplication()).getPreferencesHandler().getSavingGoalName() != null) {
                    ChildSaveActivity.start(ChildMainActivity.this, user.getAmountInCent(), user.getSavedAmount());
                } else {
                    showSavingGoalDialog();

                }
                //
            }
        });

        if (((BankathonApplication) getApplication()).getPreferencesHandler().getSavingGoalName() != null) {
            ((Button) findViewById(R.id.btn_save)).setText("Mein Spar-Ziel");
        } else {
            ((Button) findViewById(R.id.btn_save)).setText("Neues Spar-Ziel");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_AMOUNT, amountInCent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BankathonApplication) getApplication()).initGcm(new GcmManager.GetTokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                ((BankathonApplication) getApplication()).getDataAccess().setGcmId(userId, token).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.body() != null) {
                            Log.d(TAG, "gcm successfully send to server");
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

        findViewById(R.id.tv_sum).postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUser(!((TextView) findViewById(R.id.tv_sum)).getText().toString().isEmpty());
            }
        }, 300);

    }

    private void updateUser(final boolean animate) {
        ((BankathonApplication) getApplication()).getDataAccess().getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                onUserResponse(response, animate);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Network error", t);
            }
        });
    }

    private void onUserResponse(Response<User> response, boolean animate) {
        if (response.body() != null) {
            user = response.body();
            ((TextView) findViewById(R.id.sum_label)).setText("Hallo " + user.getName() + "!\nSo viel kannst du ausgeben:");
            if (!animate) {
                amountInCent = user.getAmountInCent();
                TextView tvSum = ((TextView) findViewById(R.id.tv_sum));
                tvSum.setAlpha(0f);
                tvSum.setText(Util.formatWithCurrencyCode(amountInCent, "€"));
                tvSum.animate().alpha(1f).setDuration(500).start();
            } else {
                final Integer newAmountInCent = user.getAmountInCent();
                final Integer diff = newAmountInCent - amountInCent;
                if (diff != 0) {
                    final TextView addSum = ((TextView) findViewById(R.id.tv_sum_add));
                    String addSumText = diff > 0 ? "+" : "";
                    addSum.setText(addSumText + Util.formatWithCurrencyCode(diff, "€"));
                    addSum.setAlpha(1f);
                    addSum.animate().translationYBy(addSum.getHeight()).alpha(0f).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            amountInCent = newAmountInCent;
                            ((TextView) findViewById(R.id.tv_sum)).setText(Util.formatWithCurrencyCode(amountInCent, "€"));
                            addSum.setTranslationY(0);
                        }
                    });
                }
            }
        } else if (response.errorBody() != null) {
            try {
                Log.e(TAG, "Server error: " + response.errorBody().string());
            } catch (IOException e) {
                Log.e(TAG, "Server error: cannot read errorBody", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get(this).unregisterHandler(handler);
    }


    public void createSaveGoal(String name, long price) {
        if (!name.isEmpty() && price > 0) {
            ((BankathonApplication) getApplication()).getPreferencesHandler().setSavingGoal(name, price);
            ChildSaveActivity.start(this, user.getAmountInCent(), user.getSavedAmount());

        } else {
            Toast.makeText(this, "Gib bitte einen Namen und einen Preis ein.", Toast.LENGTH_LONG).show();
            showSavingGoalDialog();
        }
    }

    private void showSavingGoalDialog() {
        SavingGoalDialog.show(getFragmentManager());
    }
}
