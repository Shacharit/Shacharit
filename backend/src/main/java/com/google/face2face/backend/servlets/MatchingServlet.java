package com.google.face2face.backend.servlets;
/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/


import com.google.face2face.backend.User;
import com.google.face2face.backend.services.Matcher;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MatchingServlet extends HttpServlet {

    private static final long serialVersionUID = 8126789192972477663L;
    public static final String SELF_DEFINITIONS = "self-definitions";
    public static final String OTHER_DEFINITIONS = "other-definitions";
    public static final String INTERESTS = "interests";

    // Firebase keys shared with client applications
    private DatabaseReference firebase;


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

        try {
            doPost(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read from snapshot
//                Object users = dataSnapshot.getValue();
//                System.out.println(users);
                List<User> users = new ArrayList<User>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot ds : children) {
                    User user = new User();
                    user.uid = ds.getKey();

                    user.selfDefs = new ArrayList<String>();
                    if (!ds.hasChild(SELF_DEFINITIONS)) {
                        continue;
                    }
                    for (DataSnapshot self_def_ds : ds.child(SELF_DEFINITIONS).getChildren()) {
                        user.selfDefs.add(self_def_ds.getKey());
                    }


                    user.otherDefs = new ArrayList<String>();
                    if (!ds.hasChild(OTHER_DEFINITIONS)) {
                        continue;
                    }
                    for (DataSnapshot other_def_ds : ds.child(OTHER_DEFINITIONS).getChildren()) {
                        user.otherDefs.add(other_def_ds.getKey());
                    }

                    user.interests = new HashMap<>();
                    if (!ds.hasChild(INTERESTS)) {
                        continue;
                    }
                    for (DataSnapshot interests_ds : ds.child(INTERESTS).getChildren()) {
                        String interest = interests_ds.getKey();
                        user.interests.put(interest, new ArrayList<String>());

                        for (DataSnapshot interest_inner_ds : interests_ds.getChildren()) {
                            System.out.println("Found 1");
                            user.interests.get(interest).add(interest_inner_ds.getKey());
                        }
                    }
                    users.add(user);
                }

                for (User user : users) {
                    System.out.println(user.uid);
                    System.out.println(user.interests);
                    System.out.println(user.otherDefs);
                    System.out.println(user.selfDefs);
                }

                Matcher matcher = new Matcher();
                double[][] scores = matcher.matchUsers(users);

                for (int i = 0; i < users.size(); i++) {
                    int matchForUser = matcher.getMatchForUser(i, scores);
                    User currentUser = users.get(i);
                    User buddy = users.get(matchForUser);

                    firebase.child("users").child(currentUser.uid).child("buddy")
                            .setValue(buddy.uid);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

