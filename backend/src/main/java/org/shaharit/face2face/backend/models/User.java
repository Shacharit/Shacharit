package org.shaharit.face2face.backend.models;

import com.google.appengine.repackaged.com.google.common.base.Predicate;
import com.google.appengine.repackaged.com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class User {
    public String uid;
    public ArrayList<Buddy> buddies;
    public ArrayList<String> selfDefs;
    public ArrayList<String> otherDefs;
    public Map<String, List<String>> interests;
    public String regId;
    public Gender gender;
    public int age;
    public String imageUrl;
    public String displayName;

    // Empty constructor for construction via Firebase
    public User() {}

    public User(String uid) {
        this.uid = uid;
        this.selfDefs = new ArrayList<>();
        this.otherDefs = new ArrayList<>();
        this.interests = new HashMap<>();
        this.buddies = new ArrayList<>();
    }

    public List<User> mergeNewBuddies(List<User> buddiesForCurrentMatch) {
        List<User> newBuddies = new ArrayList<>();

        for (User user : buddiesForCurrentMatch) {
            if (!hasBuddyWithId(user.uid)) {
                Buddy buddy = new Buddy(user.uid, user.displayName);
                buddies.add(buddy);
                newBuddies.add(user);
            }
        }

        return newBuddies;
    }

    private boolean hasBuddyWithId(final String uid) {
        return Iterables.any(buddies, new Predicate<Buddy>() {
            @Override
            public boolean apply(@Nullable Buddy buddy) {
                return buddy.uid.equals(uid);
            }
        });
    }
}
