package org.shaharit.face2face.backend.servlets;

import org.shaharit.face2face.backend.database.firebase.FirebaseGiftDb;
import org.shaharit.face2face.backend.SentGift;
import org.shaharit.face2face.backend.database.firebase.FirebaseUserDb;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.tasks.GiftSendingTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GiftSendingServlet extends ShaharitServlet {
    private static final Logger logger = Logger.getLogger(GiftSendingServlet.class.getName());
    private static final long serialVersionUID = 8126789192972477663L;

    // Servlet members
    private Map<String, SentGift> mGiftsToSend = new HashMap<>();

    // Constants:
    private static final String mNotificationTitle = "קיבלת מתנה";
    private static final String mNotificationMessage = "";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("in gift-servlet doPost");
        new GiftSendingTask(new FirebaseGiftDb(firebase), new FirebaseUserDb(firebase),
                new PushService(new FcmMessenger())).execute();
    }

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        final DatabaseReference giftsDbRef = firebase.child("sent-gifts");
//
//        // TODO: Change this (and others?) to be query-based like ShareEmailServlet.
//        // Extract all gifts
//        giftsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                // Iterate over all gifts and extract unsent gifts
//                for (DataSnapshot ds : children) {
//                    SentGift gift = ds.getValue(SentGift.class);
//                    gift.key = ds.getKey();
//                    String recipientUid = gift.recipientId;
//                    if (!gift.isSent && recipientUid != null) {
//                        mGiftsToSend.put(recipientUid, gift);
//                    }
//                }
//                sendGifts(giftsDbRef);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void sendGifts(final DatabaseReference giftsDbRef) {
//        final DatabaseReference usersDbRef = firebase.child("users");
//        usersDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                // Iterate over all users and send notifications to relevant users.
//                for (DataSnapshot ds : children) {
//                    String userKey = ds.getKey();
//                    if (mGiftsToSend.containsKey(userKey)){
//                        // Check for reg_id
//                        if (ds.child("reg_id").getValue() == null) {
//                            continue;
//                        }
//                        //String userRegId = ds.child("reg_id").getValue().toString();
//                        String userRegId = "fM5wfcOq9XE:APA91bEPwqnFgWBoghASlrgdZM-K8rcPtRqx4YTxBZwRmAVhKrDl9YQHpmByvu8DcHRtBK4pqvlmgvWbmtXYogsjdh-20UQSCixIV0UqQRECU7dQxvWoMoXC-BgOQQRovunmPW_Tobjo";
//
//                        // Build Notification
//                        SentGift gift = mGiftsToSend.get(userKey);
//                        String thoughtTitle = gift.senderGender.equals("male") ? "חושב עליך" :
//                                "חושבת עליך";
//                        String title =  gift.senderName + " " + thoughtTitle;
//                        String message = gift.eventText;
//                        Map<String, String> extras = new HashMap<>();
//                        extras.put("action", "receive_gift");
//                        extras.put("eventTitle", gift.eventTitle);
//                        extras.put("cta", gift.cta);
//                        extras.put("eventText", gift.eventText);
//                        extras.put("senderGender", gift.senderGender);
//                        extras.put("recipientId", gift.recipientId);
//                        extras.put("recipientImageUrl", gift.recipientImageUrl);
//                        extras.put("senderImageUrl", gift.senderImageUrl);
//                        extras.put("senderName", gift.senderName);
//
//
//                        try {
//                            FcmMessenger.sendPushMessage(userRegId, title, message, extras);
//                            // Mark gift sent
//                            DatabaseReference childRef = giftsDbRef.child(gift.key);
//                            childRef.child("sent").setValue(true);
//
//                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
//                            Date date = new Date();
//
//                            childRef.child("date").setValue(formatter.format(date));
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
