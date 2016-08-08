package at.favre.app.bankathon16.network.gcm;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

public final class GcmIDListenerService extends InstanceIDListenerService {

    private static final String TAG = GcmIDListenerService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        final GcmManager gcmManager = new GcmManager(getApplicationContext());

        gcmManager.updateToken(new GcmManager.GetTokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.i(TAG, "update gcm push token");
            }

            @Override
            public void onError(Exception e) {
                Log.w(TAG, "update gcm push token failed! clear caches", e);
                gcmManager.clearCaches();
            }
        });

    }
}