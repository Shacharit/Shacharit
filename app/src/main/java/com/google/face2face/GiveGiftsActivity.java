package com.google.face2face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.face2face.service.VolleySingleton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GiveGiftsActivity extends AppCompatActivity {
    private DatabaseReference mFirebaseDatabaseReference;

    private TextView buddyName;
    private TextView title;
    private TextView descriptor;
    private Button sendTextButton;
    private Button sendVideoButton;
    private Button sendGiftButton;

    /*
    Gift structure -
    Key - gift%d, Value - "type:<type>,url:<url>,text:<text>,cta:<cta>"

    data.put("recipient", buddyName);
    data.put("recipientId", buddyId); for DB
    data.put("gender", gender); sender of recipient
    data.put("username", username);
    data.put("uid", user_ds.getKey());
    data.put("description", giftEvent.description);
    data.put("giveGifts", "");
    data.put("event", giftEvent.name);
    data.put("image_url", buddyPhoto);
    data.put("gender", buddyGender);
    data.put("gift" + (i + 1), "text:" + gift.text + "," +
    "cta:" + gift.cta + "," + "url:"
    + gift.url + "type:" + gift.type);
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_gifts);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle data = getIntent().getExtras();
        Gift gift = populateGift(data);

        NetworkImageView buddyImg = (NetworkImageView) findViewById(R.id.buddyImg);
        buddyImg.setImageUrl(data.getString("image_url"),
                VolleySingleton.getInstance(this.getApplicationContext()).getImageLoader());

        ((TextView) findViewById(R.id.buddyName)).setText(gift.recipient);
        ((TextView) findViewById(R.id.event)).setText(gift.event);
        ((TextView) findViewById(R.id.descriptor)).setText(data.getString("description"));

        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayoutGiveGifts);
        for (String key : data.keySet()) {
            if (key.contains("gift")) {
                Button button = parseGift(data.getString(key), gift);
                ll.addView(button);
            }
        }
    }

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



    /*
    "text:" + gift.text + "," +
            "cta:" + gift.cta + "," + "url:"
            + gift.url + "type:" + gift.type)
            */


    private Button parseGift(String data, final Gift giftToSend) {
        String[] giftData = data.split(",");
        String cta = new String();
        String url = new String();
        String type = new String();
        Button result = new Button(this.getApplicationContext());
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
            result.setText(cta);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                }
            });
        } else if(type.equals("video")) {
            result.setText(cta);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                }
            });
        }

        return result;
    }
}
