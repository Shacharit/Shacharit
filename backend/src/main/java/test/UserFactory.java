package test;

import org.shaharit.face2face.backend.User;

import java.util.List;

public class UserFactory {


    public User createUser(List<String> movies, List<String> hobbies) {
        User user = new User("1");
        user.interests.put("movies", movies);
        user.interests.put("hobbies", hobbies);
        return user;
    }
}


