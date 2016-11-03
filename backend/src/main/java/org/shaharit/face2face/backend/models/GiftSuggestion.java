package org.shaharit.face2face.backend.models;

public class GiftSuggestion {
    public GenderCommunications greeting;
    public String url;
    public GenderCommunications cta;
    public String type;

    public GiftSuggestion() {
    }

    public GiftSuggestion(GenderCommunications greeting, GenderCommunications cta, String url, String type) {
        this.greeting = greeting;
        this.url = url;
        this.cta = cta;
        this.type = type;
    }
}
