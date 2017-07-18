package org.shaharit.face2face;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kalisky on 7/18/17.
 */

public class SentGiftsFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "GiveGiftFragment";
    private DatabaseReference mFirebaseDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sent_gifts, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        return view;
    }
}
