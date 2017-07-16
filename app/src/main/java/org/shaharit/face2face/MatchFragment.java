package org.shaharit.face2face;

import android.content.Intent;
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
                        final Buddy buddy = dataSnapshot.getValue(Buddy.class);
                        NetworkImageView buddyImg = (NetworkImageView) view.findViewById(org.shaharit.face2face.R.id.buddy_image);
                        if (buddyImg == null) {
                            return;
                        }
                        buddyImg.setImageUrl(buddy.imageUrl,
                                VolleySingleton.getInstance(getContext()).getImageLoader());
                        TextView buddyName = (TextView) view.findViewById(org.shaharit.face2face.R.id.buddy_name);
                        buddyName.setText(buddy.displayName);

                        String sharedStr = "";
                        int i = 0;
                        for (; i < buddy.sharedInterests.size() - 1; i++) {
                            String sharedInterest = buddy.sharedInterests.get(i);
                            sharedStr += sharedInterest + ", ";
                        }
                        if (buddy.sharedInterests.size() > 0) {
                            sharedStr += buddy.sharedInterests.get(i);
                            ((TextView) view.findViewById(R.id.sharedInterestsText)).setText(sharedStr);
                        } else {
                            view.findViewById(R.id.sharedInterestsLayout).setVisibility(View.GONE);
                        }

                        String notSharedStr = "";
                        i = 0;
                        for (; i < buddy.notSharedInterests.size() - 1; i++) {
                            String interest = buddy.notSharedInterests.get(i);
                            notSharedStr += interest + ", ";
                        }
                        if (buddy.notSharedInterests.size() > 0) {
                            notSharedStr += buddy.notSharedInterests.get(i);
                            ((TextView) view.findViewById(R.id.interestsText)).setText(notSharedStr);
                        } else {
                            view.findViewById(R.id.interestsLayout).setVisibility(View.GONE);
                        }

                        view.findViewById(R.id.fabEmail).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{buddy.email});
                                email.setType("message/rfc822");
                                startActivity(Intent.createChooser(email, ""));
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return view;
    }
}
