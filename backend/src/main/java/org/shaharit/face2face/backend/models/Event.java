package org.shaharit.face2face.backend.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Event {
    public String dateStr;
    public List<String> selfDefinitions;
    public Map<Gender, String> titleMap;
    public String description;
    public List<GiftSuggestion> gifts;

    public Event(String date, List<String> selfDefinitions, Map<Gender, String> titleMap,
                 String description, List<GiftSuggestion> gifts) {
        this.dateStr = date;
        this.selfDefinitions = selfDefinitions;
        this.titleMap = titleMap;
        this.description = description;
        this.gifts = gifts;
    }

    public boolean occursAtDay(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String dateStr = formatter.format(date);
        return this.dateStr.equals(dateStr);
    }
}
