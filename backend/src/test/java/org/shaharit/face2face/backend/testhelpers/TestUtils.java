package org.shaharit.face2face.backend.testhelpers;

import org.shaharit.face2face.backend.push.FcmMessenger;
import org.shaharit.face2face.backend.push.PushService;

import static org.mockito.Mockito.mock;

public class TestUtils {
    public static PushService stubbedPushServide() {
        return new PushService(mock(FcmMessenger.class));
    }
}
