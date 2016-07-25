package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;
import com.google.face2face.backend.Gift;
import com.google.face2face.backend.GiftEvent;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.Event;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventNotifierServlet extends HttpServlet {

    private DatabaseReference firebase;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String credential = config.getInitParameter("credential");
        String databaseUrl = config.getInitParameter("databaseUrl");

        System.out.println("Credential file : " + credential);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(config.getServletContext().getResourceAsStream(credential))
                .setDatabaseUrl(databaseUrl)
                .build();
        FirebaseApp.initializeApp(options);
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Go over all events and find what events there are today
        // 2. Go over all users and send notifications to all users that have buddies with those
        // definitions

        firebase.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, List<GiftEvent>> defsToEvents = new HashMap<>();

                for (DataSnapshot event_ds : dataSnapshot.getChildren()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    String date_str = event_ds.child("date").getValue().toString();

                    String todayDateStr = formatter.format(Calendar.getInstance().getTime());

                    if (date_str == null || !date_str.equals(todayDateStr)) {
                        continue;
                    }

                    String eventName = event_ds.child("event name").getValue().toString();
                    GiftEvent event = new GiftEvent(eventName);
                    event.femaleText = event_ds.child("female text").getValue().toString();
                    event.description = event_ds.child("description").getValue().toString();
                    for (int i = 0; i < 3; i++) {
                        Gift gift = new Gift();
                        DataSnapshot gift_ds = event_ds.child("gift" + (i + 1));
                        //gift.cta = gift_ds.child("cta").getValue().toString();
                        //gift.text = gift_ds.child("text").getValue().toString();
                        //gift.type = gift_ds.child("type").getValue().toString();
                        //gift.url = gift_ds.child("url").getValue().toString();

                        gift.cta = "קטע";
                        gift.text = "הטקסט";
                        gift.type = "text";
                        gift.url = "";

                        event.gifts[i] = gift;
                    }


                    System.out.println("Found event: " + eventName);

                    for (DataSnapshot defs_ds : event_ds.child("event_definitions").getChildren()) {
                        String definition = defs_ds.getKey();
                        if (!defsToEvents.containsKey(definition)) {
                            defsToEvents.put(definition, new ArrayList<GiftEvent>());
                        }

                        defsToEvents.get(definition).add(event);
                    }
                }

                firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot user_ds : dataSnapshot.getChildren()) {
                            // Check if buddy has relevant events
                            Object displayName = user_ds.child("display_name").getValue();

                            if (displayName == null) {
                                continue;
                            }

                            String username = displayName.toString();
                            DataSnapshot buddiesDs = user_ds.child("buddy");

                            for (DataSnapshot buddy_snapshot : buddiesDs.getChildren()) {
                                Object name = user_ds.child("display_name").getValue();

                                if (name == null) {
                                    // User has no name
                                    continue;
                                }

                                String buddyName = name.toString();
                                Object imageUrl = buddy_snapshot.child("image_url").getValue();

                                Object gender = buddy_snapshot.child("gender").getValue();
                                String buddyGender = gender != null ? gender.toString() : "male";
                                String buddyPhoto = imageUrl != null ? imageUrl.toString() : null;
                                //String buddyPhoto = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=96";
                                //String buddyToken = buddy_snapshot.child("reg_id").toString();
                                String buddyToken = "dMGFRFCiywo:APA91bHvrQKrIqeF98M5MSNVhoIyRcXFiS6UqGWJgLMyQebUOzJXp43y3XafBx6Ip04ytseng6a7VakoosZD8OMpfce-w2q0qf9jjku8e5aNarw5ZYqLNthzN4eDitEaAXD8ZvR24FE4";
                                for (DataSnapshot ds_def : buddy_snapshot.child("selfDefs").getChildren()) {
                                    String definition = ds_def.getValue().toString();
                                    if (!defsToEvents.containsKey(definition)) {
                                        continue;
                                    }

                                    List<GiftEvent> giftEvents = defsToEvents.get(definition);

                                    // name: <name>, cta: <cta>
                                    for (GiftEvent giftEvent : giftEvents) {
                                        String title = "שלח לחבר מתנה";
                                        String message = buddyName + " " + giftEvent.femaleText + " "
                                                + giftEvent.name;

                                        Map<String, String> data = new HashMap<>();

                                        //gift1: "type:<type>,url:<url>,text:<text>,cta:<cta>
                                        for (int i = 0; i < giftEvent.gifts.length; i++) {
                                            Gift gift = giftEvent.gifts[i];
                                            data.put("gift" + (i + 1), "text:" + gift.text + "," +
                                                    "cta:" + gift.cta + "," + "url:"
                                                    + gift.url + "type:" + gift.type);
                                        }


                                        String buddyId = buddy_snapshot.child("uid").getValue().toString();
                                        data.put("recipient", buddyName);
                                        data.put("recipient_id", buddyId);
                                        data.put("username", username);
                                        data.put("uid", user_ds.getKey());
                                        data.put("description", giftEvent.description);
                                        data.put("action", "give_gift");
                                        data.put("event", giftEvent.name);
                                        data.put("image_url", buddyPhoto);
                                        data.put("gender", buddyGender);

                                        // Send description, event, recipient, username
                                        try {
                                            FcmMessenger.sendPushMessage(buddyToken, message, title, data);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
