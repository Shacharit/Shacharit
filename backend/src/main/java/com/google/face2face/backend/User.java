package com.google.face2face.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    public String uid;
    public ArrayList<String> selfDefs;
    public ArrayList<String> otherDefs;
    public HashMap<String, List<String>> interests;
}
