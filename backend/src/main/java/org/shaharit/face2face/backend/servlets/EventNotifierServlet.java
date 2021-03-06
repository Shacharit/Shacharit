package org.shaharit.face2face.backend.servlets;

import org.shaharit.face2face.backend.database.firebase.FirebaseEventDb;
import org.shaharit.face2face.backend.database.firebase.FirebaseUserDb;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.tasks.EventNotificationTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventNotifierServlet extends ShaharitServlet {
    private static final Logger logger = Logger.getLogger(EventNotifierServlet.class.getName());
    private static final String anonymousImageUrl = "https://lh3.googleusercontent.com/-Kx8qBZwVREc/AAAAAAAAAAI/AAAAAAAAAAA/GWTS1klu7QQ/photo.jpg?sz=256";


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("in event-servlet doGet");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("in event-servlet doPost");
        new EventNotificationTask(
                new FirebaseUserDb(firebase),
                new FirebaseEventDb(firebase),
                new PushService(new FcmMessenger()),
                Calendar.getInstance()).execute();
    }
}

// Implementation moved to EventNotificationTask class
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // 1. Go over all events and find what events there are today
//        // 2. Go over all users and send notifications to all users that have buddiesBasicInfo with those
//        // definitions
//
//        firebase.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final Map<String, List<GiftEvent>> defsToEvents = new HashMap<>();
//
//                for (DataSnapshot event_ds : dataSnapshot.getChildren()) {
//                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
//                    String date_str = event_ds.child("date").getValue().toString();
//
//                    String todayDateStr = formatter.format(Calendar.getInstance().getTime());
//
//                    if (date_str == null || !date_str.equals(todayDateStr)) {
//                        continue;
//                    }
//
//                    String eventName = event_ds.child("event name").getValue().toString();
//                    GiftEvent event = new GiftEvent(eventName);
//                    event.femaleText = event_ds.child("female text").getValue().toString();
//                    event.description = event_ds.child("description").getValue().toString();
//                    for (int i = 0; i < 3; i++) {
//                        DataSnapshot gift_ds = event_ds.child("gift" + (i + 1));
//                        GiftSuggestion gift = giftFromSnapshot(gift_ds);
//
//                        event.gifts[i] = gift;
//                    }
//
//
//                    System.out.println("Found event: " + eventName);
//
//                    for (DataSnapshot defs_ds : event_ds.child("event_definitions").getChildren()) {
//                        String definition = defs_ds.getKey();
//                        if (!defsToEvents.containsKey(definition)) {
//                            defsToEvents.put(definition, new ArrayList<GiftEvent>());
//                        }
//
//                        defsToEvents.get(definition).add(event);
//                    }
//                }
//
//                firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot senderDataSnapshot : dataSnapshot.getChildren()) {
//                            // Check if buddy has relevant events
//                            Object displayName = senderDataSnapshot.child("display_name").getValue();
//                            if (displayName == null) {
//                                continue;
//                            }
//
//                            String senderName = displayName.toString();
//                            String senderGender = senderDataSnapshot.child("gender").getValue().toString();
//                            Object imageUrl = senderDataSnapshot.child("image_url").getValue();
//                            String senderImageUrl = (imageUrl == null) ? anonymousImageUrl : imageUrl.toString();
//
//                            DataSnapshot buddiesDataSnapshot = senderDataSnapshot.child("buddy");
//
//                            for (DataSnapshot buddyDataSnapshot : buddiesDataSnapshot.getChildren()) {
////                                Object name = buddy_snapshot.child("display_name").getValue();
////                                if (name == null) {
////                                    // User has no name
////                                    continue;
////                                }
//
//
////                                String buddyName = name.toString();
//                                String buddyName = "test";
//
//                                imageUrl = buddyDataSnapshot.child("imageUrl").getValue();
//                                String buddyImageUrl = (imageUrl == null) ? anonymousImageUrl : imageUrl.toString();
//
//                                Object gender = buddyDataSnapshot.child("gender").getValue();
//                                String buddyGender = gender != null ? gender.toString() : "male";
//                                String buddyPhoto = buddyImageUrl != null ? buddyImageUrl : null;
//                                //String buddyPhoto = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=96";
//                                String buddyToken = buddyDataSnapshot.child("regId").toString();
////                                String buddyToken = "f2aIW9hBC0A:APA91bFR4vmjPNzNyHVjLCin2W2lyTpIgTXT66kjBM1Qwc4CWorc5QbQqrtYfERd2Xt90xTBsoo1i2CmCMAjJCg92kqbEWlPgKx-uu4DYpYCXLmLGsv3L7K8WsBeFmG2FF-4Bb37aYxs";
////                                String buddyToken = "fHg77UkA2pM:APA91bHj9k8pWwmB0iEyxH6WW4hIk1qy4FA535IQp-PF1-cS7rpfxWR-mqRFpEb4nVxfHh7D3aUuOvOL-ID-s73PkUiheI8gdemLQdRBRRjMHNVSHVqk7GtbJyFntXmpBLzoeUFDKxjP";
//                                for (DataSnapshot buddyDefinitionDataSnapshot : buddyDataSnapshot.child("selfDefs").getChildren()) {
//                                    String definition = buddyDefinitionDataSnapshot.getValue().toString();
//                                    if (!defsToEvents.containsKey(definition)) {
//                                        continue;
//                                    }
//
//                                    List<GiftEvent> giftEvents = defsToEvents.get(definition);
//
//                                    // name: <name>, cta: <cta>
//                                    for (GiftEvent giftEvent : giftEvents) {
//                                        String title = "שלח לחבר מתנה";
//                                        String message = buddyName + " " + giftEvent.femaleText + " "
//                                                + giftEvent.name;
//
//                                        Map<String, String> data = new HashMap<>();
//
//                                        //gift1: "type:<type>,url:<url>,text:<text>,cta:<cta>
//                                        for (int i = 0; i < giftEvent.gifts.length; i++) {
//                                            GiftSuggestion gift = giftEvent.gifts[i];
//                                            data.put("gift" + (i + 1), "text:" + gift.text + "," +
//                                                    "cta:" + gift.cta + "," + "url:"
//                                                    + gift.url + "," + "type:" + gift.type);
//                                        }
//
//
//                                        String buddyId = buddyDataSnapshot.child("uid").getValue().toString();
//                                        data.put("senderName", senderName);
//                                        data.put("senderImageUrl", senderImageUrl);
//                                        data.put("recipientImageUrl", buddyPhoto);
//                                        data.put("recipientId", buddyId);
//                                        data.put("recipientName", buddyName);
//                                        data.put("eventDescription", giftEvent.description);
//                                        data.put("action", "give_gift");
//                                        data.put("eventTitle", giftEvent.name);
//                                        data.put("recipientGender", buddyGender);
//                                        data.put("senderGender", senderGender);
//
//
//                                        // Send description, event, recipient, username
//                                        try {
//                                            FcmMessenger.sendPushMessage("hL61rpDqYs:APA91bGb80BGcdzIdEoV-XzNy7x12E3_k06ztqIjx2PC_JIPfAkWakjku88kCOgk1I6IH6PFa4vIkvchRuRWpTv21mKF4bipAvR-TAjwCH0M62JwVpNB7AwJN0l2t96gkp3BY_eyoe3W", message, title, data);
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    private GiftSuggestion giftFromSnapshot(DataSnapshot gift_ds) {
//        GiftSuggestion gift = new GiftSuggestion();
//
//        gift.cta = gift_ds.child("cta male2male").getValue().toString();
//        gift.text = gift_ds.child("greeting male2male").getValue().toString();
//        gift.type = gift_ds.child("type").getValue().toString();
//        gift.url = gift_ds.child("url").getValue().toString();
//
//        return gift;
//    }
//}
