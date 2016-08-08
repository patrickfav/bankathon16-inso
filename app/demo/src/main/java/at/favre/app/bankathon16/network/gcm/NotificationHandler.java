package at.favre.app.bankathon16.network.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

import at.favre.app.bankathon16.R;

/**
 * Manages Android system notifications.
 * For internal usage only.
 *
 * @since 1.2.0 (2015-06-03)
 */
public final class NotificationHandler {

    private final static String TAG = NotificationHandler.class.getSimpleName();

    protected static void buildNotification(Context context, Class activityClass, String title, String msg) {
        NotificationManager notificationManager = getNotificationManager(context);

        Builder builder = (Builder) new Builder(context)
                .withActivityToStart(activityClass)
                .setSmallIcon(R.drawable.ic_account_balance_wallet_white_24dp)
                .setContentTitle(title)
                .setTicker(title)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setContentText(msg);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static class Builder extends NotificationCompat.Builder {

        public Builder(Context context) {
            super(context);
        }

        public Builder withActivityToStart(Class activityToStartClass) {
            if (activityToStartClass != null) {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

                Intent notificationsActivityIntent = new Intent(mContext, activityToStartClass);

                stackBuilder.addNextIntent(notificationsActivityIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                setContentIntent(resultPendingIntent);
            } else {
                Log.w(TAG, "no activity set for notification, see BSMManager.setNotificationActivityClass()");
            }
            return this;
        }
    }
}