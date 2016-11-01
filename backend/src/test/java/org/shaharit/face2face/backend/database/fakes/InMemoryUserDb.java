package org.shaharit.face2face.backend.database.fakes;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.database.UserDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        handler.processResult(new ArrayList(users.values()));
    }

    @Override
    public void updateUserBuddies(User user) {
        users.put(user.uid, user);
    }

    public void addUser(User user) {
        this.users.put(user.uid, user);
    }
}
