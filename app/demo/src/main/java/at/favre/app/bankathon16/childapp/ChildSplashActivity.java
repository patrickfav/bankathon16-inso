package at.favre.app.bankathon16.childapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import at.favre.app.bankathon16.BankathonApplication;
import at.favre.app.bankathon16.R;

/**
 * Created by PatrickF on 23.04.2016.
 */
public class ChildSplashActivity extends AppCompatActivity {

    private static final String TAG = ChildSplashActivity.class.getSimpleName();
    private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] mTechLists;

    public static void start(Context context) {
        Intent starter = new Intent(context, ChildSplashActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((BankathonApplication) getApplication()).getPreferencesHandler().isChildRegistered()) {
            ChildMainActivity.start(this);
            this.finish();

        } else {
            setContentView(R.layout.activity_child_splash);

            findViewById(R.id.iv_sparsau).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((BankathonApplication) getApplication()).getPreferencesHandler().setChildUserId(2l);

                    ChildMainActivity.start(ChildSplashActivity.this);
                    ChildSplashActivity.this.finish();
                    return true;
                }
            });
        }

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef,};
        mTechLists = new String[][]{new String[]{Ndef.class.getName()}};
    }

    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, mTechLists);
        }
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        if (tagFromIntent != null) {

            Log.d(TAG, tagFromIntent.toString());
            NdefRecord record = Ndef.get(tagFromIntent).getCachedNdefMessage().getRecords()[0];
            byte[] userIdBytes = new byte[record.getPayload().length - 3];
            System.arraycopy(record.getPayload(), 3, userIdBytes, 0, record.getPayload().length - 3);
            Long userId = new Long(new String(userIdBytes));
            Log.d(TAG, "User ID: " + userId);

            ((BankathonApplication) getApplication()).getPreferencesHandler().setChildUserId(userId);
            ChildMainActivity.start(this);
        }
    }

}
