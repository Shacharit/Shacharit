package org.shaharit.face2face.backend.tasks;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.testhelpers.builders.MatchingTaskBuilder;
import org.shaharit.face2face.backend.testhelpers.builders.UserBuilder;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MatchingTaskPushTest {
    @Test
    public void whenUserGetsMatchedWithUserHeGetsAPush() throws Exception {
        final User user1 = new UserBuilder().build();
        final User user2 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .build();

        FcmMessenger mockMessenger = mock(FcmMessenger.class);

        new MatchingTaskBuilder()
                .withUserDb(new InMemoryUserDb(Lists.newArrayList(user1, user2)))
                .withPushService(new PushService(mockMessenger))
                .build()
                .execute();

        // Assert both got message
        verify(mockMessenger).sendMessage(eq(user1.regId), anyString(), anyString(),
                argThat(isExtrasForNewBuddy(user2)));
        verify(mockMessenger).sendMessage(eq(user2.regId), anyString(), anyString(),
                argThat(isExtrasForNewBuddy(user1)));
    }

    @Test
    public void messageIsOnlySentForNewBuddies() throws Exception {
        final User user1 = new UserBuilder().build();
        final User user2 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .build();

        // First we make make a call with no assertion just to match the users
        InMemoryUserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));
        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        final User user3 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .build();
        FcmMessenger mockMessenger = mock(FcmMessenger.class);

        userDb.addUser(user3);

        new MatchingTaskBuilder()
                .withUserDb(userDb)
                .withPushService(new PushService(mockMessenger))
                .build()
                .execute();

        // Assert only got message for new user
        verify(mockMessenger).sendMessage(eq(user1.regId), anyString(), anyString(),
                argThat(isExtrasForNewBuddy(user3)));
        verify(mockMessenger, never()).sendMessage(eq(user1.regId), anyString(), anyString(),
                argThat(isExtrasForNewBuddy(user2)));
    }

    @Test
    public void messageIsSentWithAppropriateMessageForMale() throws Exception {
        final User user1 = new UserBuilder()
                .withMaleGender()
                .build();
        final User user2 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .build();

        FcmMessenger mockMessenger = mock(FcmMessenger.class);

        new MatchingTaskBuilder()
                .withUserDb(new InMemoryUserDb(Lists.newArrayList(user1, user2)))
                .withPushService(new PushService(mockMessenger))
                .build()
                .execute();

        verify(mockMessenger).sendMessage(eq(user1.regId), eq("חבר חדש"),
                eq("אמור לו שלום"),
                argThat(isExtrasForNewBuddy(user2)));
    }

    @Test
    public void messageIsSentWithAppropriateMessageForFemale() throws Exception {
        final User user1 = new UserBuilder()
                .withFemaleGender()
                .build();
        final User user2 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .build();

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new MatchingTaskBuilder()
                .withUserDb(new InMemoryUserDb(Lists.newArrayList(user1, user2)))
                .withPushService(new PushService(mockMessenger))
                .build()
                .execute();

        verify(mockMessenger).sendMessage(eq(user1.regId), eq("חברה חדשה"),
                eq("אמור לה שלום"),
                argThat(isExtrasForNewBuddy(user2)));
    }

    private NewBuddyExtrasMatcher isExtrasForNewBuddy(User user) {
        return new NewBuddyExtrasMatcher(user);
    }

    private class NewBuddyExtrasMatcher extends ArgumentMatcher<Map<String, String>> {
        private User user;

        NewBuddyExtrasMatcher(User user) {
            this.user = user;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(Object argument) {
            Map<String, String> extras = (Map<String, String>) argument;

            return extras.get("uid").equals(user.uid)
                    && extras.get("image_url").equals(user.imageUrl)
                    && extras.get("action").equals("match");
        }
    }
}
