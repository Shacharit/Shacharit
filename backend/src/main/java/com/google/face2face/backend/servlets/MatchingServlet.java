package com.google.face2face.backend.servlets;
/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/


import com.google.face2face.backend.FcmMessenger;
import com.google.face2face.backend.User;
import com.google.face2face.backend.UserBasicInfo;
import com.google.face2face.backend.services.Matcher;
import com.google.face2face.backend.utils.ListUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MatchingServlet extends HttpServlet {

    private static final long serialVersionUID = 8126789192972477663L;
    public static final String SELF_DEFINITIONS = "self-definitions";
    public static final String OTHER_DEFINITIONS = "other-definitions";
    public static final String INTERESTS = "interests";
    public static final String REG_ID = "reg_id";
    public static final String AGE = "age";
    public static final String GENDER = "gender";
    public static final String IMAGE_URL = "image_url";
    public static final String BUDDY = "buddy";
    public static final String UID = "uid";

    // Firebase keys shared with client applications
    private DatabaseReference firebase;
    private ListUtils listUtils;


    private static final Logger logger = Logger.getLogger(MatchingServlet.class.getName());


    @Override
    public void init(ServletConfig config) {
        String credential = config.getInitParameter("credential");
        String databaseUrl = config.getInitParameter("databaseUrl");

        System.out.println("Credential file : " + credential);
        logger.info("Credential file : " + credential);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(config.getServletContext().getResourceAsStream(credential))
                .setDatabaseUrl(databaseUrl)
                .build();
        FirebaseApp.initializeApp(options);
        firebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("in matching-servlet doGet");
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.info("in matching-servlet doPost");
        readFromDb();

    }

    private void readFromDb() {
        firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> logsInCallback = new ArrayList<String>();
                logsInCallback.add("inside onDataChange. time: " + LocalDateTime.now());
                List<UserBasicInfo> buddiesInDb = null;

                firebase.child("logs").setValue(logsInCallback);

                List<User> users = new ArrayList<User>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                System.out.println("children = " + children);
                logger.info("children = " + children);
                for (DataSnapshot ds : children) {

                    logsInCallback.add("after dataSnapshot.getChildren(). time: " + LocalDateTime.now());
                    firebase.child("logs").setValue(logsInCallback);


                    System.out.println("user= " + ds.getKey());
                    logger.info("user= " + ds.getKey());

                    User user = new User(ds.getKey());
                    if (ds.hasChild(REG_ID)) {
                        user.regId = ds.child(REG_ID).getValue().toString();
                    }

                    if (ds.hasChild(AGE)) {
                        user.age = Integer.parseInt(ds.child(AGE).getValue().toString());
                    }

                    if (ds.hasChild(IMAGE_URL)) {
                        user.imageUrl = ds.child(IMAGE_URL).getValue().toString();
                    }

                    if (ds.hasChild(GENDER)) {
                        user.gender = ds.child(GENDER).getValue().toString();
                    }

                    if (ds.hasChild(BUDDY)) {
                        buddiesInDb = new ArrayList<UserBasicInfo>();
                        for (DataSnapshot buddiesDs : ds.child(BUDDY).getChildren()) {

                            String uid = buddiesDs.child(UID).getValue().toString();
                            String imageUrl = buddiesDs.child(IMAGE_URL).getValue().toString();
                            UserBasicInfo userBasicInfo = new UserBasicInfo();
                            userBasicInfo.uid = uid;
                            userBasicInfo.imageUrl = imageUrl;
                            buddiesInDb.add(userBasicInfo);
                        }
                    }
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
                    logger.info(user.uid);
                    System.out.println(user.interests);
                    logger.info(user.interests.toString());
                    System.out.println(user.otherDefs);
                    logger.info(user.otherDefs.toString());
                    System.out.println(user.selfDefs);
                    logger.info(user.selfDefs.toString());
                }

                Matcher matcher = new Matcher();
                double[][] scores = matcher.calculateScores(users);

                for (int i = 0; i < users.size(); i++) {
                    List<Integer> indicesOfBuddies = matcher.getMatchesForUser(i, scores);
                    List<User> newBuddies = new ArrayList<User>();
                    final User currentUser = users.get(i);

                    for (Integer buddyIndex : indicesOfBuddies) {
                        final User buddy = users.get(buddyIndex);
                        newBuddies.add(buddy);


                        String msg = "user: " + currentUser + " was added a buddy: " + buddy + " time: " +
                                LocalDateTime.now();
                        logger.info(msg);
                        logsInCallback.add(msg);
                        firebase.child("logs").setValue(logsInCallback);

                        System.out.println(msg);
                    }

                    if (newBuddies.size() > 0) {
                        List<User> disjunction = listUtils.nameDisjunction(buddiesInDb, newBuddies);

                        sendPushAboutNewBuddies(currentUser.uid, disjunction);
                        firebase.child("users").child(currentUser.uid).child("buddy").setValue(newBuddies);
                        String msg = "users: " + currentUser + " was added buddiesInDb: " + newBuddies + " time: " +
                                LocalDateTime.now();
                        logsInCallback.add(msg);
                        firebase.child("logs").setValue(logsInCallback);
                        logger.info(msg);

                        System.out.println("users: " + currentUser + " was added buddiesInDb: " + newBuddies);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendPushAboutNewBuddies(String uid, List<User> newBuddies) {
        Map<String, String> extras = new HashMap();
        for (User newUser : newBuddies) {
            extras.put("uid", newUser.uid);
            extras.put("image_url", newUser.imageUrl);
            try {
                FcmMessenger.sendPushMessage(uid, "new buddies", "say hi to him/her" ,extras);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String buddiesToString(List<User> newBuddies) {


        //The string builder used to construct the string
        StringBuilder commaSepValueBuilder = new StringBuilder();

        //Looping through the list
        for (int i = 0; i < newBuddies.size(); i++){
            //append the value into the builder
            commaSepValueBuilder.append(newBuddies.get(i));

        }
        return commaSepValueBuilder.toString();
    }
}