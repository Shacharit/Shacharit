package org.shaharit.face2face.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalisky on 7/16/17.
 */

public class EventNotification {
    public String eventDescription;
    public String eventTitle;
    public String buddyImageUrl;
    public String buddyName;
    public String buddyId;
    public String buddyEmail;
    public String eventName;
    public List<Gift> giftSuggestions = new ArrayList<>();
}
