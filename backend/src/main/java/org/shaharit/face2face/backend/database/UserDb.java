package org.shaharit.face2face.backend.database;

import org.shaharit.face2face.backend.models.User;

import java.util.List;

public interface UserDb {
    void getUsers(UsersHandler handler);
    void updateUserBuddies(User user);

    interface UsersHandler {
        void processResult(List<User> users);
    }
}
