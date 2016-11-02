package org.shaharit.face2face.backend.testhelpers;

import org.mockito.ArgumentMatcher;
import org.shaharit.face2face.backend.models.User;

import java.util.Map;

public class ExtrasForRecipientMatcher extends ArgumentMatcher<Map<String, String>> {
    private User user;

    private ExtrasForRecipientMatcher(User user) {
        this.user = user;
    }

    @Override
    public boolean matches(Object argument) {
        Map<String, String> extras = (Map<String, String>) argument;

        return extras.get("recipientName").equals(user.displayName) &&
                extras.get("recipientId").equals(user.uid) &&
                extras.get("recipientImageUrl").equals(user.imageUrl) &&
                extras.get("recipientGender").equals(user.gender.toString());
    }


    public static ExtrasForRecipientMatcher hasExtrasForEventRecipient(User recipient) {
        return new ExtrasForRecipientMatcher(recipient);
    }
}
