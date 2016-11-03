package org.shaharit.face2face.backend.testhelpers;

import com.google.common.collect.Lists;

import org.shaharit.face2face.backend.models.GiftSuggestion;
import org.shaharit.face2face.backend.models.Event;
import org.shaharit.face2face.backend.models.Gender;

import java.util.ArrayList;
import java.util.HashMap;

public class EventBuilder {

    private final HashMap<Gender, String> titleMap = new HashMap<Gender, String>() {{
        put(Gender.MALE, "he is celebrating");
    }};
    private final ArrayList<GiftSuggestion> gifts = Lists.newArrayList();
    private String dateStr = "24-Dec-2016";
    private ArrayList<String> selfDefinitions = Lists.newArrayList("Def");
    private String description = "some desc";

    public Event build() {
        return new Event(dateStr, selfDefinitions, titleMap, description, gifts);
    }

    public EventBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public EventBuilder withMaleTitle(String maleTitle) {
        this.titleMap.put(Gender.MALE, maleTitle);
        return this;
    }

    public EventBuilder withFemaleTitle(String femaleTitle) {
        this.titleMap.put(Gender.FEMALE, femaleTitle);
        return this;
    }

    public EventBuilder forSelfDefinitions(String... eventSelfDefinition) {
        this.selfDefinitions = Lists.newArrayList(eventSelfDefinition);
        return this;
    }

    public EventBuilder withGift(GiftSuggestion gift) {
        gifts.add(gift);
        return this;
    }

    public EventBuilder atDate(String datStr) {
        this.dateStr = datStr;
        return this;
    }
}
