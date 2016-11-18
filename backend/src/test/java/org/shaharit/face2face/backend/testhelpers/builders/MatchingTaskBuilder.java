package org.shaharit.face2face.backend.testhelpers.builders;

import com.google.common.collect.Lists;

import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.services.MatchingLog;
import org.shaharit.face2face.backend.tasks.MatchingTask;
import org.shaharit.face2face.backend.testhelpers.TestUtils;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

public class MatchingTaskBuilder {
    private UserDb userDb = new InMemoryUserDb(new ArrayList<User>());
    private MatchingLog matchingLog = mock(MatchingLog.class);
    private PushService pushService = TestUtils.stubbedPushServide();

    public MatchingTaskBuilder withUserDb(UserDb userDb) {
        this.userDb = userDb;
        return this;
    }

    public MatchingTaskBuilder withMatchingLog(MatchingLog matchingLog) {
        this.matchingLog = matchingLog;
        return this;
    }

    public MatchingTask build() {
        return new MatchingTask(userDb, pushService ,matchingLog);
    }

    public MatchingTaskBuilder withPushService(PushService pushService) {
        this.pushService = pushService;
        return this;
    }
}
