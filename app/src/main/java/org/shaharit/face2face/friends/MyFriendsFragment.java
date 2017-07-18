package org.shaharit.face2face.friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.Constants;
import org.shaharit.face2face.R;
import org.shaharit.face2face.events.Events;
import org.shaharit.face2face.model.Buddy;
import org.shaharit.face2face.model.EventNotification;
import org.shaharit.face2face.utils.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyFriendsFragment extends Fragment {

    private DatabaseReference mFirebaseDatabaseReference;
    private List<Buddy> buddies;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list);
        final FriendAdapter adapter = new FriendAdapter(getContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (buddies == null || buddies.size() >= position) {
                    return;
                }
                Events.FriendClickedEvent event = new Events.FriendClickedEvent();
                event.userId = buddies.get(position).uid;
                EventBus.getInstance().post(event);
            }
        });
        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.BUDDY_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buddies = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final Buddy buddy = data.getValue(Buddy.class);
                    buddies.add(buddy);
                }
                adapter.setItems(buddies);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}