package org.shaharit.face2face.backend.database.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.models.Buddy;
import org.shaharit.face2face.backend.models.Gender;
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
        if (ds.hasChild(IMAGE_URL)) {
            user.imageUrl = ds.child(IMAGE_URL).getValue().toString();
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
        Buddy buddy = new Buddy();
        buddy.imageUrl = buddyDs.child("imageUrl").getValue().toString();
        buddy.uid = buddyDs.child(UID).getValue().toString();
        buddy.displayName = buddyDs.child("displayName").getValue().toString();
        buddy.gender = Gender.valueOf(buddyDs.child("gender").getValue().toString().toUpperCase());

        for (DataSnapshot selfDefsDs : buddyDs.child("selfDefs").getChildren()) {
            buddy.selfDefs.add(selfDefsDs.getValue().toString());
        }

        return buddy;
    }

    @Override
    public void updateUserBuddies(User user) {

        DatabaseReference userBuddiesRef = firebase.child("users").child(user.uid).child("buddy");

        for (int i = 0; i < user.buddies.size(); i++) {
            Buddy buddy = user.buddies.get(i);

            userBuddiesRef.child("imageUrl").setValue(buddy.imageUrl);
            userBuddiesRef.child(UID).setValue(buddy.uid);
            userBuddiesRef.child("displayName").setValue(buddy.displayName);
            userBuddiesRef.child("gender").setValue(buddy.gender);
            userBuddiesRef.child("selfDefs").setValue(buddy.selfDefs);
        }

        userBuddiesRef.setValue(user.buddies);
    }

    @Override
    public void getRegIdForUserIds(List<String> userIds, final RegIdsHandler handler) {
        firebase.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> res = new HashMap<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    res.put(ds.getKey(), ds.child(REG_ID).getValue().toString());
                }

                handler.processResult(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
