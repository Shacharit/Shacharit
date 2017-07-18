package org.shaharit.face2face.backend.models;

public class Gift {
    public final String id;
    public String eventName;
    public String eventTitle;
    public final String recipientUid;
    public final String senderUid;
    public String text;
    public String type;
    public String url;

    public Gift(String id, String eventName, String eventTitle, String recipientUid, String senderUid, String text, String type, String url) {
        this.id = id;
        this.eventName = eventName;
        this.eventTitle = eventTitle;
        this.recipientUid = recipientUid;
        this.senderUid = senderUid;
        this.text = text;
        this.type = type;
        this.url = url;
    }
}
