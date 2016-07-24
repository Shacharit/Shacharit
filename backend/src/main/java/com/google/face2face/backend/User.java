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

    public User(String uid) {
        this.uid = uid;
        this.selfDefs = new ArrayList<>();
        this.otherDefs = new ArrayList<>();
        this.interests = new HashMap<>();
    }
}
