package at.favre.app.bankathon16;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import at.favre.app.bankathon16.misc.PreferencesHandler;
import at.favre.app.bankathon16.network.BankathonBackendService;
import at.favre.app.bankathon16.network.gcm.GcmManager;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by mario on 23.04.16.
 */
public class BankathonApplication extends Application {
    private static final String TAG = BankathonApplication.class.getSimpleName();

    private static final String BACKEND_URL = "http://128.130.255.161:18080/";

    private BankathonBackendService mBackendService;
    private PreferencesHandler preferencesHandler;
    private GcmManager gcmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesHandler = new PreferencesHandler(getApplicationContext());
    }

    public PreferencesHandler getPreferencesHandler() {
        return preferencesHandler;
    }

    public void initGcm(GcmManager.GetTokenCallback callback) {
        Log.i(TAG, "init gcm");
        checkPlayServices();
        gcmManager = new GcmManager(getApplicationContext());
        if (!gcmManager.hasCachedToken()) {
            Log.i(TAG, "no gcm token, update token");
            gcmManager.updateToken(callback);
        } else {
            callback.onTokenReceived(gcmManager.getCachedToken());
            Log.d(TAG, "has cached token, is " + gcmManager.getCachedToken());
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
        if (result != ConnectionResult.SUCCESS) {
            Log.w(TAG, "Google Play Services not installed! Resultcode: " + result);
            return false;
        }
        Log.d(TAG, "Google Play Services are available!");
        return true;
    }

    public BankathonBackendService getDataAccess() {
        if (mBackendService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BACKEND_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            mBackendService = retrofit.create(BankathonBackendService.class);
        }
        return mBackendService;
    }
}
