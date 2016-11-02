package org.shaharit.face2face.backend.testhelpers;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import org.shaharit.face2face.backend.models.Gender;
import org.shaharit.face2face.backend.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBuilder {
    private static final String HOBBIES = "hobbies";
    private static final String MOVIES = "movies";
    private static int id = 0;

    // Some defaults
    private ArrayList<String> selfDefinitions = Lists.newArrayList("Jewish");
    private ArrayList<String> otherDefinitions = Lists.newArrayList("Muslim");
    private Map<String, List<String>> interests = new HashMap<String, List<String>>() {{
        put(MOVIES, Lists.newArrayList("movie1", "movie2", "movie3"));
        put(HOBBIES, Lists.newArrayList("hobby1", "hobby2", "hobby3"));
    }};
    private Gender gender = Gender.MALE;
    private String displayName;

    public User build() {
        User user = new User(Integer.toString(++id));

        // Just set to some number that is different than id
        user.regId = Integer.toString(1000 + id);
        user.selfDefs = selfDefinitions;
        user.otherDefs = otherDefinitions;
        user.interests = interests;
        user.gender = gender;
        user.imageUrl = "http://" + user.uid + ".jpg";
        user.displayName = displayName == null ? "user" + user.uid : displayName;
        return user;
    }

    public UserBuilder withSelfDefinitions(String... selfDefinitions) {
        this.selfDefinitions = Lists.newArrayList(selfDefinitions);
        return this;
    }

    public UserBuilder withOtherDefinitions(String... otherDefinitions) {
        this.otherDefinitions = Lists.newArrayList(otherDefinitions);
        return this;
    }

    public UserBuilder withMovieInterests(String... movies) {
        interests.put(MOVIES, Lists.newArrayList(movies));
        return this;
    }

    public UserBuilder withHobbies(String... hobbies) {
        interests.put(HOBBIES, Lists.newArrayList(hobbies));
        return this;
    }

    public UserBuilder withOppositeGenderOf(Gender gender) {
        this.gender = gender.equals(Gender.MALE) ? Gender.FEMALE : Gender.MALE;
        return this;
    }

    public UserBuilder withMatchingPersonalityFor(User otherUser) {
        selfDefinitions.addAll(otherUser.otherDefs);
        otherDefinitions.addAll(otherUser.selfDefs);
        interests.putAll(otherUser.interests);
        gender = otherUser.gender;

        return this;
    }


    public UserBuilder withNoHobbies() {
        interests.remove(HOBBIES);
        return this;
    }

    public UserBuilder withNoMovieInterests() {
        interests.remove(MOVIES);
        return this;
    }

    public UserBuilder withMaleGender() {
        gender = Gender.MALE;
        return this;
    }

    public UserBuilder withFemaleGender() {
        gender = Gender.FEMALE;
        return this;
    }

    public UserBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
}
