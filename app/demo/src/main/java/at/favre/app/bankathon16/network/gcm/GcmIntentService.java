package at.favre.app.bankathon16.network.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import at.favre.app.bankathon16.childapp.ChildMainActivity;
import at.favre.app.bankathon16.misc.Util;
import at.favre.app.bankathon16.misc.bus.EventBus;
import at.favre.app.bankathon16.misc.bus.Result;
import at.favre.app.bankathon16.parentsapp.ParentMainActivity;

/**
 * receives gcm push messages from GcmBroadcastReceiver and handles them
 * for internal usage only
 *
 * @since 2.0.0
 */
public final class GcmIntentService extends GcmListenerService {
    private final static String TAG = GcmIntentService.class.getSimpleName();

    public static final String SEND_MONEY = "SEND_MONEY";
    public static final String CHILD_PAYMENT = "CHILD_PAYMENT";

    @Override
    public void onMessageReceived(final String from, final Bundle pushData) {
        Log.i(TAG, "gcm onMessageReceived() called with: " + "from = [" + from + "], data = [" + (pushData != null ? pushData.toString() : "null") + "]");

        String msg = pushData.getString("message");
        String[] parts = msg.split(";");
        int amount = Integer.valueOf(parts[1]);

        switch (parts[0]) {
            case SEND_MONEY:
                NotificationHandler.buildNotification(getApplicationContext(), ChildMainActivity.class, "Du hast Geld bekommen!", "Deine Eltern haben dir " +
                        Util.formatWithCurrencyCode(amount, "€") + " geschickt.");
                break;
            case CHILD_PAYMENT:
                String name = parts[2];
                NotificationHandler.buildNotification(getApplicationContext(), ParentMainActivity.class, name + " hat bezahlt", name + " hat um "
                        + Util.formatWithCurrencyCode(amount, "€") + " eingekauft.");
                break;
        }
        sendEventToBus(parts[0]);
    }

    private void sendEventToBus(String type) {
        EventBus.get(getApplicationContext()).sendEvent(type, Result.SUCCESS, Bundle.EMPTY);
    }

    @Override
    public void onDeletedMessages() {
        Log.v(TAG, "gcm push message delete from server");
    }

    @Override
    public void onMessageSent(String msgId) {
        Log.v(TAG, "gcm push upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        Log.w(TAG, "gcm push upstream message sent error . Id=" + msgId + " error: " + error);
    }

}
