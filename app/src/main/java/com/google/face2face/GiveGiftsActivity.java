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

        Bundle data = getIntent().getExtras();
        Gift gift = new Gift();
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayoutGiveGifts);

        if (data.containsKey("image_url")) {
            ImageView buddyImg = (ImageView) findViewById(R.id.buddyImg);

            Bitmap bitmapImg = null;
            try {
                // TODO: move to use volley or async task.
                bitmapImg = BitmapFactory.decodeStream((InputStream)new URL(data.getString("image_url")).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            buddyImg.setImageBitmap(bitmapImg);
        }
        if (data.containsKey("recipient")) {
            TextView buddyName = (TextView) findViewById(R.id.buddyName);

            buddyName.setText(data.getString("recipient"));
            gift.recipient = data.getString("recipient");
        }
        if (data.containsKey("event")) {
            TextView buddyName = (TextView) findViewById(R.id.event);

            buddyName.setText(data.getString("event"));
            gift.event = data.getString("event");
        }
        if (data.containsKey("description")) {
            TextView buddyName = (TextView) findViewById(R.id.descriptor);
            buddyName.setText(data.getString("description"));
        }
        if (data.containsKey("event")) {
            TextView buddyName = (TextView) findViewById(R.id.event);
            buddyName.setText(data.getString("event"));
            gift.event = data.getString("event");
        }
        for (String key : data.keySet()) {
            if (key.contains("gift")) {
                Button toAdd = parseGift(data.getString(key));
            }
        }

        /*
        Bundle data = getIntent().getExtras();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        giftToSend = new Gift();
        for (String key : data.keySet()) {
            if (key.contains("gift"))
                gifts.add((String)data.get(key));
            else if (key.equals("recipient"))
                giftToSend.recipient = (String)data.get(key);
            else if(key.equals("event"))
                giftToSend.event = (String)data.get(key);
        }
        button__sendGift = (Button) findViewById(R.id.button_sendGift);
        listView_gifts = (ListView) findViewById(R.id.listView_gifts);
        gifts = new ArrayList<>();
        selectedGift = "";
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gifts);

        listView_gifts.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        button__sendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // this line adds the data of your EditText and puts in your array
                gifts.add(selectedGift);
                // next thing you have to do is check if your adapter has changed
                adapter.notifyDataSetChanged();
            }
        });

        listView_gifts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                giftToSend.name = listView_gifts.getItemAtPosition(position).toString();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                //get current date time with Date()
                Date date = new Date();
                giftToSend.date = dateFormat.format(date);
                mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
            }
        });
        */
    }
    "text:" + gift.text + "," +
            "cta:" + gift.cta + "," + "url:"
            + gift.url + "type:" + gift.type)


    private Button parseGift(String data) {
        String[] giftData = data.split(",");
        String cta = new String();
        String url = new String();
        String type = new String();
        Button result = new Button();
        for (String s : giftData) {
            if (s.contains("cta:"))
                cta = s.substring(4);
            else if (s.contains("url:"))
                url = s.substring(4);
            else if (s.contains("type:"))
                type = s.substring(6);
        }
        if (type.equals("greeting")) {
            result.setText(cta);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseDatabaseReference.child("sent-gifts").push().setValue(giftToSend);
                }
            });
        }
        else if(type.equals("video")) {
            result.setText(cta);

        }

    }
}
