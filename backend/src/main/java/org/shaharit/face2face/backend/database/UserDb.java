package org.shaharit.face2face.backend.database;

import org.shaharit.face2face.backend.models.EventNotification;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.User;

import java.util.List;
import java.util.Map;

public interface UserDb {
    void getUsers(UsersHandler handler);
    void updateUserBuddies(User user);

    void getRegIdForUserIds(List<String> userIds, RegIdsHandler handler);

    String addEventNotification(User user, EventNotification eventNotification);

    String addGift(String recipientUid, Gift gift);

    interface UsersHandler extends ResultHandler<List<User>> {}
    interface RegIdsHandler extends ResultHandler<Map<String, String>> {}
}
