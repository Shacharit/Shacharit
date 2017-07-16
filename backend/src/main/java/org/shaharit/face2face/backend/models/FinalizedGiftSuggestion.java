package org.shaharit.face2face.backend.models;

class FinalizedGiftSuggestion {
    public String cta;
    public String url;
    public String type;
    public String greeting;

    public FinalizedGiftSuggestion() {
    }

    public FinalizedGiftSuggestion(String cta, String url, String type, String greeting) {
        this.cta = cta;
        this.url = url;
        this.type = type;
        this.greeting = greeting;
    }
}
