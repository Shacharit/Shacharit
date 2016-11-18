package org.shaharit.face2face.backend.tasks;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import org.junit.Test;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.database.fakes.MockGiftDb;
import org.shaharit.face2face.backend.models.Gift;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.testhelpers.builders.GiftBuilder;
import org.shaharit.face2face.backend.testhelpers.builders.UserBuilder;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.shaharit.face2face.backend.testhelpers.ExtrasForPushAction.hasExtrasForPushAction;

public class GiftSendingTaskTest {

    @Test
    public void sendsUnsentGiftForExistingRecipient() throws Exception {
        User recipient = new UserBuilder().build();
        InMemoryUserDb userDb = new InMemoryUserDb(Lists.newArrayList(recipient));

        Gift gift = new GiftBuilder(recipient.uid).build();
        MockGiftDb mockGiftDb = new MockGiftDb(Lists.newArrayList(gift));

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new GiftSendingTask(mockGiftDb, userDb, new PushService(mockMessenger)).execute();

        assertTrue("Gift was not marked as sent", mockGiftDb.wasGiftMarkedAsSent(gift.id));
        verify(mockMessenger).sendMessage(eq(recipient.regId), anyString(), anyString(),
                argThat(hasExtrasForPushAction("receive_gift")));
    }

    @Test
    public void doesNotSendUnsentGiftForNotFoundRecipient() throws Exception {
        User recipient = new UserBuilder().build();

        // Let's say the user does not exist in our DB
        InMemoryUserDb userDb = new InMemoryUserDb(new ArrayList<User>());

        Gift gift = new GiftBuilder(recipient.uid).build();
        MockGiftDb mockGiftDb = new MockGiftDb(Lists.newArrayList(gift));

        FcmMessenger mockMessenger = mock(FcmMessenger.class);
        new GiftSendingTask(mockGiftDb, userDb, new PushService(mockMessenger)).execute();

        assertTrue("Gift was not marked as sent", mockGiftDb.wasGiftMarkedAsSent(gift.id));
        verify(mockMessenger, never()).sendMessage(eq(recipient.regId), anyString(), anyString(),
                argThat(hasExtrasForPushAction("receive_gift")));
    }
}