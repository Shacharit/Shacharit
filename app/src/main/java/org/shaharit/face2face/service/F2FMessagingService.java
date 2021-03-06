package org.shaharit.face2face.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.shaharit.face2face.NavigationActivity;
import org.shaharit.face2face.gifts.ReceiveGiftActivity;
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
                remoteMessage.getData().put("action", "give_gift");
                showNotification(remoteMessage.getData(), new Intent(this, NavigationActivity.class));
                break;
            case "receive_gift":
                showNotification(remoteMessage.getData(), new Intent(this, ReceiveGiftActivity.class));
                break;
            case "send_email":
                sendEmailNotification(remoteMessage.getData());
                break;
            case "match":
                remoteMessage.getData().put("action", "match");
                showNotification(remoteMessage.getData(), new Intent(this, NavigationActivity.class));
                break;
            default:
                Log.w(TAG, "The FCM notification is missing or has bad 'action' field: " + remoteMessage);
        }
    }

    private void showNotification(Map<String, String> data, Intent intent) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }

        Bitmap img;
        try {
            img = BitmapFactory.decodeStream((InputStream) new URL(data.get("image_url")).getContent());
        } catch (IOException e) {
            // Loading a default image
            img = BitmapFactory.decodeResource(getResources(), org.shaharit.face2face.R.mipmap.ic_launcher);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("message"))
                .setSmallIcon(org.shaharit.face2face.R.mipmap.ic_app_small)
                .setLargeIcon(img)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT))
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void sendEmailNotification(Map<String, String> data) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + data.get("email")));
        intent.putExtra(Intent.EXTRA_SUBJECT, "TODO: Put subject here");
        intent.putExtra(Intent.EXTRA_TEXT, "<div>TODO: Body. Should be HTML</div>");

        showNotification(data, intent);
    }
}
