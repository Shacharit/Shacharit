package org.shaharit.face2face.backend.tasks;

import org.shaharit.face2face.backend.models.Buddy;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.services.Matcher;

import java.util.ArrayList;
import java.util.List;

public class MatchingTask implements Task {
    private UserDb userDb;
    private PushService pushService;

    public MatchingTask(UserDb userDb, PushService pushService) {
        this.userDb = userDb;
        this.pushService = pushService;
    }

    @Override
    public void execute() {
        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                for (int i = 0; i < users.size(); i++) {
                    checkMatchForEachUser(users, new Matcher(), i);
                }
                System.out.println("Matching complete");
            }
        });
    }

    private void checkMatchForEachUser(List<User> users, Matcher matcher, int i) {
        double[][] scores = matcher.calculateScores(users);
        List<Integer> indicesOfBuddies = matcher.getMatchesForUser(i, scores);
        List<User> buddiesForCurrentMatch = new ArrayList<>();
        final User currentUser = users.get(i);

        for (Integer buddyIndex : indicesOfBuddies) {
            final User buddy = users.get(buddyIndex);
            buddiesForCurrentMatch.add(buddy);
        }

        handleFoundMatch(buddiesForCurrentMatch, currentUser);
    }

    private void handleFoundMatch(List<User> buddiesForCurrentMatch, User currentUser) {
        if (buddiesForCurrentMatch.size() > 0) {
            List<User> newBuddies = currentUser.mergeNewBuddies(buddiesForCurrentMatch);

            userDb.updateUserBuddies(currentUser);

            pushService.sendPushAboutNewBuddies(currentUser.regId, newBuddies);
        }
    }
}
