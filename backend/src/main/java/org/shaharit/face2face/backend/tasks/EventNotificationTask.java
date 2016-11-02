package org.shaharit.face2face.backend.tasks;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.shaharit.face2face.backend.database.EventDb;
import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.models.Buddy;
import org.shaharit.face2face.backend.models.Event;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.PushService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class EventNotificationTask {
    private UserDb userDb;
    private final EventDb eventDb;
    private final PushService pushService;
    private final Calendar calendar;

    public EventNotificationTask(UserDb userDb, EventDb eventDb, PushService pushService, Calendar calendar) {
        this.userDb = userDb;
        this.eventDb = eventDb;
        this.pushService = pushService;
        this.calendar = calendar;
    }

    public void execute() {
        eventDb.getEvents(new EventDb.EventResultHandler() {
            @Override
            public void processResult(final List<Event> events) {
                ArrayList<Event> todayEvents =
                        Lists.newArrayList(Iterables.filter(events, new Predicate<Event>() {
                    @Override
                    public boolean apply(Event event) {
                        return event.occursAtDay(calendar.getTime());
                    }
                }));

                if (todayEvents.isEmpty()) {
                    return;
                }

                userDb.getUsers(new EventNotifierProcessor(todayEvents));
            }
        });
    }

    private class EventNotifierProcessor implements UserDb.UsersHandler {
        private List<Event> events;

        EventNotifierProcessor(List<Event> events) {
            this.events = events;
        }

        @Override
        public void processResult(List<User> users) {
            for (Event event : events) {
                for (User user : users) {
                    List<Buddy> buddies = user.getRelevantBuddiesForEvent(event);

                    // Send message to user about each buddy that is relevant
                    for (Buddy buddy : buddies) {
                        pushService.sendPushAboutBuddyEvent(user, buddy, event);
                    }
                }
            }
        }
    }
}
