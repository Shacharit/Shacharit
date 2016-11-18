package org.shaharit.face2face.backend.servlets;
import org.shaharit.face2face.backend.database.firebase.FirebaseUserDb;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.services.FirebaseMatchingLog;
import org.shaharit.face2face.backend.tasks.MatchingTask;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MatchingServlet extends ShaharitServlet {
    private static final Logger logger = Logger.getLogger(MatchingServlet.class.getName());

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
        new MatchingTask(new FirebaseUserDb(firebase),
                new PushService(new FcmMessenger()),
                new FirebaseMatchingLog(firebase)).execute();
    }

// Rafi: All logic moved to MatchingTask class
//    private void readFromDb() {
//        firebase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<String> logsInCallback = new ArrayList<>();
//                logsInCallback.add("inside onDataChange. time");
//                buddiesInDb = new HashMap<>();
//
//                firebase.child("logs").setValue(logsInCallback);
//
//                List<User> users = new ArrayList<>();
//                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//
//                for (DataSnapshot ds : children) {
//                    addToPojoUsers(logsInCallback, users, ds);
//                }
//
//                printAllUsersDebug(users);
//
//                Matcher matcher = new Matcher();
//                double[][] scores = matcher.calculateScores(users);
//
//                for (int i = 0; i < users.size(); i++) {
//                    checkMatchForEachUser(logsInCallback, users, matcher, scores, i);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }
//
//    private void checkMatchForEachUser(List<String> logsInCallback,
//                                       List<User> users, Matcher matcher,
//                                       double[][] scores, int i) {
//        List<Integer> indicesOfBuddies = matcher.getMatchesForUser(i, scores);
//        List<User> buddiesForCurrentMatch = new ArrayList<>();
//        final User currentUser = users.get(i);
//
//        for (Integer buddyIndex : indicesOfBuddies) {
//            final User buddy = users.get(buddyIndex);
//            buddiesForCurrentMatch.add(buddy);
//
//            String msg = "user: " + currentUser + " was added a buddy: " + buddy + " time: " + getCurrentTime();
//            logger.info(msg);
//            logsInCallback.add(msg);
//            firebase.child("logs").setValue(logsInCallback);
//            System.out.println(msg);
//        }
//
//        handleFoundMatch(logsInCallback, buddiesForCurrentMatch, currentUser);
//    }
//
//    private void handleFoundMatch(List<String> logsInCallback, List<User> buddiesForCurrentMatch, User currentUser) {
//        if (buddiesForCurrentMatch.size() > 0) {
//            List<User> disjunction = listUtils.nameDisjunction(buddiesInDb.get(currentUser.uid), buddiesForCurrentMatch);
//
//            firebase.child("users").child(currentUser.uid).child("buddy").setValue(buddiesForCurrentMatch);
//            String msg = "users: " + currentUser + " was added buddies: " + buddiesForCurrentMatch + " " +
//                    "time: " + getCurrentTime();
//            logsInCallback.add(msg);
//            firebase.child("logs").setValue(logsInCallback);
//            logger.info(msg);
//
//            sendPushAboutNewBuddies(currentUser.regId, disjunction);
//
//            System.out.println("users: " + currentUser + " was added buddies: " + buddiesForCurrentMatch);
//        }
//    }
//
//    private void printAllUsersDebug(List<User> users) {
//        for (User user : users) {
//            System.out.println(user.uid);
//            logger.info(user.uid);
//            System.out.println(user.interests);
//            logger.info(user.interests.toString());
//            System.out.println(user.otherDefs);
//            logger.info(user.otherDefs.toString());
//            System.out.println(user.selfDefs);
//            logger.info(user.selfDefs.toString());
//        }
//    }
//
//    private void addToPojoUsers(List<String> logsInCallback,
//                                List<User> users, DataSnapshot ds) {
//        logsInCallback.add("after dataSnapshot.getChildren()");
//        firebase.child("logs").setValue(logsInCallback);
//
//
//        System.out.println("user= " + ds.getKey());
//        logger.info("user= " + ds.getKey());
//
//        User user = new User(ds.getKey());
//        if (ds.hasChild(REG_ID)) {
//            user.regId = ds.child(REG_ID).getValue().toString();
//        }
//
//        if (ds.hasChild(AGE)) {
//            user.age = Integer.parseInt(ds.child(AGE).getValue().toString());
//        }
//
//        if (ds.hasChild(DISPLAY_NAME)) {
//            user.displayName = ds.child(DISPLAY_NAME).getValue().toString();
//        }
//        if (ds.hasChild(IMAGE_URL)) {
//            user.imageUrl = ds.child(IMAGE_URL).getValue().toString();
//        }
//
////        if (ds.hasChild(GENDER)) {
////            user.gender = ds.child(GENDER).getValue().toString();
////        }
//
//        if (ds.hasChild(BUDDY)) {
//            List<UserBasicInfo> buddiesFromDb = new ArrayList<>();
//            for (DataSnapshot buddiesDs : ds.child(BUDDY).getChildren()) {
//                String uid = buddiesDs.child(UID).getValue().toString();
//                String imageUrl = buddiesDs.hasChild(IMAGE_URL) ? buddiesDs.child(IMAGE_URL).getValue().toString() : "";
//                UserBasicInfo userBasicInfo = new UserBasicInfo();
//                userBasicInfo.uid = uid;
//                userBasicInfo.imageUrl = imageUrl;
//                buddiesFromDb.add(userBasicInfo);
//            }
//            buddiesInDb.put(user.uid, buddiesFromDb);
//        }
//        if (!ds.hasChild(SELF_DEFINITIONS)) {
//            return;
//        }
//        for (DataSnapshot self_def_ds : ds.child(SELF_DEFINITIONS).getChildren()) {
//            user.selfDefs.add(self_def_ds.getValue().toString());
//        }
//
//        user.otherDefs = new ArrayList<>();
//        if (!ds.hasChild(OTHER_DEFINITIONS)) {
//            return;
//        }
//        for (DataSnapshot other_def_ds : ds.child(OTHER_DEFINITIONS).getChildren()) {
//            user.otherDefs.add(other_def_ds.getValue().toString());
//        }
//
//        user.interests = new HashMap<>();
//        if (!ds.hasChild(INTERESTS)) {
//            return;
//        }
//        for (DataSnapshot interests_ds : ds.child(INTERESTS).getChildren()) {
//            String interest = interests_ds.getKey();
//            user.interests.put(interest, new ArrayList<String>());
//
//            for (DataSnapshot interest_inner_ds : interests_ds.getChildren()) {
//                user.interests.get(interest).add(interest_inner_ds.getValue().toString());
//            }
//        }
//        users.add(user);
//    }
//
//    private String getCurrentTime() {
//        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
//        Date now = new Date();
//        return sdfDate.format(now);
//    }
//
//    private void sendPushAboutNewBuddies(String uid, List<User> newBuddies) {
//        Map<String, String> extras = new HashMap<>();
//        for (User newUser : newBuddies) {
//            extras.put("uid", newUser.uid);
//            extras.put("image_url", newUser.imageUrl);
//            extras.put("action", "match");
//            try {
//                FcmMessenger.sendPushMessage(uid, "new buddies", "say hi to him/her", extras);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}