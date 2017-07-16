package org.shaharit.face2face;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.model.Buddy;
import org.shaharit.face2face.service.VolleySingleton;

import static android.R.attr.data;

public class MatchFragment extends Fragment {

    private static final String TAG = "MatchFragment";
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_match, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final String userId = getArguments().getString("userId");
        if (userId == null) {
            Log.e(TAG, "userId is null");
            return view;
        }
        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.BUDDY_CHILD)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Buddy buddy = dataSnapshot.getValue(Buddy.class);
                        NetworkImageView buddyImg = (NetworkImageView) view.findViewById(org.shaharit.face2face.R.id.buddy_image);
                        if (buddyImg == null) {
                            return;
                        }
                        buddyImg.setImageUrl(buddy.imageUrl,
                                VolleySingleton.getInstance(getContext()).getImageLoader());
                        TextView buddy_name = (TextView) view.findViewById(org.shaharit.face2face.R.id.buddy_name);
                        buddy_name.setText(buddy.displayName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return view;
    }
}
