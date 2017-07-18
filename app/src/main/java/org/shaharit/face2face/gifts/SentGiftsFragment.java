package org.shaharit.face2face.gifts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.Constants;
import org.shaharit.face2face.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalisky on 7/18/17.
 */

public class SentGiftsFragment extends Fragment {
    private DatabaseReference mFirebaseDatabaseReference;
    private List<SentGift> gifts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_with_list, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ListView listView = (ListView) view.findViewById(R.id.list);
        final SentGiftsAdapter adapter = new SentGiftsAdapter(getContext());
        listView.setAdapter(adapter);
        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.SENT_GIFTS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gifts = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final SentGift gift = data.getValue(SentGift.class);
                    gifts.add(gift);
                }
                adapter.setItems(gifts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }
}
