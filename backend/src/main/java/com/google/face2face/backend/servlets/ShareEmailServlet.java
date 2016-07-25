package com.google.face2face.backend.servlets;

import com.google.face2face.backend.FcmMessenger;
import com.google.face2face.backend.ShareRequest;
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

public class ShareEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 8126789192972477663L;

    // Firebase keys shared with client applications
    private DatabaseReference firebase;

    // Constants:
    public static String SendEmailAction = "send_email";

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
        final DatabaseReference shareRequestsDbRef = firebase.child("share_requests");

        // Extract all gifts
        shareRequestsDbRef.orderByChild("handled").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShareRequest request = dataSnapshot.getValue(ShareRequest.class);
                doShare(request, shareRequestsDbRef);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void doShare(final ShareRequest request, final DatabaseReference shareRequestsDbRef) {
        final DatabaseReference usersDbRef = firebase.child("users");
        usersDbRef.child(request.shareeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot shareeDataSnapshot) {
                final String shareeRegId = shareeDataSnapshot.child("reg_id").getValue().toString();
                usersDbRef.child(request.sharerId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot sharerDataSnapshot) {
                        String sharerName = sharerDataSnapshot.child("display_name").getValue().toString();
                        String sharerEmail = sharerDataSnapshot.child("email_address").getValue().toString();
                        // Build Notification
                        String title = String.format("{0} would like to talk to you!", sharerName);
                        String message = String.format("Click here to compose an email message to {0}.", sharerName);
                        Map<String, String> extras = new HashMap<>();
                        extras.put("action", SendEmailAction);
                        extras.put("email", sharerEmail);
                        // Send Notification
                        try {
                            FcmMessenger.sendPushMessage(shareeRegId, title, message, extras);
                            // Mark gift sent
                            shareRequestsDbRef.child(request.key).child("handled").setValue("true");
                        } catch (IOException e) {
                            e.printStackTrace();
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
