package org.shaharit.face2face.gifts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.Constants;
import org.shaharit.face2face.R;
import org.shaharit.face2face.model.GiftSuggestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalisky on 7/18/17.
 */

public class ReceivedGiftsFragment extends Fragment {
    private DatabaseReference mFirebaseDatabaseReference;
    private List<Gift> gifts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_with_list, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ListView listView = (ListView) view.findViewById(R.id.list);
        final SentGiftsAdapter adapter = new SentGiftsAdapter(getContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (gifts == null || gifts.size() <= position) {
                    return;
                }
//                Events.FriendClickedEvent event = new Events.FriendClickedEvent();
//                event.userId = gifts.get(position).uid;
//                EventBus.getInstance().post(event);
            }
        });
        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.GIFT_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gifts = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final Gift gift = data.getValue(Gift.class);
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
}
