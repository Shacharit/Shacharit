package org.shaharit.face2face.backend.models;

import com.google.appengine.repackaged.com.google.common.base.Predicate;
import com.google.appengine.repackaged.com.google.common.collect.Iterables;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

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
                Buddy buddy = new Buddy(user);
                buddies.add(buddy);
                newBuddies.add(user);
            }
        }

        return newBuddies;
    }

    private boolean hasBuddyWithId(final String uid) {
        return Iterables.any(buddies, new Predicate<Buddy>() {
            @Override
            public boolean apply(Buddy buddy) {
                return buddy.uid.equals(uid);
            }
        });
    }

    public List<Buddy> getRelevantBuddiesForEvent(final Event event) {
        return Lists.newArrayList(Iterables.filter(buddies, new Predicate<Buddy>() {
            @Override
            public boolean apply(Buddy buddy) {
                return buddy.caresAboutEvent(event);
            }
        }));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return uid != null ? uid.equals(user.uid) : user.uid == null;

    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }
}
