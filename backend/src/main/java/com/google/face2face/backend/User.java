package com.google.face2face.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public String uid;
    public ArrayList<String> selfDefs;
    public ArrayList<String> otherDefs;
    public Map<String, List<String>> interests;
    public String regId;
    public String gender;
    public int age;
    public String imageUrl;

    public User(String uid) {
        this.uid = uid;
        this.selfDefs = new ArrayList<>();
        this.otherDefs = new ArrayList<>();
        this.interests = new HashMap<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", selfDefs=" + selfDefs +
                ", otherDefs=" + otherDefs +
                ", interests=" + interests +
                ", regId='" + regId + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
