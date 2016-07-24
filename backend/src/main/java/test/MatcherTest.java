package test;
import com.google.common.collect.ImmutableList;
import com.google.face2face.backend.User;
import com.google.face2face.backend.services.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class MatcherTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMatchUsers() throws Exception {
        Matcher matcher = new Matcher();
        UserFactory userFactory = new UserFactory();
        User user1 = userFactory.createUser(
                ImmutableList.<String>of("movie1", "movie2", "movie3"),
                ImmutableList.<String>of("hobbie1", "hobbie2", "hobbie3"));
        User user2 = userFactory.createUser(
                ImmutableList.<String>of("movie1", "movie4", "movie3"),
                ImmutableList.<String>of("hobbie1", "hobbie4", "hobbie3"));
        User user3 = userFactory.createUser(
                ImmutableList.<String>of("movie1", "movie3", "movie5"),
                ImmutableList.<String>of("hobbie1", "hobbie3", "hobbie2"));

        double[][] scores = matcher.matchUsers(ImmutableList.of(user1, user2, user3));

//        assert(scores[0][1], equalTo(1.0));
    }

    @Test
    public void testGetMatchForUser() throws Exception {
        Matcher matcher = new Matcher();
        UserFactory userFactory = new UserFactory();
        User user1 = userFactory.createUser(
                ImmutableList.<String>of("movie1", "movie2", "movie3"),
                ImmutableList.<String>of("hobbie1", "hobbie2", "hobbie3"));
        User user2 = userFactory.createUser(
                ImmutableList.<String>of("movie1", "movie4", "movie3"),
                ImmutableList.<String>of("hobbie1", "hobbie4", "hobbie3"));
        User user3 = userFactory.createUser(
                ImmutableList.<String>of("movie1", "movie3", "movie5"),
                ImmutableList.<String>of("hobbie1", "hobbie3", "hobbie2"));

        double[][] scores = matcher.matchUsers(ImmutableList.of(user1, user2, user3));
        int matchForUser = matcher.getMatchForUser(0, scores);

        assertThat(matchForUser, equalTo(2));
    }
}