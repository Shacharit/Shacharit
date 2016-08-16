package org.shaharit.face2face.backend.utils;

import org.shaharit.face2face.backend.User;
import org.shaharit.face2face.backend.UserBasicInfo;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public List<String> intersection(List<String> list1, List<String> list2) {
        List<String> list = new ArrayList<String>();

        for (String t : list1) {
            if (list2.contains(t.trim())) {
                list.add(t);
            }
        }

        return list;
    }

    public List<User> nameDisjunction(List<UserBasicInfo> buddiesInDb,
                                               List<User> buddiesNew) {

        List<User> toReturn = new ArrayList(buddiesNew);

        for (User userNew : buddiesNew) {
            if (buddiesInDb == null) continue;

            for (UserBasicInfo userInDb : buddiesInDb) {
                if (userNew.uid.equals(userInDb.uid)) {
                    toReturn.remove(userNew);
                    continue;
                }
            }
        }
        return toReturn;
    }
}
