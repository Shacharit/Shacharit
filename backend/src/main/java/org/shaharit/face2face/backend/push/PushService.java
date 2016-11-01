package org.shaharit.face2face.backend.push;

import org.shaharit.face2face.backend.models.Gender;
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
                // Use this when manually testing in production so that you don't send unwanted
                // messages
//                final String testerRegId = "eWJqEh7o964:APA91bFxKhKXdww7lFlhj70uOglS49wh-4KZZ8jSyCxuHSad41StstgLrt_JbdZQbv_l2u527sENWZtNiYPRryoA7drXNtm5aHOqIYmYHUqpEZqLnLxAElo-GGvwr4d0TLyUGomNIg5t";
//                if (!regid.equals(testerRegId)) {
//                    continue;
//                }

                messenger.sendMessage(regid, "new buddies", newBuddyMessage.get(newUser.gender),
                        extras);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
