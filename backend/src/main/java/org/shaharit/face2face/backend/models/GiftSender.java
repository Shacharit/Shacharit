package org.shaharit.face2face.backend.models;

public class GiftSender {

    public String displayName;
    public String imageUrl;
    public final Gender gender;

    public GiftSender(String displayName, String imageUrl, Gender gender) {
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.gender = gender;
    }
}
