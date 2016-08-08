package at.favre.app.bankathon16.childapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import at.favre.app.bankathon16.BankathonApplication;
import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.Util;
import at.favre.app.bankathon16.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildPaymentActivity extends AppCompatActivity {
    private static final String TAG = ChildPaymentActivity.class.getSimpleName();

    private static final String KEY_USERID = "USERID";
    private long userId;
    private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] mTechLists;

    public static void start(Context context, long userId) {
        Intent starter = new Intent(context, ChildPaymentActivity.class);
        starter.putExtra(KEY_USERID, userId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_pay_terminal);
        userId = getIntent().getLongExtra(KEY_USERID, 2L);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFiltersArray = new IntentFilter[]{ndef,};
        mTechLists = new String[][]{new String[]{MifareClassic.class.getName()}};

        ImageView imageView = (ImageView) findViewById(R.id.icon);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        imageView.startAnimation(pulse);

    }

    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, mTechLists);
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        if (tagFromIntent != null) {

            Log.d(TAG, tagFromIntent.toString());

            Long tagId = 0L;
            for (int i = 0; i < tagFromIntent.getId().length; i++) {
                tagId += ((long) tagFromIntent.getId()[i] & 0xffL) << (8 * i);
            }
            Log.d(TAG, "tag id: " + tagId);

            Integer amountInCent = 100;
            String merchant = "Hofer";

            switch (tagId.toString()) {
                case "2472559186": //rot
                    amountInCent = 150;
                    merchant = "Forbidden";
                    break;
                case "2482605570": //grau
                    amountInCent = 231;
                    merchant = "ToysRUs";
                    break;
                case "2482435794": //schwarz
                    amountInCent = 334;
                    merchant = "ToysRUs";
                    break;
                case "2472115234": //blau
                    amountInCent = 42;
                    merchant = "ToysRUs";
                    break;
                case "2471922914": //gelb
                    amountInCent = 576;
                    merchant = "ToysRUs";
                    break;
            }

            if(merchant.equals("Forbidden")) {
                Toast.makeText(ChildPaymentActivity.this, "Du darfst bei diesem Geschäft nicht einkaufen!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            final Integer selectedAmount = amountInCent;
            ((BankathonApplication) getApplication()).getDataAccess().pay(userId, amountInCent).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.body() != null) {
                        //TODO: Show successful payment
                        Toast.makeText(ChildPaymentActivity.this, String.format("Du hast um %s eingekauft", Util.formatWithCurrencyCode(selectedAmount, "EUR")), Toast.LENGTH_LONG).show();
                        finish();
                    } else if (response.errorBody() != null) {
                        if(response.code() == 412) {
                            Toast.makeText(ChildPaymentActivity.this, R.string.not_enough_money, Toast.LENGTH_LONG).show();
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
                    Log.e(TAG, "Network error", t);
                }
            });
        }
    }
}
