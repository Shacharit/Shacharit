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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BdayNotifierServlet extends HttpServlet {
    // Firebase keys shared with client applications
    private DatabaseReference firebase;

    // Servlet members
    private Map<String, Gift> mGiftsToSend = new HashMap<>();


    @Override
    public void init(ServletConfig config) {
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
        // 1. Iterate over all users.
        //    1.1 If the user has a birthday, iterate over all of the user's buddies.
        //        1.1.1 Notify the user's buddy that the user has a birthday.

        // TODO(maiad): Make sure that we have a birthday table that contains the different presents
        // available, and his/shes texts.

//        for (user : users) {
//            if (user.Birthday() != today) {
//                continue;
//            }
//            String title = user.Name() + " has a birthday!";
//            String message = "Congratulate " + user.Name() + " for his birthday:";
//            // extras should have keys: gift1, gift2, gift3, TODO(maiad): What else?
//            HashMap<String, String> extras = new HashMap<>();
//            extras["gift1"] = birthday_table.Gift1();
//            extras["gift2"] = birthday_table.Gift2();
//            extras["gift3"] = birthday_table.Gift3();
//            for (buddy : buddies) {
//                String token = buddy.Token();
//                FcmMessenger.sendPushMessage(token, title, message, extras);
//            }
//        }

        firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, List<GiftEvent>> defsToEvents = new HashMap<>();

                for (DataSnapshot userDs : dataSnapshot.getChildren()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    String birthdayStr = userDs.child("birthday").getValue().toString();

                    String todayDateStr = formatter.format(Calendar.getInstance().getTime());
                    // TODO(maiad): Make sure that the comparison is performed without considering
                    // the year!
                    if (birthdayStr == null || !birthdayStr.equals(todayDateStr)) {
                        continue;
                    }

                    String userName = userDs.getKey().toString();
                    GiftEvent event = new GiftEvent(userName);

                    String userGender = userDs.child("gender").getValue().toString();
                    if (userGender.equals("female")) {
//                        event.femaleText = birthdayDs.child("female text").getValue().toString();
                    }


//                    String buddyName = userDs.child("buddy").child("uid").getValue().toString();
//
//                    event.femaleText = userDs.child("female text").getValue().toString();
//                    event.description = userDs.child("description").getValue().toString();
//                    for (int i = 0; i < 3; i++) {
//                        event.gifts[i] = userDs.child("gift" + (i +1)).child("text")
//                                .getValue().toString();
//                    }
//
//
//                    System.out.println("Found event: " + buddyName);
//
//                    for(DataSnapshot defs_ds : userDs.child("event_definitions").getChildren()) {
//                        String definition = defs_ds.getKey();
//                        if (!defsToEvents.containsKey(definition)) {
//                            defsToEvents.put(definition, new ArrayList<GiftEvent>());
//                        }
//
//                        defsToEvents.get(definition).add(event);
//                    }
                }

                firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot user_ds : dataSnapshot.getChildren()) {
//                            // Check if buddy has relevant events
//                            Object displayName = user_ds.child("display_name").getValue();
//
//                            if (displayName == null) {
//                                continue;
//                            }
//
//                            String username = displayName.toString();
//                            DataSnapshot buddy_snapshot = user_ds.child("buddy");
//                            Object name = user_ds.child("display_name").getValue();
//
//                            if (name == null) {
//                                // User has no name
//                                continue;
//                            }
//
//                            String buddyName = name.toString();
//                            String buddyToken = "fmRZ6KFsuYI:APA91bHbYkBJ3GizRmOKp88Fc4O62ke2WaQJAfS1JsnwDkDcZ37NAvAy1ZK9yPJyt56o9fb3tkb_PWG4zr2F3WGq11VwsW4FWARWfSeIYKwMHZ-Wd12bbdWffRvdvsjpymkhEzAcqHME";
//                            for(DataSnapshot ds_def : buddy_snapshot.child("selfDefs").getChildren()) {
//                                String definition = ds_def.getValue().toString();
//                                if (!defsToEvents.containsKey(definition)) {
//                                    continue;
//                                }
//
//                                List<GiftEvent> giftEvents = defsToEvents.get(definition);
//
//                                // name: <name>, cta: <cta>
//                                for (GiftEvent giftEvent : giftEvents) {
//                                    String title = "שלח לחבר מתנה";
//                                    String message = giftEvent.name + " " + giftEvent.femaleText + " " + buddyName;
//
//                                    Map<String, String> data = new HashMap<>();
//
//                                    for (int i = 0; i < giftEvent.gifts.length; i++) {
//                                        data.put("gift" + (i+1), giftEvent.gifts[i]);
//                                    }
//                                    data.put("recipient", buddyName);
//                                    data.put("username", username);
//                                    data.put("description", giftEvent.description);
//                                    data.put("giveGifts", "");
//
//                                    // Send description, event, recipient, username
//                                    try {
//                                        FcmMessenger.sendPushMessage(buddyToken, message, title , data);
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                            }
//                        }
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
