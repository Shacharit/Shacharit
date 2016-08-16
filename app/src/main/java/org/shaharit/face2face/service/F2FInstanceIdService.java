package org.shaharit.face2face.service;

import android.preference.PreferenceManager;
import android.util.Log;

import org.shaharit.face2face.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class F2FInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "F2FInstanceIdService";
    private static final String F2F_ENGAGE_TOPIC = "f2f_engage";

    private DatabaseReference mDatabaseReference;

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token: " + token);

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString(Constants.PUSH_TOKEN, token).apply();

        // Save token under user
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // TODO: Actually use user

//        mDatabaseReference.child(Constants.USERS_CHILD).child(currentUser.getUid())
//                .child("reg_id").setValue(token);


        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance()
                .subscribeToTopic(F2F_ENGAGE_TOPIC);
    }
}
