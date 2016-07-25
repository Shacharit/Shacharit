package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;
import com.google.face2face.backend.SentGift;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
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
    private String mNotificationTitle = "קיבלת מתנה";
    private String mNotificationMessage = "";


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
        final DatabaseReference giftsDbRef = firebase.child("sent-gifts");

        // Extract all gifts
        giftsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // Iterate over all gifts and extract unsent gifts
                for (DataSnapshot ds : children) {
                    SentGift gift = ds.getValue(SentGift.class);
                    gift.key = ds.getKey();
                    String recipientUid = gift.recipient;
                    if ("false".equals(gift.sent)) {
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
                        String userRegId = ds.child("reg_id").getValue().toString();

                        // Build Notification
                        SentGift gift = mGiftsToSend.get(userKey);
                        String title = "You Recieved a gift!";
                        String message = "FUN :)";
                        Map<String, String> extras = new HashMap<>();
                        extras.put("event", gift.event);
                        extras.put("name", gift.name);

                        // Send Notification
                        try {
                            FcmMessenger.sendPushMessage(userRegId, title, message, extras);
                            // Mark gift sent
                            giftsDbRef.child(gift.key).child("sent").setValue("true");
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
