package org.shaharit.face2face.backend.tasks;

import com.google.common.collect.Iterables;

import org.shaharit.face2face.backend.models.Buddy;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.services.MatchResult;
import org.shaharit.face2face.backend.services.MatchSummary;
import org.shaharit.face2face.backend.services.Matcher;
import org.shaharit.face2face.backend.services.MatchingLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchingTask implements Task {
    private UserDb userDb;
    private PushService pushService;
    private MatchingLog matchingLog;

    public MatchingTask(UserDb userDb, PushService pushService, MatchingLog matchingLog) {
        this.userDb = userDb;
        this.pushService = pushService;
        this.matchingLog = matchingLog;
    }

    @Override
    public void execute() {
        userDb.getUsers(new MatchingUserHandler());
    }

    private class MatchingUserHandler implements UserDb.UsersHandler {
        @Override
        public void processResult(List<User> users) {
            Map<User, Map<User, MatchSummary>> matchingResult =
                    new Matcher().calculateScores(users);

            for (Map.Entry<User, Map<User, MatchSummary>> userMapEntry :
                    matchingResult.entrySet()) {
                final User user = userMapEntry.getKey();
                final List<User> matchedBuddies = new ArrayList<>();

                for (Map.Entry<User, MatchSummary> userMatchSummaryEntry :
                        userMapEntry.getValue().entrySet()) {
                    final User potentialBuddy = userMatchSummaryEntry.getKey();
                    final MatchSummary matchSummary = userMatchSummaryEntry.getValue();

                    if (matchSummary.matchResult.equals(MatchResult.MATCH)) {
                        matchedBuddies.add(potentialBuddy);
                    }
                    matchingLog.logMatchSummary(user, potentialBuddy,
                            matchSummary);
                }
                handleFoundMatch(matchedBuddies, user);
            }

            System.out.println("Matching complete");
        }
    }

    private void handleFoundMatch(List<User> buddiesForCurrentMatch, User currentUser) {
        if (buddiesForCurrentMatch.size() > 0) {
            List<User> newBuddies = currentUser.mergeNewBuddies(buddiesForCurrentMatch);

            userDb.updateUserBuddies(currentUser);

            pushService.sendPushAboutNewBuddies(currentUser.regId, newBuddies);
        }
    }
}
