package org.shaharit.face2face.backend.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventNotification {

    public String eventDescription;
    public String eventTitle;
    public String buddyImageUrl;
    public String buddyName;
    public String buddyId;
    public String buddyEmail;
    public String eventName;
    public List<FinalizedGiftSuggestion> giftSuggestions = new ArrayList<>();

    public EventNotification() {
    }

    public EventNotification(Event event, User user, Buddy buddy) {
        this.buddyId = buddy.uid;
        this.buddyName = buddy.displayName;
        this.buddyImageUrl = buddy.imageUrl;
        this.eventTitle = event.titleMap.get(buddy.gender) + " " + buddy.displayName;
        this.eventDescription = event.description;
        this.giftSuggestions = fromGifts(event.gifts, user, buddy);
        this.buddyEmail = buddy.email;
        this.eventName = event.name;
    }

    private List<FinalizedGiftSuggestion> fromGifts(
            List<GiftSuggestion> gifts,
            User user,
            Buddy buddy) {
        ArrayList<FinalizedGiftSuggestion> res = new ArrayList<>(gifts.size());

        for (GiftSuggestion gift : gifts) {
            res.add(new FinalizedGiftSuggestion(
                    gift.cta.getCommunication(user.gender, buddy.gender),
                    gift.url,
                    gift.type,
                    gift.greeting.getCommunication(user.gender, buddy.gender)
            ));
        }

        return res;
    }
}
