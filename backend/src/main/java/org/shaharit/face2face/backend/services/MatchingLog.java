package org.shaharit.face2face.backend.services;

import org.shaharit.face2face.backend.models.User;

public interface MatchingLog {
    void logMatchSummary(User user, User potentialBuddy, MatchSummary matchSummary);

    void logTrace(String tag, String msg);
}
