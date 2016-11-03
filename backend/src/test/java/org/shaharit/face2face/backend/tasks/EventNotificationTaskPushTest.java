package org.shaharit.face2face.backend.tasks;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.GenderCommunications;
import org.shaharit.face2face.backend.models.GiftSuggestion;
import org.shaharit.face2face.backend.database.EventDb;
import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.database.fakes.InMemoryEventsDb;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.models.Event;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.testhelpers.EventBuilder;
import org.shaharit.face2face.backend.testhelpers.TestUtils;
import org.shaharit.face2face.backend.testhelpers.UserBuilder;

import java.util.Calendar;
import java.util.Map;

import static org.mockito.AdditionalMatchers.and;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.shaharit.face2face.backend.testhelpers.ExtrasForPushAction.hasExtrasForPushAction;
import static org.shaharit.face2face.backend.testhelpers.ExtrasForRecipientMatcher.hasExtrasForEventRecipient;

public class EventNotificationTaskPushTest {
    @Test
    public void pushIsSentWithCorrectAction() throws Exception {
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        Event event = new EventBuilder().build();

        UserBuddyPair userBuddyPair = triggerEventNotification(mockMessenger, event);

        verify(mockMessenger).sendMessage(eq(userBuddyPair.theUser.regId), anyString(), anyString(),
                argThat(hasExtrasForPushAction("give_gift")));

    }

    @Test
    public void pushIsSentWithAllRelevantSenderData() throws Exception {
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        Event event = new EventBuilder().build();

        UserBuddyPair userBuddyPair = triggerEventNotification(mockMessenger, event);

        verify(mockMessenger).sendMessage(eq(userBuddyPair.theUser.regId), anyString(), anyString(),
                argThat(hasExtrasForEventSender(userBuddyPair.theUser)));

    }

    @Test
    public void pushIsSentWithAllRelevantRecipientData() throws Exception {
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        Event event = new EventBuilder().build();

        UserBuddyPair userBuddyPair = triggerEventNotification(mockMessenger, event);

        verify(mockMessenger).sendMessage(eq(userBuddyPair.theUser.regId), anyString(), anyString(),
                argThat(hasExtrasForEventRecipient(userBuddyPair.theBuddy)));

    }

    @Test
    public void pushIsSentWithAllRelevantEventDataForMale() throws Exception {
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        String description = "Holocaust day";
        Event event = new EventBuilder()
                .withDescription(description)
                .withMaleTitle("מציין את יום הזיכרון לשואה והגבורה")
                .build();

        UserBuddyPair userBuddyPair = triggerEventNotification(mockMessenger, event,
                new UserBuilder(), new UserBuilder().withDisplayName("רפי").withFemaleGender());

        String expectedEventTitle = "רפי מציין את יום הזיכרון לשואה והגבורה";
        verify(mockMessenger).sendMessage(eq(userBuddyPair.theUser.regId), eq("שלח לחבר מתנה"),
                eq(expectedEventTitle),
                argThat(hasExtrasWithEventInfo(expectedEventTitle, description)));
    }

    @Test
    public void pushIsSentWithAllRelevantGiftInformationForMale() throws Exception {
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        String url = "http://holocaust.org";
        String type = "greeting";
        Gender gend = Gender.MALE;
        Event event = new EventBuilder()
                .withGift(new GiftSuggestion(
                        new GenderCommunications().addCommunication(gend, gend,
                                "חושב עליך ואיך עובר עליך היום"),
                        new GenderCommunications()
                                .addCommunication(gend, gend, "כתוב שאתה חושב עליו"),
                        url,
                        type))
                .build();

        UserBuddyPair userBuddyPair = triggerEventNotification(mockMessenger, event,
                new UserBuilder().withDisplayName("רפי").withMaleGender(),
                new UserBuilder().withMaleGender());

        verify(mockMessenger).sendMessage(eq(userBuddyPair.theUser.regId), anyString(), anyString(),
                argThat(hasExtrasForGift("רפי חושב עליך ואיך עובר עליך היום", "כתוב שאתה חושב עליו",
                    url, type
                )));
    }

    @Test
    public void pushIsSentWithAllRelevantGiftInformationForFemale() throws Exception {
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        String url = "http://holocaust.org";
        String type = "video";
        Gender gender = Gender.FEMALE;
        Event event = new EventBuilder()
                .withGift(new GiftSuggestion(
                        new GenderCommunications().addCommunication(gender, gender,
                                "חושבת עלייך ואיך עובר עלייך היום"),
                        new GenderCommunications()
                                .addCommunication(gender, gender, "כתבי שאת חושבת עליו"),
                        url,
                        type))
                .build();

        UserBuddyPair userBuddyPair = triggerEventNotification(mockMessenger, event,
                new UserBuilder().withDisplayName("דנה").withFemaleGender(),
                new UserBuilder().withFemaleGender());

        verify(mockMessenger).sendMessage(eq(userBuddyPair.theUser.regId), anyString(), anyString(),
                argThat(hasExtrasForGift(
                        "דנה חושבת עלייך ואיך עובר עלייך היום",
                        "כתבי שאת חושבת עליו",
                        url, type
                )));
    }

