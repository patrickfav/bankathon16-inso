package at.favre.app.bankathon16.parentsapp;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import at.favre.app.bankathon16.R;

/**
 * Created by PatrickF on 22.04.2016.
 */
public class NfcRegisterChildActivity extends AppCompatActivity {
    private static final String TAG = NfcRegisterChildActivity.class.getSimpleName();

    private static final String KEY_REGISTER_ID = "KEY_REGISTER_ID";
    private static final String KEY_NAME = "NAME";
    private NfcAdapter nfcAdapter;
    private Long registerId;
    private String name;

    public static void start(Context context, String name, long registerId) {
        Intent starter = new Intent(context, NfcRegisterChildActivity.class);
        starter.putExtra(KEY_REGISTER_ID, registerId);
        starter.putExtra(KEY_NAME, name);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_nfc_register_child);
        registerId = getIntent().getLongExtra(KEY_REGISTER_ID, 2L);
        name = getIntent().getStringExtra(KEY_NAME);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.setNdefPushMessageCallback(new SendNFcMsgCallback(), this);
        nfcAdapter.setOnNdefPushCompleteCallback(new NfcAdapter.OnNdefPushCompleteCallback() {
            @Override
            public void onNdefPushComplete(NfcEvent nfcEvent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NfcRegisterChildActivity.this, "Registrierung erfolgreich", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        }, this);

        ((TextView) findViewById(R.id.tv_hallo)).setText(name + "'s App registrieren");

        ImageView imageView = (ImageView) findViewById(R.id.icon);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse2);
        imageView.startAnimation(pulse);

        ImageView imageView2 = (ImageView) findViewById(R.id.icon2);
        Animation pulse2 = AnimationUtils.loadAnimation(this, R.anim.pulse2_reverese);
        imageView2.startAnimation(pulse2);
    }

    private class SendNFcMsgCallback implements NfcAdapter.CreateNdefMessageCallback {

        @Override
        public NdefMessage createNdefMessage(NfcEvent event) {
            try {
                Log.d(TAG, "send ndef msg over nfc: " + registerId);
                NdefRecord ndefRecord = NdefRecord.createTextRecord(null, registerId.toString());
                return new NdefMessage(ndefRecord);
            } catch (Exception e) {
                Log.e(TAG, "NDEF Exception", e);
                throw new IllegalStateException("could not create nfc msg");
            }
        }
    }
}
