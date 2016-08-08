package at.favre.app.bankathon16.network.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import at.favre.app.bankathon16.BuildConfig;
import at.favre.app.bankathon16.R;

/**
 * GcmHelper provides encapsulated functionality for gcm registration handling.
 * Uses InstanceID - needs Google Play Services 7.5.0 or higher
 * <p/>
 * for internal usage only
 *
 * @since 2.0.0
 */
public final class GcmManager {

    private final static String TAG = GcmManager.class.getSimpleName();

    private final Context context;

    private final static String GCM_PUSH_PREFS_TOKEN = GcmManager.class.getPackage() + ".GCM_PUSH_PREFS_TOKEN";
    private final SharedPreferences sharedPreferences;

    /**
     * Uses predefined shared preferences
     *
     * @param context Android Context
     */
    public GcmManager(Context context) {
        this(context, context.getSharedPreferences(context.getPackageName() + "_GCM_", Context.MODE_PRIVATE));
    }

    /**
     * @param context
     * @param sharedPreferences to use for saving gcm token
     */
    public GcmManager(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * Returns a stable identifier that uniquely identifies the app instance.
     *
     * @return unique app instance identifier
     */
    public String getInstanceID() {
        return InstanceID.getInstance(context).getId();
    }

    /**
     * Gets GCM push Token asynchronously from GCM Backend and caches it locally
     * caches token locally
     * <p/>
     * <p/>
     * on error, exponential backoff strategy should be used to retry updateToken
     *
     * @param callback optional callback
     */
    public void updateToken(final GetTokenCallback callback) {
        Log.d(TAG, "update token");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String token = InstanceID.getInstance(context).getToken(context.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    setCachedToken(token);
                    Log.d(TAG, "token received: " + token);
                    if (callback != null) {
                        callback.onTokenReceived(token);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "could not generate gcm push token", e);
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            }

        });
        thread.start();
    }


    /**
     * Deletes gcm push token. App won't receive any push messages anymore.
     * clears cache
     *
     * @param callback optional callback
     */
    public void deleteToken(final DeleteTokenCallback callback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InstanceID.getInstance(context).deleteToken(BuildConfig.GCM_PROJECT_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    if (callback != null) {
                        callback.onTokenDeleted();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "could not delete gcm push token", e);
                    if (callback != null) {
                        callback.onError();
                    }
                }
                setCachedToken(null);
            }
        });
        thread.start();

    }

    /**
     * @param value caches token in shared prefs
     */
    private void setCachedToken(String value) {
        sharedPreferences.edit().putString(GCM_PUSH_PREFS_TOKEN, value).apply();
    }

    /**
     * updateToken caches gcm push token automatically
     *
     * @return last cached gcm push token
     */
    public String getCachedToken() {
        return sharedPreferences.getString(GCM_PUSH_PREFS_TOKEN, null);
    }


    /**
     * @return true iff app as a cached gcm push token
     */
    public boolean hasCachedToken() {
        Log.v(TAG, "gcm has cached token: " + getCachedToken());
        return getCachedToken() != null;
    }

    /**
     * deletes cached token
     */
    public void clearCaches() {
        setCachedToken(null);
    }

    /**
     * callback for async getToken
     *
     * @since 2.0.0
     */
    public interface GetTokenCallback {
        /**
         * @param token received gcm push token
         */
        void onTokenReceived(String token);

        /**
         * could not receive gcm push token
         */
        void onError(Exception e);
    }

    /**
     * callback for async getToken
     *
     * @since 2.0.0
     */
    public interface DeleteTokenCallback {
        /**
         * called if gcm push token was deleted successfully
         */
        void onTokenDeleted();

        /**
         * could not receive gcm push token
         */
        void onError();
    }
}