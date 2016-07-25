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
        buddyImg.setImageUrl(data.getString("image_url"),
                VolleySingleton.getInstance(this.getApplicationContext()).getImageLoader());

        TextView buddy_name = (TextView) findViewById(R.id.buddy_name);
        assert buddy_name != null;
        buddy_name.setText(gift.recipient);

        TextView title = (TextView) findViewById(R.id.title);
        assert title != null;
        title.setText(gift.event);

        TextView descriptior = (TextView) findViewById(R.id.descriptor);
        assert descriptior != null;
        descriptior.setText(data.getString("description"));

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayoutGiveGifts);
        for (String key : data.keySet()) {
            if (key.contains("gift")) {
                Button button = (Button) LayoutInflater.from(this).inflate(
                        R.layout.give_gift_button, ll).findViewById(R.id.send_message_button);
                parseGift(button, data.getString(key), gift);
            }
        }
    }

    // TODO: Create functions for each button, and call the SendGiftActivity class with the
    // parameters: username, user gender, gift parameters.
    private Gift populateGift(Bundle data) {
        Gift gift = new Gift();

        gift.recipient_id = data.getString("recipientId");
        gift.recipient = data.getString("recipient");
        gift.event = data.getString("event");
        gift.gender = data.getString("gender");
        gift.sender = data.getString("username");
        gift.sender_id = data.getString("uid");

        return gift;
    }

    private void parseGift(Button button, String data, final Gift giftToSend) {
        String[] giftData = data.split(",");
        String cta = new String();
        String url = new String();
        String type = new String();
        for (String s : giftData) {
            if (s.contains("cta:"))
                cta = s.substring(4);
            else if (s.contains("url:"))
                url = s.substring(4);
            else if (s.contains("type:"))
                type = s.substring(6);
        }
        if (!cta.isEmpty()) {
            giftToSend.cta = cta;
        }
        if (type.equals("greeting")) {
            button.setText(cta);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                }
            });
        } else if(type.equals("video")) {
            button.setText(cta);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                }
            });
        } else if(type.equals("gift")) {
            button.setText(cta);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                }
            });
        }
    }
}
