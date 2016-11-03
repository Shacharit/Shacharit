package org.shaharit.face2face.backend.database.firebase;

import com.google.api.client.util.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.shaharit.face2face.backend.database.EventDb;
import org.shaharit.face2face.backend.models.Event;
import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.GenderCommunications;
import org.shaharit.face2face.backend.models.GiftSuggestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirebaseEventDb implements EventDb {
    private final DatabaseReference firebase;

    public FirebaseEventDb(DatabaseReference firebase) {
        this.firebase = firebase;
    }

    @Override
    public void getEvents(final EventResultHandler handler) {
        firebase.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> res = Lists.newArrayList();

                for (DataSnapshot event_ds : dataSnapshot.getChildren()) {
                    res.add(new Event(
                            event_ds.child("date").getValue().toString(),
                            eventDefinitionsFromDs(event_ds.child("event_definitions")),
                            titleMapFromDs(event_ds),
                            event_ds.child("description").getValue().toString(),
                            giftsFromSnapshot(event_ds)
                    ));
                }

                handler.processResult(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<GiftSuggestion> giftsFromSnapshot(DataSnapshot eventDs) {
        List<GiftSuggestion> res = Lists.newArrayList();

        for (int i = 0; i < 3; i++) {
            final DataSnapshot giftDs = eventDs.child("gift" + (i + 1));

            res.add(new GiftSuggestion(
                    greetingsFromGiftDs(giftDs),
                    ctaFromGiftDs(giftDs),
                    giftDs.child("url").getValue().toString(),
                    giftDs.child("type").getValue().toString()
            ));
        }

        return res;
    }

    private GenderCommunications genderCommunicationsFromGiftDs(String paramName,
                                                                DataSnapshot giftDs) {
        GenderCommunications res = new GenderCommunications();

        for (Gender gender1 : Gender.values()) {
            for (Gender gender2 : Gender.values()) {
                String childName = String.format("%s %s2%s", paramName,
                        gender1.toString().toLowerCase(),
                        gender2.toString().toLowerCase());

                if (giftDs.hasChild(childName)) {
                    res.addCommunication(gender1, gender2,
                            giftDs.child(childName).getValue().toString());
                }
            }
        }

        return res;
    }

    private GenderCommunications ctaFromGiftDs(DataSnapshot giftDs) {
        return genderCommunicationsFromGiftDs("cta", giftDs);
    }

    private GenderCommunications greetingsFromGiftDs(final DataSnapshot giftDs) {
        return genderCommunicationsFromGiftDs("greeting", giftDs);
    }

    private Map<Gender, String> titleMapFromDs(final DataSnapshot event_ds) {
        return new HashMap<Gender, String>() {{
            put(Gender.MALE, event_ds.child("title-male").getValue().toString());
            put(Gender.FEMALE, event_ds.child("title-female").getValue().toString());
        }};
    }

    private List<String> eventDefinitionsFromDs(DataSnapshot event_definitions) {
        List<String> eventDefinitions = Lists.newArrayList();

        for (DataSnapshot ds : event_definitions.getChildren()) {
            eventDefinitions.add(ds.getKey());
        }

        return eventDefinitions;
    }
}
