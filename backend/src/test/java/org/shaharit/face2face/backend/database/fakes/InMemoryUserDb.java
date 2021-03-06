package org.shaharit.face2face.backend.database.fakes;

import com.google.appengine.repackaged.com.google.common.base.Predicate;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

import org.shaharit.face2face.backend.models.EventNotification;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.database.UserDb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class InMemoryUserDb implements UserDb {
    private final Map<String, User> users;

    public InMemoryUserDb(List<User> users) {
        this.users = new HashMap<>(users.size());

        for (User user : users) {
            addUser(user);
        }

    }

    @Override
    public void getUsers(UsersHandler handler) {
        handler.processResult(new ArrayList<>(users.values()));
    }

    @Override
    public void updateUserBuddies(User user) {
        users.put(user.uid, user);
    }

    @Override
    public void getRegIdForUserIds(final List<String> userIds, RegIdsHandler handler) {
        Map<String, String> res = Maps.transformEntries(users,
                new Maps.EntryTransformer<String, User, String>() {
            @Override
            public String transformEntry(String uid, User user) {
                return user.regId;
            }
        });

        handler.processResult(Maps.filterKeys(res, new Predicate<String>() {
            @Override
            public boolean apply(String uid) {
                return userIds.contains(uid);
            }
        }));
    }

    @Override
    public String addEventNotification(User user, EventNotification eventNotification) {
        return null;
    }

    @Override
    public String addGift(String recipientUid, Gift gift, String displayName, String imageUrl, String email) {
        // We just dummy adding the gift and return the user reg ID immediately
        return users.get(recipientUid).regId;
    }

    @Override
    public void getUser(final String senderUid, UsersHandler usersHandler) {
        usersHandler.processResult(new ArrayList<User>() {{ add(users.get(senderUid));}});
    }

    public void addUser(User user) {
        this.users.put(user.uid, user);
    }
}
