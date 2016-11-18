package org.shaharit.face2face.backend.testhelpers.builders;

import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.GiftSender;

public class GiftBuilder {

    private final String recipientUid;
    private String giftText = "Happy";
    private Gender senderGender = Gender.FEMALE;
    private String name;
    private String imageUrl;
    private String eventTitle;

    public GiftBuilder(String uid) {
        this.recipientUid = uid;
    }

    public Gift build() {
        return new Gift("1", giftText, eventTitle , recipientUid,
                new GiftSender(name, imageUrl, senderGender));
    }

    public GiftBuilder withSenderGender(Gender gender) {
        this.senderGender = gender;
        return this;
    }

    public GiftBuilder withSenderDetails(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        return this;
    }

    public GiftBuilder withGiftText(String giftText) {
        this.giftText = giftText;
        return this;
    }

    public GiftBuilder withEventTitle(String title) {
        this.eventTitle = title;
        return this;
    }

    public GiftBuilder withSenderName(String displayName) {
        this.name = displayName;
        return this;
    }
}
