package at.favre.app.bankathon16.misc;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Handles all of the app's persistable preferences
 */
public class PreferencesHandler {

    //general settings
    private final static String CHILD_USER_ID = "CHILD_USER_ID";
    private final static String SAVING_GOAL_NAME = "SAVING_GOAL_NAME";
    private final static String SAVING_GOAL_AMOUNT = "SAVING_GOAL_AMOUNT";


    private final Context context;
    private SharedPreferences appPrivateSharedPreferences;


    /**
     * Creates new PreferencesHandler instance.
     * You should only need one instance per app.
     *
     * @param context Application Context
     */
    public PreferencesHandler(final Context context) {
        this.context = context;
    }


    public long getChildUserId() {
        return getAppPrivateSharedPreferences().getLong(CHILD_USER_ID, 2L);
    }

    public void setChildUserId(long value) {
        getAppPrivateSharedPreferences().edit().putLong(CHILD_USER_ID, value).apply();
    }

    public boolean isChildRegistered() {
        return getAppPrivateSharedPreferences().contains(CHILD_USER_ID);
    }

    /**
     * The GlobalPublicPreferences should only be used within this class.
     * Create getter and setter to access specific prefs.
     *
     * @return private {@link SharedPreferences} that is global for the app
     */
    private SharedPreferences getAppPrivateSharedPreferences() {
        if (appPrivateSharedPreferences == null) {
            appPrivateSharedPreferences = context.getSharedPreferences(context.getPackageName() + "_app_", Context.MODE_PRIVATE);
        }
        return appPrivateSharedPreferences;
    }


    public void clear() {
        getAppPrivateSharedPreferences().edit().clear().apply();
    }

    public void setSavingGoal(String name, long amount) {
        getAppPrivateSharedPreferences().edit().putString(SAVING_GOAL_NAME, name).apply();
        getAppPrivateSharedPreferences().edit().putLong(SAVING_GOAL_AMOUNT, amount).apply();
    }

    public String getSavingGoalName() {
        return getAppPrivateSharedPreferences().getString(SAVING_GOAL_NAME, null);
    }

    public long getSavingGoalAmount() {
        return getAppPrivateSharedPreferences().getLong(SAVING_GOAL_AMOUNT, 0L);
    }
}
