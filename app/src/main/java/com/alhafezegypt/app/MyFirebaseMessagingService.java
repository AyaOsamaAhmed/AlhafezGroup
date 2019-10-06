package com.alhafezegypt.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by shady on 11/8/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String link = remoteMessage.getData().get("link");
        String type = remoteMessage.getData().get("type");

        if(null == type)
            type="1";
        if(type.isEmpty())
            type = "1";

        sendNotification(title, body, link, type);
    }


    private void sendNotification(String title, String body, String link, String type) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent;

        if("1".equals(type)) {
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

            notificationIntent.putExtra("action", "fromNotification");
            notificationIntent.putExtra("link", link);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            contentIntent = PendingIntent.getActivity(getApplicationContext(), (int) (System.currentTimeMillis()),
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }else {

            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);

            notificationIntent.setData(Uri.parse(link));
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(title)
                        .setTicker(title)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true);

        if (body != null) {
            mBuilder.setContentText(body);
        } else {
            mBuilder.setContentText("<missing message content>");
        }

        mBuilder.setNumber(0);

        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
