package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;
import com.google.face2face.backend.Gift;
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
        final DatabaseReference giftsDbRef = firebase.child("sent-gifts");

        // Extract all gifts
        giftsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // Iterate over all gifts and extract unsent gifts
                for (DataSnapshot ds : children) {
                    Gift gift = ds.getValue(Gift.class);
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
                        Gift gift = mGiftsToSend.get(userKey);
                        String user_reg_id = userRegId;
                        String title = "You Recieved a gift!";
                        String message = "FUN :)";
                        Map<String, String> extras = new HashMap<>();
                        extras.put("event", gift.event);
                        extras.put("name", gift.name);

                        // Send Notification
                        try {
                            FcmMessenger.sendPushMessage(user_reg_id, title, message, extras);
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
