package at.favre.app.bankathon16.childapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import at.favre.app.bankathon16.BankathonApplication;
import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.Util;
import at.favre.app.bankathon16.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PatrickF on 23.04.2016.
 */
public class ChildSaveActivity extends AppCompatActivity {

    private static final String KEY_SAVEAMOUNT = "SAVEAMOUNT";
    private static final String KEY_CURRENTAMOUNT = "CURRENTAMOUNT";

    private static final String TAG = ChildSaveActivity.class.getSimpleName();
    private long userId;
    private int saveAmount;
    private int currentAmount;

    public static void start(Context context, int currentAmount, int saveAmount) {
        Intent starter = new Intent(context, ChildSaveActivity.class);
        starter.putExtra(KEY_SAVEAMOUNT, saveAmount);
        starter.putExtra(KEY_CURRENTAMOUNT, currentAmount);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_save);

        saveAmount = getIntent().getIntExtra(KEY_SAVEAMOUNT, 0);
        currentAmount = getIntent().getIntExtra(KEY_CURRENTAMOUNT, 0);

        userId = ((BankathonApplication) getApplication()).getPreferencesHandler().getChildUserId();
        ((TextView) findViewById(R.id.tv_hallo)).setText(((BankathonApplication) getApplication()).getPreferencesHandler().getSavingGoalName());

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMoneyDialog.show(getFragmentManager(), currentAmount);
            }
        });

        findViewById(R.id.bar_current).postDelayed(new Runnable() {
            @Override
            public void run() {
                updateView(saveAmount);
            }
        },300);
//        updateView(saveAmount);
    }

    public void updateSavedAmount(int centAmount) {
        ((BankathonApplication) getApplication()).getDataAccess().saveMoney(userId, centAmount).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() != null) {
//                    Toast.makeText(ChildSaveActivity.this, R.string.enter_amount_success, Toast.LENGTH_LONG).show();
                    saveAmount = response.body().getSavedAmount();
                    currentAmount = response.body().getAmountInCent();
                    updateView(response.body().getSavedAmount());
                    setResult(RESULT_OK);
                } else if (response.errorBody() != null) {
                    if(response.code() == 412) {
                        Toast.makeText(ChildSaveActivity.this, R.string.not_enough_money, Toast.LENGTH_LONG).show();
                    }
                    try {
                        Log.e(TAG, "Server error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "Server error: cannot read errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ChildSaveActivity.this, R.string.enter_amount_failure, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void updateView(final int centAmount) {
        final long goalAmount = ((BankathonApplication) getApplication()).getPreferencesHandler().getSavingGoalAmount();

        ((TextView) findViewById(R.id.tv_goal)).setText(Util.formatWithCurrencyCode(goalAmount, "€"));
        findViewById(R.id.btn_save).setEnabled(currentAmount > 0);
        findViewById(R.id.btn_save).setAlpha(currentAmount > 0 ? 1f : 0.4f);

        findViewById(R.id.bar_bg).postDelayed(new Runnable() {
            @Override
            public void run() {
                float precent = Math.min(1f, (float) centAmount / (float) goalAmount);

                int height = Math.round((float) findViewById(R.id.bar_bg).getHeight() * precent);
                View barCurrent = findViewById(R.id.bar_current);
                expand(barCurrent, height);
                ((TextView) findViewById(R.id.tv_saved)).invalidate();
                ((TextView) findViewById(R.id.tv_saved)).setText(Util.formatWithCurrencyCode(centAmount, "€"));

                if (precent < 0.08f) {
                    findViewById(R.id.tv_saved).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.tv_saved).setVisibility(View.VISIBLE);
                }

                if (precent > 0.9f) {
                    findViewById(R.id.tv_goal).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.tv_goal).setVisibility(View.VISIBLE);
                }
            }
        }, 8);


    }

    public void expand(final View v, final int targetHeight) {
//        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        if (v.getLayoutParams().height == 0) {
            v.getLayoutParams().height = 1;
        }
        final int initialHeight = v.getLayoutParams().height;
        Log.d("PLEASE REMOVE", "init: " + initialHeight);
        final int diff = targetHeight - initialHeight;
        Log.d("PLEASE REMOVE", "diff: " + diff);
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = interpolatedTime == 1
                        ? targetHeight
                        : initialHeight + (int) (diff * interpolatedTime);
                v.setLayoutParams(layoutParams);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
//        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(800);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        v.startAnimation(a);
    }

}
