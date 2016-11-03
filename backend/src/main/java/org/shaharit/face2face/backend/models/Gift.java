package org.shaharit.face2face.backend.models;

public class Gift {
    public final String id;
    public String eventTitle;
    public final String recipientUid;
    public final GiftSender giftSender;
    public String text;

    public Gift(String id, String text, String eventTitle, String recipientUid, GiftSender giftSender) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.recipientUid = recipientUid;
        this.text = text;
        this.giftSender = giftSender;
    }
}
