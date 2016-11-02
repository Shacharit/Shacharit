package org.shaharit.face2face.backend.tasks;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.shaharit.face2face.backend.database.EventDb;
import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.database.fakes.InMemoryEventsDb;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.testhelpers.EventBuilder;
import org.shaharit.face2face.backend.testhelpers.ExtrasForRecipientMatcher;
import org.shaharit.face2face.backend.testhelpers.TestUtils;
import org.shaharit.face2face.backend.testhelpers.UserBuilder;

import java.util.Calendar;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.shaharit.face2face.backend.testhelpers.ExtrasForRecipientMatcher.hasExtrasForEventRecipient;

public class EventNotificationTaskTest {
    private final PushService stubbedPushService = TestUtils.stubbedPushServide();

    @Test
    public void notifiesUserForBuddyWithRelevantEventToday() throws Exception {
        // Setup a buddy
        final String eventSelfDefinition = "Christian";
        User user1 = new UserBuilder().withOtherDefinitions(eventSelfDefinition).build();
        User user2 = new UserBuilder().withMatchingPersonalityFor(user1).build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        new MatchingTask(userDb, stubbedPushService).execute();

        // Setup an event for christians
        EventDb eventsDb = new InMemoryEventsDb(new EventBuilder()
                .forSelfDefinitions(eventSelfDefinition)
                .atDate("25-Dec-2016")
                .build());

        //Run events task
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.DECEMBER, 25);
        new EventNotificationTask(userDb, eventsDb, new PushService(mockMessenger), calendar)
                .execute();

        verify(mockMessenger).sendMessage(eq(user2.regId), anyString(), anyString(),
                anyMap());
    }

    @Test
    public void doesNotNotifyUserIfNoBuddyWithRelevantEventToday() throws Exception {
        // Setup a buddy
        final String eventSelfDefinition = "Christian";
        User user1 = new UserBuilder().withOtherDefinitions(eventSelfDefinition).build();
        User user2 = new UserBuilder().withMatchingPersonalityFor(user1).build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        new MatchingTask(userDb, stubbedPushService).execute();

        // Setup an event for christians
        EventDb eventsDb = new InMemoryEventsDb(new EventBuilder()
                .forSelfDefinitions(eventSelfDefinition)
                .atDate("25-Dec-2016")
                .build());

        //Run events task
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.OCTOBER, 24);
        new EventNotificationTask(userDb, eventsDb, new PushService(mockMessenger), calendar)
                .execute();

        verify(mockMessenger,never()).sendMessage(eq(user2.regId), anyString(), anyString(),
                anyMap());
    }

    @Test
    public void onlyNotifiesBuddiesWithRelevantInterests() throws Exception {
        // Setup a buddy
        final String eventSelfDefinition = "Christian";
        User user = new UserBuilder().withOtherDefinitions("Liberal").build();
        User relevantBuddy = new UserBuilder().withSelfDefinitions(eventSelfDefinition)
                .withMatchingPersonalityFor(user).build();
        User irrelevantBuddy = new UserBuilder().withSelfDefinitions("Jew")
                .withMatchingPersonalityFor(user).build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user, relevantBuddy));

        new MatchingTask(userDb, stubbedPushService).execute();

        // Setup an event for christians
        EventDb eventsDb = new InMemoryEventsDb(new EventBuilder()
                .forSelfDefinitions(eventSelfDefinition)
                .atDate("25-Dec-2016")
                .build());

        //Run events task
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.DECEMBER, 25);
        new EventNotificationTask(userDb, eventsDb, new PushService(mockMessenger), calendar)
                .execute();

        verify(mockMessenger).sendMessage(eq(user.regId), anyString(), anyString(),
                argThat(hasExtrasForEventRecipient(relevantBuddy)));
        verify(mockMessenger,never()).sendMessage(eq(relevantBuddy.regId), anyString(), anyString(),
                argThat(hasExtrasForEventRecipient(irrelevantBuddy)));
    }
}