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
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventNotifierServlet extends HttpServlet {

    private DatabaseReference firebase;
    private static final Logger logger = Logger.getLogger(EventNotifierServlet.class.getName());
    private static final String anonymousImageUrl = "https://lh3.googleusercontent.com/-Kx8qBZwVREc/AAAAAAAAAAI/AAAAAAAAAAA/GWTS1klu7QQ/photo.jpg?sz=256";


    @Override
    public void init(ServletConfig config) throws ServletException {
        FirebaseInitializer.initializeFirebase();
        firebase = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("in matching-servlet doGet");
        doPost(req, resp);
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
                        DataSnapshot gift_ds = event_ds.child("gift" + (i + 1));
                        Gift gift = giftFromSnapshot(gift_ds);

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
                        for (DataSnapshot senderDataSnapshot : dataSnapshot.getChildren()) {
                            // Check if buddy has relevant events
                            Object displayName = senderDataSnapshot.child("display_name").getValue();
                            if (displayName == null) {
                                continue;
                            }

                            String senderName = displayName.toString();
                            String senderGender = senderDataSnapshot.child("gender").getValue().toString();
                            Object imageUrl = senderDataSnapshot.child("image_url").getValue();
                            String senderImageUrl = (imageUrl == null) ? anonymousImageUrl : imageUrl.toString();

                            DataSnapshot buddiesDataSnapshot = senderDataSnapshot.child("buddy");

                            for (DataSnapshot buddyDataSnapshot : buddiesDataSnapshot.getChildren()) {
                                // TODO: get buddy username.
//                                Object name = buddy_snapshot.child("display_name").getValue();
//                                if (name == null) {
//                                    // User has no name
//                                    continue;
//                                }


//                                String buddyName = name.toString();
                                String buddyName = "test";

                                imageUrl = buddyDataSnapshot.child("imageUrl").getValue();
                                String buddyImageUrl = (imageUrl == null) ? anonymousImageUrl : imageUrl.toString();

                                Object gender = buddyDataSnapshot.child("gender").getValue();
                                String buddyGender = gender != null ? gender.toString() : "male";
                                String buddyPhoto = buddyImageUrl != null ? buddyImageUrl.toString() : null;
                                //String buddyPhoto = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=96";
                                String buddyToken = buddyDataSnapshot.child("regId").toString();
//                                String buddyToken = "f2aIW9hBC0A:APA91bFR4vmjPNzNyHVjLCin2W2lyTpIgTXT66kjBM1Qwc4CWorc5QbQqrtYfERd2Xt90xTBsoo1i2CmCMAjJCg92kqbEWlPgKx-uu4DYpYCXLmLGsv3L7K8WsBeFmG2FF-4Bb37aYxs";
                                for (DataSnapshot buddyDefinitionDataSnapshot : buddyDataSnapshot.child("selfDefs").getChildren()) {
                                    String definition = buddyDefinitionDataSnapshot.getValue().toString();
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


                                        String buddyId = buddyDataSnapshot.child("uid").getValue().toString();
                                        data.put("senderName", senderName);
                                        data.put("senderImageUrl", senderImageUrl);
                                        data.put("recipientImageUrl", buddyPhoto);
                                        data.put("recipientId", buddyId);
                                        data.put("recipientName", buddyName);
                                        data.put("description", giftEvent.description);
                                        data.put("action", "give_gift");
                                        data.put("eventTitle", giftEvent.name);
                                        data.put("recipientGender", buddyGender);
                                        data.put("senderGender", senderGender);


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

    private Gift giftFromSnapshot(DataSnapshot gift_ds) {
        Gift gift = new Gift();

        gift.cta = gift_ds.child("cta male2male").getValue().toString();
        gift.text = gift_ds.child("greeting male2male").getValue().toString();
        gift.type = gift_ds.child("type").getValue().toString();
        gift.url = gift_ds.child("url").getValue().toString();

        return gift;
    }
}
