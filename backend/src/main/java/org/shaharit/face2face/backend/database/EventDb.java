package org.shaharit.face2face.backend.database;

import org.shaharit.face2face.backend.models.Event;

import java.util.List;

public interface EventDb {
    void getEvents(EventResultHandler handler);

    interface EventResultHandler extends ResultHandler<List<Event>> {}
}
