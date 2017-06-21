package org.shaharit.face2face.backend.services;

import com.google.firebase.database.DatabaseReference;

import org.shaharit.face2face.backend.models.User;

public class FirebaseMatchingLog implements MatchingLog {
    private static final String LOGS = "logs";
    private final DatabaseReference firebase;

    public FirebaseMatchingLog(DatabaseReference firebase) {
        this.firebase = firebase;
    }

    @Override
    public void logMatchSummary(User user, User potentialBuddy, MatchSummary matchSummary) {
        getLogsRef().child(user.uid).child(potentialBuddy.uid).setValue(matchSummary);
    }

    private DatabaseReference getLogsRef() {
        return firebase.child(LOGS);
    }

    @Override
    public void logTrace(String tag, String msg) {
        getLogsRef().child(tag);
    }
}
