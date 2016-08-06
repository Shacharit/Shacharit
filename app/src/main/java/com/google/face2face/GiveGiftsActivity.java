package com.google.face2face;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.face2face.service.VolleySingleton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GiveGiftsActivity extends AppCompatActivity {
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_gifts);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle data = getIntent().getExtras();
        Gift gift = populateGift(data);

        NetworkImageView buddyImg = (NetworkImageView) findViewById(R.id.buddy_image);
        if (buddyImg == null) {
            return;
        }
        buddyImg.setImageUrl(gift.recipientImageUrl,
                VolleySingleton.getInstance(this.getApplicationContext()).getImageLoader());

        TextView buddy_name = (TextView) findViewById(R.id.buddy_name);
        assert buddy_name != null;
        buddy_name.setText(gift.recipientName);

        TextView title = (TextView) findViewById(R.id.title);
        assert title != null;
        title.setText(gift.eventTitle);

        TextView descriptior = (TextView) findViewById(R.id.descriptor);
        assert descriptior != null;
        descriptior.setText(gift.eventDescription);

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayoutGiveGifts);
        for (String key : data.keySet()) {
            if (key.contains("gift")) {
                parseGift(data.getString(key), gift);
            }
        }
    }

    private Gift populateGift(Bundle data) {
        Gift gift = new Gift();
        gift.recipientId = data.getString("recipientId");
        gift.recipientName = data.getString("recipientName");
        gift.recipientGender = data.getString("recipientGender");
        gift.recipientImageUrl = data.getString("recipientImageUrl");
        gift.senderGender = data.getString("senderGender");
        gift.senderName = data.getString("senderName");
        gift.senderId = data.getString("senderId");

        // Users
        gift.senderName = data.getString("senderName");
        gift.senderImageUrl = data.getString("senderImageUrl");
        gift.senderGender = data.getString("senderGender");
        gift.recipientImageUrl  = data.getString("recipientImageUrl");
        gift.recipientId = data.getString("recipientId");
        gift.recipientName = data.getString("recipientName");

        // Data
        gift.isSent = false;

        // Gift
        gift.eventTitle = data.getString("eventTitle");
        gift.eventDescription = data.getString("eventDescription");
        return gift;
    }

    private void parseGift(String data, final Gift giftToSend) {
        String[] giftData = data.split(",");
        String cta = "";
        String url = "";
        String type = "";
        String text = "";
        for (String s : giftData) {
            if (s.contains("cta:"))
                cta = s.substring(4);
            else if (s.contains("url:"))
                url = s.substring(4);
            else if (s.contains("type:"))
                type = s.substring(5);
            else if (s.contains("text:"))
                text = s.substring(5);
        }
        if (!cta.isEmpty()) {
            giftToSend.cta = cta;
        }
        if (!text.isEmpty()) {
            giftToSend.giftText = text;
        }

        if (type.equals("greeting")) {
            Button button = (Button) findViewById(R.id.send_message_button);
            button.setText(cta);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                    finish();
                }
            });
        } else if(type.equals("video")) {
            Button button = (Button) findViewById(R.id.send_video_button);
            button.setText(cta);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                    finish();
                }
            });
        } else if(type.equals("physical")) {
            Button button = (Button) findViewById(R.id.send_gift_button);
            button.setText(cta);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                    finish();
                }
            });
        }
    }
}
