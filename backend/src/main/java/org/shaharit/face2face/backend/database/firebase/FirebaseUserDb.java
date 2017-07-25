package org.shaharit.face2face.backend.database.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.models.Buddy;
import org.shaharit.face2face.backend.models.EventNotification;
import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUserDb implements UserDb {
    private static final String SELF_DEFINITIONS = "self-definitions";
    private static final String OTHER_DEFINITIONS = "other-definitions";
    private static final String INTERESTS = "interests";
    private static final String REG_ID = "reg_id";
    private static final String AGE = "age";
    private static final String GENDER = "gender";
    private static final String IMAGE_URL = "image_url";
    private static final String BUDDY = "buddy";
    private static final String UID = "uid";
    private static final String DISPLAY_NAME = "display_name";
    private static final String USERS = "users";
    private static final String EMAIL = "email_address";
    private static final String GIFT_INFO = "giftInfo";


    private final DatabaseReference firebase;

    public FirebaseUserDb(DatabaseReference firebase) {
        this.firebase = firebase;
    }

    @Override
    public void getUsers(final UsersHandler handler) {
        firebase.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot ds : children) {
                    users.add(userFromDs(ds));
                }

                handler.processResult(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw new RuntimeException("Database error: " + databaseError.getMessage() + " with details: " + databaseError.getDetails());
            }
        });
    }

    private User userFromDs(DataSnapshot ds) {
        User user = new User(ds.getKey());

        if (ds.hasChild(REG_ID)) {
            user.regId = ds.child(REG_ID).getValue().toString();
        }

        if (ds.hasChild(AGE)) {
            user.age = Integer.parseInt(ds.child(AGE).getValue().toString());
        }

        if (ds.hasChild(DISPLAY_NAME)) {
            user.displayName = ds.child(DISPLAY_NAME).getValue().toString();
        }

        if (ds.hasChild(DISPLAY_NAME)) {
            user.displayName = ds.child(DISPLAY_NAME).getValue().toString();
        }

        if (ds.hasChild(IMAGE_URL)) {
            user.imageUrl = ds.child(IMAGE_URL).getValue().toString();
        }

        if (ds.hasChild(EMAIL)) {
            user.email = ds.child(EMAIL).getValue().toString();
        }

        if (ds.hasChild(GENDER)) {
            user.gender = Gender.valueOf(ds.child(GENDER).getValue().toString().toUpperCase());
        }

        if (ds.hasChild(BUDDY)) {
            for (DataSnapshot buddiesDs : ds.child(BUDDY).getChildren()) {
                user.buddies.add(buddyFromDs(buddiesDs));
            }
        }
        if (!ds.hasChild(SELF_DEFINITIONS)) {
            return user;
        }
        for (DataSnapshot self_def_ds : ds.child(SELF_DEFINITIONS).getChildren()) {
            user.selfDefs.add(self_def_ds.getValue().toString());
        }

        user.otherDefs = new ArrayList<>();
        if (!ds.hasChild(OTHER_DEFINITIONS)) {
            return user;
        }
        for (DataSnapshot other_def_ds : ds.child(OTHER_DEFINITIONS).getChildren()) {
            user.otherDefs.add(other_def_ds.getValue().toString());
        }

        user.interests = new HashMap<>();
        if (!ds.hasChild(INTERESTS)) {
            return user;
        }
        for (DataSnapshot interests_ds : ds.child(INTERESTS).getChildren()) {
            String interest = interests_ds.getKey();
            user.interests.put(interest, new ArrayList<String>());

            for (DataSnapshot interest_inner_ds : interests_ds.getChildren()) {
                user.interests.get(interest).add(interest_inner_ds.getValue().toString());
            }
        }

        return user;
    }

    private Buddy buddyFromDs(DataSnapshot buddyDs) {
        return buddyDs.getValue(Buddy.class);
    }

    @Override
    public void updateUserBuddies(User user) {

        DatabaseReference userBuddiesRef = firebase.child("users").child(user.uid).child("buddy");

//        for (int i = 0; i < user.buddies.size(); i++) {
//            Buddy buddy = user.buddies.get(i);
//
//            userBuddiesRef.child("imageUrl").setValue(buddy.imageUrl);
//            userBuddiesRef.child(UID).setValue(buddy.uid);
//            userBuddiesRef.child("displayName").setValue(buddy.displayName);
//            userBuddiesRef.child("gender").setValue(buddy.gender);
//            userBuddiesRef.child("selfDefs").setValue(buddy.selfDefs);
//            userBuddiesRef.child("email").setValue(buddy.email);
//        }

        Map<String, Buddy> buddyMap = new HashMap<>(user.buddies.size());

        for (Buddy buddy : user.buddies) {
            buddyMap.put(buddy.uid, buddy);
        }

        userBuddiesRef.setValue(buddyMap);
    }

    @Override
    public void getRegIdForUserIds(final List<String> userIds, final RegIdsHandler handler) {
        firebase.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> res = new HashMap<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();

                    if (!userIds.contains(key)) {
                        continue;
                    }

                    res.put(key, ds.child(REG_ID).getValue().toString());
                }

                handler.processResult(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public String addEventNotification(User user, EventNotification eventNotification) {
        DatabaseReference notification = firebase.child(USERS)
                .child(user.uid).child("notifications").push();

        notification.setValue(eventNotification);

        return notification.getKey();
    }

    @Override
    public String addGift(String recipientUid, Gift gift, String displayName, String imageUrl, String email) {
        DatabaseReference giftRef = firebase.child(USERS)
                .child(recipientUid).child("gifts").push();

        giftRef.child(GIFT_INFO).child("text").setValue(gift.text);
        giftRef.child(GIFT_INFO).child("url").setValue(gift.url);
        giftRef.child(GIFT_INFO).child("type").setValue(gift.type);
        giftRef.child("senderName").setValue(displayName);
        giftRef.child("senderImageUrl").setValue(imageUrl);
        giftRef.child("senderEmail").setValue(email);

        giftRef.child("eventTitle").setValue(gift.eventTitle);

        return giftRef.getKey();
    }

    @Override
    public void getUser(String userId, final UsersHandler usersHandler) {
        firebase.child(USERS).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                users.add(userFromDs(dataSnapshot));

                usersHandler.processResult(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
