package org.shaharit.face2face.backend.push;

import org.shaharit.face2face.backend.models.Buddy;
import org.shaharit.face2face.backend.models.Event;
import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.GenderCommunications;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.GiftSuggestion;
import org.shaharit.face2face.backend.models.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushService {
    private static final Map<Gender, String> newBuddyMessage = new HashMap<Gender, String>() {{
        put(Gender.MALE, "say hi to him");
        put(Gender.FEMALE, "say hi to her");
    }};

    private static final Map<Gender, String> gotGiftTitles = new HashMap<Gender, String>() {{
        put(Gender.MALE, "חושב עליך");
        put(Gender.FEMALE, "חושבת עליך");
    }};

    private static final GenderCommunications eventPushTitles = new GenderCommunications() {{
        addCommunication(Gender.MALE, Gender.MALE, "שלח לחבר מתנה");
        addCommunication(Gender.FEMALE, Gender.FEMALE, "שלחי לחברה מתנה");
    }};

    private FcmMessenger messenger;

    public PushService(FcmMessenger messenger) {
        this.messenger = messenger;
    }

    public void sendPushAboutNewBuddies(String regid, List<User> newBuddies) {
        Map<String, String> extras = new HashMap<>();
        for (User newUser : newBuddies) {
            extras.put("uid", newUser.uid);
            extras.put("image_url", newUser.imageUrl);
            extras.put("action", "match");
            try {
                if (shouldSkipSending(regid)) {
                    continue;
                }

                messenger.sendMessage(regid, "new buddies", newBuddyMessage.get(newUser.gender),
                        extras);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean shouldSkipSending(String regid) {
        boolean res = false;

        // Uncomment this when manually testing in production so that you don't send unwanted
        // messages
//        final String testerRegId = "eWJqEh7o964:APA91bFxKhKXdww7lFlhj70uOglS49wh-4KZZ8jSyCxuHSad41StstgLrt_JbdZQbv_l2u527sENWZtNiYPRryoA7drXNtm5aHOqIYmYHUqpEZqLnLxAElo-GGvwr4d0TLyUGomNIg5t";
//        res = !regid.equals(testerRegId);

        return res;
    }

    public void sendPushAboutBuddyEvent(User user, Buddy buddy, Event event) {
        Map<String, String> extras = new HashMap<>();

        extras.put("recipientId", buddy.uid);
        extras.put("recipientName", buddy.displayName);
        extras.put("recipientGender", buddy.gender.toString());
        extras.put("recipientImageUrl", buddy.imageUrl);
        extras.put("senderGender", user.gender.toString());
        extras.put("senderName", user.displayName);
        extras.put("senderId", user.uid);
        extras.put("senderImageUrl", user.imageUrl);
        String eventTitle = buddy.displayName + " " + event.titleMap.get(buddy.gender);
        extras.put("eventTitle", eventTitle);
        extras.put("eventDescription", event.description);
        extras.put("action", "give_gift");

        if (event.gifts.size() > 0) {
            extras.put("gift",
                    giftCsv(event.gifts.get(0), user.displayName, user.gender, user.gender));
        }


        try {
            if (shouldSkipSending(user.regId)) {
                return;
            }

            messenger.sendMessage(user.regId,
                    eventPushTitles.getCommunication(user.gender, buddy.gender), eventTitle, extras);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String giftCsv(GiftSuggestion gift, String senderName, Gender recipient, Gender sender) {
        return String.format("cta:%s,url:%s,type:%s,text:%s",
                gift.cta.getCommunication(sender, recipient),
                gift.url,
                gift.type,
                senderName + " " + gift.greeting.getCommunication(sender, recipient));
    }

    public void sendPushAboutGift(String regId, Gift gift) {
        HashMap<String, String> extras = new HashMap<>();
        extras.put("action", "receive_gift");

        // Sender information
        extras.put("senderName", gift.giftSender.displayName);
        extras.put("senderImageUrl", gift.giftSender.imageUrl);

        // Event info
        extras.put("eventTitle", gift.eventTitle);

        try {
            if (shouldSkipSending(regId)) {
                return;
            }

            messenger.sendMessage(regId,gotGiftTitles.get(gift.giftSender.gender),gift.text,
                    extras);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
