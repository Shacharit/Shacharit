package org.shaharit.face2face.backend.tasks;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import org.junit.Test;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.services.MatchResult;
import org.shaharit.face2face.backend.services.MatchSummary;
import org.shaharit.face2face.backend.services.fakes.InMemoryMatchingLog;
import org.shaharit.face2face.backend.testhelpers.builders.MatchingTaskBuilder;
import org.shaharit.face2face.backend.testhelpers.builders.UserBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MatchingTestLoggingTest {
    @Test
    public void givenSingleUserThenSummaryLogForUserIsEmpty() throws Exception {
        final User user = new UserBuilder().build();

        InMemoryMatchingLog matchingLog = new InMemoryMatchingLog();
        MatchingTask matchingTask = new MatchingTaskBuilder()
                .withUserDb(new InMemoryUserDb(Lists.newArrayList(user)))
                .withMatchingLog(matchingLog)
                .build();

        matchingTask.execute();

        assertThat(matchingLog.getSummaryLogForUser(user.uid).keySet(), is(empty()));
    }

    @Test
    public void givenUserWithNonMatchingOtherDefinitionThenSummarySaysFailDueToNonOther() throws Exception {
        final User user1 = new UserBuilder().withOtherDefinitions("def1").build();
        final User user2 = new UserBuilder().withSelfDefinitions("def2").build();


        final InMemoryMatchingLog matchingLog = new InMemoryMatchingLog();
        MatchingTask matchingTask = new MatchingTaskBuilder()
                .withUserDb(new InMemoryUserDb(Lists.newArrayList(user1, user2)))
                .withMatchingLog(matchingLog)
                .build();

        matchingTask.execute();

        final MatchSummary matchSummary =
                matchingLog.getSummaryLogForUser(user1.uid).get(user2.uid);

        assertThat(matchSummary.matchResult, equalTo((MatchResult.NON_MATCHING_SELF_DEFINITION)));
    }

    @Test
    public void givenUserWithNonMatchingGenderThenSummarySaysFailDueToGender() throws Exception {
        final User user1 = new UserBuilder().withMaleGender().build();
        final User user2 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .withFemaleGender()
                .build();


        final InMemoryMatchingLog matchingLog = new InMemoryMatchingLog();
        MatchingTask matchingTask = new MatchingTaskBuilder()
                .withUserDb(new InMemoryUserDb(Lists.newArrayList(user1, user2)))
                .withMatchingLog(matchingLog)
                .build();

        matchingTask.execute();

        final MatchSummary matchSummary =
                matchingLog.getSummaryLogForUser(user1.uid).get(user2.uid);

        assertThat(matchSummary.matchResult, equalTo((MatchResult.NON_MATCHING_GENDER)));
    }

    @Test
    public void givenUserWithNonMatchingInterestsThenSummarySaysFailDueToInterestsMisMatch() throws Exception {
        final User user1 = new UserBuilder().withNoHobbies()
                .withMovieInterests("mov1", "mov2")
                .build();

        final User user2 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .withNoMovieInterests()
                .build();


        final InMemoryMatchingLog matchingLog = new InMemoryMatchingLog();
        MatchingTask matchingTask = new MatchingTaskBuilder()
                .withUserDb(new InMemoryUserDb(Lists.newArrayList(user1, user2)))
                .withMatchingLog(matchingLog)
                .build();

        matchingTask.execute();

        final MatchSummary matchSummary =
                matchingLog.getSummaryLogForUser(user1.uid).get(user2.uid);

        assertThat(matchSummary.matchResult, equalTo((MatchResult.NON_MATCHING_INTERESTS)));
    }
}
