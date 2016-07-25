package com.google.face2face.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.face2face.NavigationActivity;
import com.google.face2face.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class F2FMessagingService extends FirebaseMessagingService {
    private static final String TAG = "F2FMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Got an FCM notification. Popping it on the screen.");
        Intent intent = new Intent(this, NavigationActivity.class);
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        Bitmap img = null;
        try {
             img = BitmapFactory.decodeStream((InputStream) new URL(
                    "http://www.dkdiaries.com/wp-content/uploads/2012/02/For-the-Birds-Pixar.jpg").getContent());
        } catch (IOException e) {
            // Loading a default image
            img = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        }


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setSmallIcon(R.mipmap.ic_app_small)
                .setLargeIcon(img)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }
}
