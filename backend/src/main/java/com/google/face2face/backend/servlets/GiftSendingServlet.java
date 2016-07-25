package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;
import com.google.face2face.backend.FirebaseInitializer;
import com.google.face2face.backend.SentGift;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GiftSendingServlet extends HttpServlet {
    private static final long serialVersionUID = 8126789192972477663L;

    // Firebase keys shared with client applications
    private DatabaseReference firebase;

    // Servlet members
    private Map<String, SentGift> mGiftsToSend = new HashMap<>();

    // Constants:
    private static final String mNotificationTitle = "קיבלת מתנה";
    private static final String mNotificationMessage = "";


    @Override
    public void init(ServletConfig config) {
        FirebaseInitializer.initializeFirebase();
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final DatabaseReference giftsDbRef = firebase.child("sent-gifts");

        // TODO: Change this (and others?) to be query-based like ShareEmailServlet.
        // Extract all gifts
        giftsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // Iterate over all gifts and extract unsent gifts
                for (DataSnapshot ds : children) {
                    SentGift gift = ds.getValue(SentGift.class);
                    gift.key = ds.getKey();
                    String recipientUid = gift.recipientId;
                    if ("false".equals(gift.isSent) && recipientUid != null) {
                        mGiftsToSend.put(recipientUid, gift);
                    }
                }
                sendGifts(giftsDbRef);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendGifts(final DatabaseReference giftsDbRef) {
        final DatabaseReference usersDbRef = firebase.child("users");
        usersDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // Iterate over all users and send notifications to relevant users.
                for (DataSnapshot ds : children) {
                    String userKey = ds.getKey();
                    if (mGiftsToSend.containsKey(userKey)){
                        // Check for reg_id
                        if (ds.child("reg_id").getValue() == null) {
                            continue;
                        }
                        //String userRegId = ds.child("reg_id").getValue().toString();
                        String userRegId = "dMGFRFCiywo:APA91bHvrQKrIqeF98M5MSNVhoIyRcXFiS6UqGWJgLMyQebUOzJXp43y3XafBx6Ip04ytseng6a7VakoosZD8OMpfce-w2q0qf9jjku8e5aNarw5ZYqLNthzN4eDitEaAXD8ZvR24FE4";

                        // Build Notification
                        SentGift gift = mGiftsToSend.get(userKey);
                        String thoughtTitle = gift.senderGender.equals("male") ? "חושב עליך" :
                                "חושבת עליך";
                        String title =  gift.senderName + " " + thoughtTitle;
                        String message = gift.eventText;
                        Map<String, String> extras = new HashMap<>();
                        extras.put("eventTitle", gift.eventTitle);
                        extras.put("cta", gift.cta);
                        extras.put("eventText", gift.eventText);
                        extras.put("senderGender", gift.senderGender);
                        extras.put("recipientId", gift.recipientId);
                        extras.put("recipientImageUrl", gift.recipientImageUrl);
                        extras.put("senderImageUrl", gift.senderImageUrl);
                        extras.put("senderName", gift.senderName);


                        try {
                            FcmMessenger.sendPushMessage(userRegId, title, message, extras);
                            // Mark gift sent
                            DatabaseReference childRef = giftsDbRef.child(gift.key);
                            childRef.child("sent").setValue("true");

                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                            Date date = new Date();

                            childRef.child("date").setValue(formatter.format(date));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
