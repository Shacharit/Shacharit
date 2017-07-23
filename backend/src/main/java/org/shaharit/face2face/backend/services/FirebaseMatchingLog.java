package org.shaharit.face2face.backend.services;

import com.google.firebase.database.DatabaseReference;

import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.servlets.ShaharitServlet;

import java.util.logging.Logger;

public class FirebaseMatchingLog implements MatchingLog {
    private static final Logger logger = Logger.getLogger(FirebaseMatchingLog.class.getName());

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
        logger.info(String.format("logging msg: \"%s\" under tag\"%s\"", msg, tag));
        getLogsRef().child(tag).setValue(msg);
    }
}
