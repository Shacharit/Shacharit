package org.shaharit.face2face.backend.database.fakes;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import org.shaharit.face2face.backend.database.EventDb;
import org.shaharit.face2face.backend.models.Event;

import java.util.ArrayList;

public class InMemoryEventsDb implements EventDb {
    private final ArrayList<Event> events;

    public InMemoryEventsDb(Event event) {
        this.events = Lists.newArrayList(event);
    }

    @Override
    public void getEvents(EventResultHandler handler) {
        handler.processResult(events);
    }
}
