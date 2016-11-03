package org.shaharit.face2face.backend;

import org.shaharit.face2face.backend.models.GiftSuggestion;

public class GiftEvent {
    public String name;
    public String maleText;
    public String femaleText;
    public GiftSuggestion[] gifts = new GiftSuggestion[3];
    public String description;

    public GiftEvent(String name) {
        this.name = name;
    }
}
