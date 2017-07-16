package org.shaharit.face2face;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class GotGiftFragment extends Fragment {
    private static final String TAG = "GotGiftFragment";
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_match, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final String giftId = getArguments().getString("giftId");

        return view;
    }
}
