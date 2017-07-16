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
import org.shaharit.face2face.backend.servlets.MatchingServlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MatchingTask implements Task {
    private static final Logger logger = Logger.getLogger(MatchingTask.class.getName());

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
        logger.info("Start match task");
        matchingLog.logTrace("StartTime", String.valueOf(System.currentTimeMillis()));
        userDb.getUsers(new MatchingUserHandler());
        logger.info("Waiting for DB callback");
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
                final Map<String, MatchSummary> buddySummaries = new HashMap<>();

                for (Map.Entry<User, MatchSummary> userMatchSummaryEntry :
                        userMapEntry.getValue().entrySet()) {
                    final User potentialBuddy = userMatchSummaryEntry.getKey();
                    final MatchSummary matchSummary = userMatchSummaryEntry.getValue();

                    if (matchSummary.matchResult.equals(MatchResult.MATCH)) {
                        matchedBuddies.add(potentialBuddy);
                        buddySummaries.put(potentialBuddy.uid, matchSummary);
                    }
                    matchingLog.logMatchSummary(user, potentialBuddy,
                            matchSummary);
                }
                handleFoundMatch(matchedBuddies, buddySummaries, user);
            }

            matchingLog.logTrace("EndTime", String.valueOf(System.currentTimeMillis()));
        }
    }

    private void handleFoundMatch(List<User> buddiesForCurrentMatch, Map<String, MatchSummary> buddySummaries, User currentUser) {
        if (buddiesForCurrentMatch.size() > 0) {
            List<User> newBuddies = currentUser.mergeNewBuddies(buddiesForCurrentMatch, buddySummaries);

            userDb.updateUserBuddies(currentUser);

            pushService.sendPushAboutNewBuddies(currentUser.regId, newBuddies);
        }
    }
}
