package com.example.push_notification.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.push_notification.MainActivity;
import com.example.push_notification.MyApplication;
import com.example.push_notification.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final int REQUEST_CODE = 100;
//    private final int NOTIFICATION_ID = 200;
    private final String MESSAGE_CONTENT_KEY = "content";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        ----- 1.0 - NOTIFICATION MESSAGE -----
//        RemoteMessage.Notification notification = message.getNotification();
//        if (notification == null) {
//            return;
//        }
//        String strTitle = notification.getTitle();
//        String strMessage = notification.getBody();

//        ----- 2.0 - DATA MESSAGE -----
        Map<String, String> stringMap = remoteMessage.getData();
        String strContent = stringMap.get(MESSAGE_CONTENT_KEY);
        sendNotification(strContent);

    }

    private void sendNotification(String strContent) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), MyApplication.CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.notify_title))
                .setContentText(strContent)
                .setSmallIcon(R.drawable.icons8_firebase)
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getNotificationId(), notification);

    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    @Override
    public void onNewToken(@NonNull String firebaseToken) {
        Log.i("[check]", "Refreshed token: " + firebaseToken);
    }

}
