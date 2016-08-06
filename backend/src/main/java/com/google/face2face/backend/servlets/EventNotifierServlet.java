package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;
import com.google.face2face.backend.FirebaseInitializer;
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
        FirebaseInitializer.initializeFirebase();
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Go over all events and find what events there are today
        // 2. Go over all users and send notifications to all users that have buddiesBasicInfo with those
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

                        gift.cta = "אחל טו בשבט שמח";
                        gift.text = "טו בשבט שמח";
                        gift.type = "greeting";
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

                                String userGender = user_ds.child("gender").getValue().toString();
                                String userImageUrl = user_ds.child("image_url").getValue().toString();
                                String buddyName = name.toString();
                                Object imageUrl = buddy_snapshot.child("image_url").getValue();

                                Object gender = buddy_snapshot.child("gender").getValue();
                                String buddyGender = gender != null ? gender.toString() : "male";
                                String buddyPhoto = imageUrl != null ? imageUrl.toString() : null;
                                //String buddyPhoto = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=96";
                                //String buddyToken = buddy_snapshot.child("reg_id").toString();
                                String buddyToken = "eGfQL6JAfuQ:APA91bHxMOFjNF4ODCv4stNscMFzrJsZkwPRw4w-S1cJAGH8MkcHrG9BS0uHk4_Y_jKTo77DL7-3R8gRNaNojU0Yzx8hBEINg1FIKcAH-xF4L0AEZglbhwZNJPuytpht8ClpPMZeN3Iu";
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
                                                    + gift.url + "," + "type:" + gift.type);
                                        }


                                        String buddyId = buddy_snapshot.child("uid").getValue().toString();
                                        data.put("senderName", username);
                                        data.put("senderImageUrl", userImageUrl);
                                        data.put("recipientImageUrl", buddyPhoto);
                                        data.put("recipientId", buddyId);
                                        data.put("recipientName", buddyName);
                                        data.put("description", giftEvent.description);
                                        data.put("action", "give_gift");
                                        data.put("eventTitle", giftEvent.name);
                                        data.put("recipientGender", buddyGender);
                                        data.put("senderGender", userGender);


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