    @Test
    public void pushIsSentWithAllRelevantEventDataForFemale() throws Exception {
        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        String description = "Holocaust day";
        Event event = new EventBuilder()
                .withDescription(description)
                .withFemaleTitle("מציינת את יום הזיכרון לשואה והגבורה")
                .build();

        UserBuddyPair userBuddyPair = triggerEventNotification(mockMessenger, event,
                new UserBuilder().withFemaleGender(),
                new UserBuilder().withDisplayName("דנה").withFemaleGender());

        String eventTitle = "דנה מציינת את יום הזיכרון לשואה והגבורה";
        verify(mockMessenger).sendMessage(eq(userBuddyPair.theUser.regId),
                eq("שלחי לחברה מתנה"),
                eq(eventTitle),
                argThat(hasExtrasWithEventInfo(eventTitle, description)));

    }

    private ExtrasForSenderMatcher hasExtrasForEventSender(User sender) {
        return new ExtrasForSenderMatcher(sender);
    }

    private ExtrasForEventMatcher hasExtrasWithEventInfo(String title, String description) {
        return new ExtrasForEventMatcher(title, description);
    }

    private ExtrasForGiftMatcher hasExtrasForGift(String greeting, String cta, String url,
                                                  String type) {
        return new ExtrasForGiftMatcher(greeting, cta, url, type);
    }

    private class ExtrasForSenderMatcher extends ArgumentMatcher<Map<String, String>> {
        private User user;

        ExtrasForSenderMatcher(User user) {
            this.user = user;
        }

        @Override
        public boolean matches(Object argument) {
            Map<String, String> extras = (Map<String, String>) argument;

            return extras.get("senderName").equals(user.displayName) &&
                    extras.get("senderId").equals(user.uid) &&
                    extras.get("senderImageUrl").equals(user.imageUrl) &&
                    extras.get("senderGender").equals(user.gender.toString());
        }
    }

    private class ExtrasForEventMatcher extends ArgumentMatcher<Map<String, String>> {
        private final String title;
        private final String description;

        ExtrasForEventMatcher(String title, String description) {
            this.title = title;
            this.description = description;
        }

        @Override
        public boolean matches(Object argument) {
            Map<String, String> extras = (Map<String, String>) argument;

            return extras.get("eventTitle").equals(title) &&
                    extras.get("eventDescription").equals(description);
        }
    }

    private class ExtrasForGiftMatcher extends ArgumentMatcher<Map<String, String>> {
        private final String greeting;
        private final String cta;
        private final String url;
        private final String type;

        ExtrasForGiftMatcher(String greeting, String cta, String url, String type) {
            this.greeting = greeting;
            this.cta = cta;
            this.url = url;
            this.type = type;
        }

        @Override
        public boolean matches(Object argument) {
            Map<String, String> extras = (Map<String, String>) argument;

            return extras.get("gift").equals(
                    String.format("cta:%s,url:%s,type:%s,text:%s", cta, url, type, greeting)
            );
        }
    }

    private UserBuddyPair triggerEventNotification(FcmMessenger mockMessenger, Event event) {
        return triggerEventNotification(mockMessenger, event, new UserBuilder(), new UserBuilder());
    }

    private UserBuddyPair triggerEventNotification(FcmMessenger mockMessenger, Event event,
                                                   UserBuilder userBuilder,
                                                   UserBuilder buddyBuilder) {
        // Setup a buddy
        User user1 = userBuilder.withOtherDefinitions(event.selfDefinitions.get(0)).build();

        User user2 = buddyBuilder.withMatchingPersonalityFor(user1).build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        new MatchingTask(userDb, TestUtils.stubbedPushServide()).execute();


        event.dateStr = "24-Dec-2016";
        EventDb eventsDb = new InMemoryEventsDb(event);

        //Run events task

        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.DECEMBER, 24);
        new EventNotificationTask(userDb, eventsDb, new PushService(mockMessenger), calendar)
                .execute();
        return new UserBuddyPair(user1, user2);
    }

    private class UserBuddyPair {
        final User theUser;
        final User theBuddy;

        UserBuddyPair(User theUser, User theBuddy) {

            this.theUser = theUser;
            this.theBuddy = theBuddy;
        }
    }
}
