package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;
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

                    System.out.println("Found event: " + eventName);

                    for(DataSnapshot defs_ds : event_ds.child("event_definitions").getChildren()) {
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
                        for(DataSnapshot user_ds : dataSnapshot.getChildren()) {
                            // Check if buddy has relevant events
                            DataSnapshot buddy_snapshot = user_ds.child("buddy");
                            Object name = user_ds.child("display_name").getValue();

                            if (name == null) {
                                // User has no name
                                continue;
                            }

                            String buddyName = name.toString();
                            String buddyToken = "fmRZ6KFsuYI:APA91bHbYkBJ3GizRmOKp88Fc4O62ke2WaQJAfS1JsnwDkDcZ37NAvAy1ZK9yPJyt56o9fb3tkb_PWG4zr2F3WGq11VwsW4FWARWfSeIYKwMHZ-Wd12bbdWffRvdvsjpymkhEzAcqHME";
                            for(DataSnapshot ds_def : buddy_snapshot.child("selfDefs").getChildren()) {
                                String definition = ds_def.getValue().toString();
                                if (!defsToEvents.containsKey(definition)) {
                                    continue;
                                }

                                List<GiftEvent> giftEvents = defsToEvents.get(definition);

                                for (GiftEvent giftEvent : giftEvents) {
                                    String title = "שלח לחבר מתנה";
                                    String message = giftEvent.name + " " + giftEvent.femaleText + " " + buddyName;

                                    Map<String, String> giftNames = new HashMap<>();

                                    for (int i = 0; i < giftEvent.gifts.length; i++) {
                                        giftNames.put("gift" + i, giftEvent.gifts[i]);
                                    }

                                    try {
                                        FcmMessenger.sendPushMessage(buddyToken, message, title , giftNames);
                                    } catch (IOException e) {
                                        e.printStackTrace();
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
