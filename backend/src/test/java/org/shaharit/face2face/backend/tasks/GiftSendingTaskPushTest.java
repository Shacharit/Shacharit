package org.shaharit.face2face.backend.tasks;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.database.fakes.MockGiftDb;
import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.testhelpers.builders.GiftBuilder;
import org.shaharit.face2face.backend.testhelpers.builders.UserBuilder;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.shaharit.face2face.backend.testhelpers.ExtrasForPushAction.hasExtrasForPushAction;

public class GiftSendingTaskPushTest {
    @Test
    public void sendsCorrectTitleForMaleSender() throws Exception {
        User sender = new UserBuilder().withDisplayName("רפי").withMaleGender().build();
        User recipient = new UserBuilder().build();
        InMemoryUserDb userDb = new InMemoryUserDb(Lists.newArrayList(recipient, sender));

        Gift gift = new GiftBuilder(recipient.uid)
                .withSenderDetails(sender)
                .build();

        MockGiftDb mockGiftDb = new MockGiftDb(Lists.newArrayList(gift));

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new GiftSendingTask(mockGiftDb, userDb, new PushService(mockMessenger)).execute();

        verify(mockMessenger).sendMessage(eq(recipient.regId), eq("רפי חושב עליך"), anyString(),
                argThat(hasExtrasForPushAction("receive_gift")));

    }

    @Test
    public void sendsCorrectTitleForFemaleSender() throws Exception {
        User sender = new UserBuilder().withDisplayName("יפה").withFemaleGender().build();
        User recipient = new UserBuilder().build();
        InMemoryUserDb userDb = new InMemoryUserDb(Lists.newArrayList(recipient, sender));

        Gift gift = new GiftBuilder(recipient.uid)
                .withSenderDetails(sender)
                .build();

        MockGiftDb mockGiftDb = new MockGiftDb(Lists.newArrayList(gift));

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new GiftSendingTask(mockGiftDb, userDb, new PushService(mockMessenger)).execute();

        verify(mockMessenger).sendMessage(eq(recipient.regId), eq("יפה חושבת עליך"), anyString(),
                argThat(hasExtrasForPushAction("receive_gift")));

    }

    @Test
    public void sendsMessageBasedOnGiftText() throws Exception {
        User sender = new UserBuilder().build();
        User recipient = new UserBuilder().build();
        InMemoryUserDb userDb = new InMemoryUserDb(Lists.newArrayList(recipient, sender));

        String giftText = "Happy Easter";
        Gift gift = new GiftBuilder(recipient.uid)
                .withSenderDetails(sender)
                .withGiftText(giftText)
                .build();

        MockGiftDb mockGiftDb = new MockGiftDb(Lists.newArrayList(gift));

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new GiftSendingTask(mockGiftDb, userDb, new PushService(mockMessenger)).execute();

        verify(mockMessenger).sendMessage(eq(recipient.regId), anyString(), eq(giftText),
                argThat(hasExtrasForPushAction("receive_gift")));

    }

    @Test
    public void sendsRelevantSenderInformation() throws Exception {
        User sender = new UserBuilder().build();
        User recipient = new UserBuilder().build();
        InMemoryUserDb userDb = new InMemoryUserDb(Lists.newArrayList(recipient, sender));

        Gift gift = new GiftBuilder(recipient.uid)
                .withSenderDetails(sender)
                .build();

        MockGiftDb mockGiftDb = new MockGiftDb(Lists.newArrayList(gift));

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new GiftSendingTask(mockGiftDb, userDb, new PushService(mockMessenger)).execute();

        verify(mockMessenger).sendMessage(eq(recipient.regId), anyString(), anyString(),
                argThat(hasExtrasForGiftSender(sender.displayName, sender.imageUrl)));
    }

    @Test
    public void sendsRelevantEventInformation() throws Exception {
        User sender = new UserBuilder().build();
        User recipient = new UserBuilder().build();
        InMemoryUserDb userDb = new InMemoryUserDb(Lists.newArrayList(recipient, sender));

        String eventTitle = "The greatest event ever";
        Gift gift = new GiftBuilder(recipient.uid)
                .withEventTitle(eventTitle)
                .withSenderDetails(sender)
                .build();

        MockGiftDb mockGiftDb = new MockGiftDb(Lists.newArrayList(gift));

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new GiftSendingTask(mockGiftDb, userDb, new PushService(mockMessenger)).execute();

        verify(mockMessenger).sendMessage(eq(recipient.regId), anyString(), anyString(),
                argThat(hasExtrasForGiftEvent(eventTitle)));
    }

    private ExtrasForGiftEvent hasExtrasForGiftEvent(String title) {
        return new ExtrasForGiftEvent(title);
    }

    private class ExtrasForGiftEvent extends ArgumentMatcher<Map<String, String>> {
        private String title;

        ExtrasForGiftEvent(String title) {
            this.title = title;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(Object argument) {
            Map<String, String> extras = (Map<String, String>) argument;

            return extras.get("eventTitle").equals(title);
        }
    }

    private ExtrasForGiftSender hasExtrasForGiftSender(String displaName, String imageUrl) {
        return new ExtrasForGiftSender(displaName, imageUrl);
    }

    private class ExtrasForGiftSender extends ArgumentMatcher<Map<String, String>> {
        private final String displayName;
        private final String imageUrl;

        ExtrasForGiftSender(String displayName, String imageUrl) {

            this.displayName = displayName;
            this.imageUrl = imageUrl;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(Object argument) {
            Map<String, String> extras = (Map<String, String>) argument;

            return extras.get("senderName").equals(displayName) &&
                    extras.get("senderImageUrl").equals(imageUrl);
        }
    }
}
