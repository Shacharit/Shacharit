package com.google.face2face.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.face2face.BuddyEventActivity;
import com.google.face2face.MatchActivity;
import com.google.face2face.R;
import com.google.face2face.ReceiveGiftActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class F2FMessagingService extends FirebaseMessagingService {
    private static final String TAG = "F2FMessagingService";

    // Actions - send_gift, get_gift, send_email

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Got an FCM notification. Popping it on the screen.");

        switch (remoteMessage.getData().get("action")) {
            case "give_gift":
                showNotification(remoteMessage.getData(), new Intent(this, BuddyEventActivity.class));
                break;
            case "receive_gift":
                showNotification(remoteMessage.getData(), new Intent(this, ReceiveGiftActivity.class));
                break;
            case "send_email":
                sendEmailNotification(remoteMessage.getData());
                break;
            case "match":
                showNotification(remoteMessage.getData(), new Intent(this, MatchActivity.class));
                break;
            default:
                Log.w(TAG, "The FCM notification is missing or has bad 'action' field: " + remoteMessage);
        }
    }

    private void showNotification(Map<String, String> data, Intent intent) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        Bitmap img = null;
        try {
            img = BitmapFactory.decodeStream((InputStream) new URL(data.get("image_url")).getContent());
        } catch (IOException e) {
            // Loading a default image
            img = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("message"))
                .setSmallIcon(R.mipmap.ic_app_small)
                .setLargeIcon(img)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT))
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }

    private void sendEmailNotification(Map<String, String> data) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + data.get("email")));
        intent.putExtra(Intent.EXTRA_SUBJECT, "TODO: Put subject here");
        intent.putExtra(Intent.EXTRA_TEXT, "<div>TODO: Body. Should be HTML</div>");

        showNotification(data, intent);
    }
}
