package org.shaharit.face2face.backend.services;

import com.google.firebase.database.DatabaseReference;

import org.shaharit.face2face.backend.models.User;

public class FirebaseMatchingLog implements MatchingLog {
    private final DatabaseReference firebase;

    public FirebaseMatchingLog(DatabaseReference firebase) {

        this.firebase = firebase;
    }

    @Override
    public void logMatchSummary(User user, User potentialBuddy, MatchSummary matchSummary) {
        firebase.child("logs").child(user.uid).child(potentialBuddy.uid).setValue(matchSummary);
    }
}
