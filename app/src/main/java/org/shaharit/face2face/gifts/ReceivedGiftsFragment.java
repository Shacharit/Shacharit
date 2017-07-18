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
import org.shaharit.face2face.events.Events;
import org.shaharit.face2face.utils.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kalisky on 7/18/17.
 */

public class ReceivedGiftsFragment extends Fragment {
    private DatabaseReference mFirebaseDatabaseReference;
    private List<ReceivedGift> gifts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_with_list, container, false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ListView listView = (ListView) view.findViewById(R.id.list);
        final ReceivedGiftsAdapter adapter = new ReceivedGiftsAdapter(getContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (gifts == null || gifts.size() <= position) {
                    return;
                }
                Events.GiftClickedEvent event = new Events.GiftClickedEvent();
                event.giftId = gifts.get(position).id;
                EventBus.getInstance().post(event);
            }
        });
        mFirebaseDatabaseReference
                .child(Constants.USERS_CHILD)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.GIFT_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gifts = new ArrayList<>();
                for (DataSnapshot giftData : dataSnapshot.getChildren()) {
                    ReceivedGift gift = new ReceivedGift();
                    for (DataSnapshot field : giftData.getChildren()) {
                        if (field.getKey().equals("senderImageUrl")) {
                            gift.senderImageUrl = (String) field.getValue();
                        } else if (field.getKey().equals("senderName")) {
                            gift.senderName = (String) field.getValue();
                        } else if (field.getKey().equals("giftInfo")) {
                            Map<String, String> map = (Map<String, String>) field.getValue();
                            for (Map.Entry<String, String> giftField : map.entrySet()) {
                                if (giftField.getKey().equals("type")) {
                                    gift.type = giftField.getValue();
                                }
                            }
                        }
                    }
                    gift.id = giftData.getKey();
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
