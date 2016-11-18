package org.shaharit.face2face.backend.services.fakes;

import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.services.MatchSummary;
import org.shaharit.face2face.backend.services.MatchingLog;

import java.util.HashMap;
import java.util.Map;

public class InMemoryMatchingLog implements MatchingLog {

    private Map<String, Map<String, MatchSummary>> userSummariesMap = new HashMap<>();

    public Map<String, MatchSummary> getSummaryLogForUser(String uid) {
        verifyHasKey(uid);
        return userSummariesMap.get(uid);
    }

    @Override
    public void logMatchSummary(User user, User potentialBuddy, MatchSummary matchSummary) {
        String uid = user.uid;
        verifyHasKey(uid);

        userSummariesMap.get(uid).put(potentialBuddy.uid, matchSummary);
    }

    private void verifyHasKey(String uid) {
        if (!userSummariesMap.containsKey(uid)) {
            userSummariesMap.put(uid, new HashMap<String, MatchSummary>());
        }
    }
}
