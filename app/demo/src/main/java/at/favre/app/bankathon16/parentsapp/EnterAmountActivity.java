package at.favre.app.bankathon16.parentsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.favre.app.bankathon16.BankathonApplication;
import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.Util;
import at.favre.app.bankathon16.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterAmountActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_NAME = "NAME";
    public static final String KEY_ID = "ID";

    private static final String TAG = EnterAmountActivity.class.getSimpleName();

    private TextView receiverName;
    private EditText mAmountText;
    private Button btnSendMoney;
    private CheckBox weekly;

    private long userId;
    private String name;

    public static Intent start(Context context, long id, String name) {
        Intent starter = new Intent(context, EnterAmountActivity.class);
        starter.putExtra(KEY_NAME, name);
        starter.putExtra(KEY_ID, id);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_enter_amount);

        userId = getIntent().getLongExtra(KEY_ID, 0L);
        name = getIntent().getStringExtra(KEY_NAME);

        receiverName = (TextView) findViewById(R.id.tv_amount_title);
        receiverName.setText(String.format(getString(R.string.enter_amount_send_money_to_name), name));

        mAmountText = (EditText) findViewById(R.id.et_amount);
        // mAmountText.addTextChangedListener(new CurrencyTextWatcher(mAmountText));

        btnSendMoney = (Button) findViewById(R.id.btn_send_money);
        btnSendMoney.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btnSendMoney) {
            int cents = 0;
            try {
                cents = (int) Util.getMoneyFromET(mAmountText);

            } catch (NumberFormatException e) {
                Log.wtf(TAG, "Can't touch this");
            }

            if (cents > 0 && cents < 10_000 * 100) {

                ((BankathonApplication) getApplication()).getDataAccess().sendMoney(userId, cents).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(EnterAmountActivity.this, R.string.enter_amount_success, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        EnterAmountActivity.this.finish();

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(EnterAmountActivity.this, R.string.enter_amount_failure, Toast.LENGTH_LONG).show();

                    }
                });

            } else {
                Toast.makeText(this, R.string.enter_amount_wrong_number, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
