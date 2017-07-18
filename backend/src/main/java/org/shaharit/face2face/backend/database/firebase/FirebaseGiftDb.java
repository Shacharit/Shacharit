package org.shaharit.face2face.backend.database.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.backend.database.GiftDb;
import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.GiftSender;

import java.util.ArrayList;
import java.util.List;


public class FirebaseGiftDb implements GiftDb {
    private static final String SENT_GIFTS = "sent-gifts";
    private static final String SENT_FIELD = "isSent";
    private DatabaseReference firebase;

    public FirebaseGiftDb(DatabaseReference firebase) {
        this.firebase = firebase;
    }

    @Override
    public void getUnsentGifts(final GiftsHandler handler) {
        firebase.child(SENT_GIFTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Gift> res = new ArrayList<>();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                // Iterate over all gifts and extract unsent gifts
                for (DataSnapshot ds : children) {
                    if (ds.hasChild(SENT_FIELD) && Boolean.parseBoolean(ds.child(SENT_FIELD).getValue().toString())) {
                        continue;
                    }

                    res.add(giftFromDs(ds));
                }

                handler.processResult(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Gift giftFromDs(DataSnapshot ds) {
        return new Gift(
                ds.getKey(),
                ds.child("eventName").getValue().toString(),
                ds.child("eventTitle").getValue().toString(),
                ds.child("recipientId").getValue().toString(),
                ds.child("senderUid").getValue().toString(),
                ds.child("giftText").getValue().toString(),
                ds.child("type").getValue().toString(),
                ds.child("url").getValue().toString()
        );
    }

    @Override
    public void markGiftAsSent(String giftId) {
        firebase.child(SENT_GIFTS).child(giftId).child(SENT_FIELD).setValue(true);
    }
}
