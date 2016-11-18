package org.shaharit.face2face.backend.tasks;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.shaharit.face2face.backend.models.Buddy;
import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.database.UserDb;
import org.shaharit.face2face.backend.database.fakes.InMemoryUserDb;
import org.shaharit.face2face.backend.push.PushService;
import org.shaharit.face2face.backend.testhelpers.TestUtils;
import org.shaharit.face2face.backend.testhelpers.builders.MatchingTaskBuilder;
import org.shaharit.face2face.backend.testhelpers.builders.UserBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class MatchingTaskTest {
    private final PushService stubbedPushService = TestUtils.stubbedPushServide();

    @Test
    public void emptyDb() throws Exception {
        UserDb userDb = new InMemoryUserDb(new ArrayList<User>());

        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(users, is(empty()));
            }
        });
    }

    @Test
    public void whenNoMatchingInterestsNoBuddiesFound() throws Exception {
        final User user1 = new UserBuilder()
                .withSelfDefinitions("Jewish")
                .withOtherDefinitions("Muslim")
                .withMovieInterests("movie1")
                .withHobbies("hobby1")
                .build();

        final User user2 = new UserBuilder()
                .withSelfDefinitions("Jewish")
                .withOtherDefinitions("Muslim")
                .withMovieInterests("movie2")
                .withHobbies("hobby2")
                .build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));
        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(getUserById(users, user1.uid).buddies, is(empty()));
                assertThat(getUserById(users, user2.uid).buddies, is(empty()));
            }
        });
    }

    @Test
    public void sameInterestsAndMatchingOtherDefinitionCausesBuddyMatch() throws Exception {
        final User user1 = new UserBuilder()
                .withSelfDefinitions("Jewish")
                .withOtherDefinitions("Muslim")
                .withMovieInterests("movie1", "movie2", "movie3")
                .withHobbies("hobby1", "hobby2", "hobby3")
                .build();

        final User user2 = new UserBuilder()
                .withSelfDefinitions("Muslim")
                .withOtherDefinitions("Jewish")
                .withMovieInterests("movie1", "movie2", "movie3")
                .withHobbies("hobby1", "hobby2", "hobby3")
                .build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(getUserById(users, user1.uid).buddies, hasItem(userWithId(user2)));
                assertThat(getUserById(users, user2.uid).buddies, hasItem(userWithId(user1)));
            }
        });
    }

    @Test
    public void TwoInterestShouldBeEnoughForAMatch() throws Exception {
        final User user1 = new UserBuilder()
                .withSelfDefinitions("Jewish")
                .withOtherDefinitions("Muslim")
                .withMovieInterests("movie1", "movie42", "movie43")
                .withHobbies("hobby1", "hobby8", "hobby9")
                .build();

        final User user2 = new UserBuilder()
                .withSelfDefinitions("Muslim")
                .withOtherDefinitions("Jewish")
                .withMovieInterests("movie1", "movie2", "movie3")
                .withHobbies("hobby1", "hobby2", "hobby3")
                .build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(getUserById(users, user1.uid).buddies, hasItem(userWithId(user2)));
                assertThat(getUserById(users, user2.uid).buddies, hasItem(userWithId(user1)));
            }
        });
    }

    @Test
    public void givenAlreadyMatchingUsersWhenTaskRunsTheyRemainBuddies() throws Exception {
        final User user1 = new UserBuilder().build();

        final User user2 = new UserBuilder().withMatchingPersonalityFor(user1).build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        MatchingTaskBuilder matchingTaskBuilder = new MatchingTaskBuilder().withUserDb(userDb);
        matchingTaskBuilder.build().execute();

        // At this point they should already be matched... We run again to see they still do
        matchingTaskBuilder.build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(getUserById(users, user1.uid).buddies, hasItem(userWithId(user2)));
                assertThat(getUserById(users, user2.uid).buddies, hasItem(userWithId(user1)));
            }
        });
    }

    @Test
    public void usersWithSameInterestsButDifferentSexAreNotMatched() throws Exception {
        final User user1 = new UserBuilder().build();
        final User user2 = new UserBuilder()
                .withMatchingPersonalityFor(user1)
                .withOppositeGenderOf(user1.gender)
                .build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(getUserById(users, user1.uid).buddies, not(hasItem(userWithId(user2))));
                assertThat(getUserById(users, user2.uid).buddies, not(hasItem(userWithId(user1))));
            }
        });
    }

    @Test
    public void userWithSameInterestValueOnOtherCategoryIsNotMatched() throws Exception {
        final User user1 = new UserBuilder()
                .withSelfDefinitions("Jewish")
                .withOtherDefinitions("Muslim")
                .withHobbies("hobby1", "hobby2", "hobby3")
                .withNoMovieInterests()
                .build();

        final User user2 = new UserBuilder()
                .withSelfDefinitions("Muslim")
                .withOtherDefinitions("Jewish")
                .withMovieInterests("hobby1", "hobby2", "hobby3")
                .withNoHobbies()
                .build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user1, user2));

        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(getUserById(users, user1.uid).buddies, not(hasItem(userWithId(user2))));
                assertThat(getUserById(users, user2.uid).buddies, not(hasItem(userWithId(user1))));
            }
        });
    }

    @Test
    public void userIsNotMatchedWithHimself() throws Exception {
        final User user = new UserBuilder().build();

        UserDb userDb = new InMemoryUserDb(Lists.newArrayList(user));

        new MatchingTaskBuilder().withUserDb(userDb).build().execute();

        userDb.getUsers(new UserDb.UsersHandler() {
            @Override
            public void processResult(List<User> users) {
                assertThat(getUserById(users, user.uid).buddies, not(hasItem(userWithId(user))));
            }
        });
    }



    private UserIdMatcher userWithId(User user2) {
        return new UserIdMatcher(user2.uid);
    }


    private User getUserById(List<User> users, final String uid) {
        return Iterables.filter(users, new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.uid.equals(uid);
            }
        }).iterator().next();
    }

    private class UserIdMatcher extends TypeSafeMatcher<Buddy> {
        private String uid;

        UserIdMatcher(String uid) {
            this.uid = uid;
        }

        @Override
        protected boolean matchesSafely(Buddy item) {
            return item.uid.equals(uid);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(matchDescription(uid));
        }

        private String matchDescription(String uid) {
            return "a user with id " + uid;
        }

        @Override
        protected void describeMismatchSafely(Buddy item, Description mismatchDescription) {
            mismatchDescription.appendText(matchDescription(item.uid));
        }
    }
}