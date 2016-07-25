package com.google.face2face.backend.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public List<String> intersection(List<String> list1, List<String> list2) {
        List<String> list = new ArrayList<String>();

        for (String t : list1) {
            if(list2.contains(t.trim())) {
                list.add(t);
            }
        }

        return list;
    }
}
