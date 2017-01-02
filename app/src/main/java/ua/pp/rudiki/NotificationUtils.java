package ua.pp.rudiki;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import ua.pp.rudiki.R;

public class NotificationUtils {

    public static void displayNotification(Context context, int notificationId, String tickerText, String contentTitle, String contentText, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(tickerText);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);

        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notifyMgr.notify(notificationId, mBuilder.build());

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
