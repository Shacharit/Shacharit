package org.shaharit.face2face.backend.services;

import org.shaharit.face2face.backend.models.User;
import org.shaharit.face2face.backend.utils.ListUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matcher {

    private ListUtils listUtils = new ListUtils();

    public Map<User, Map<User, MatchSummary>> calculateScores(List<User> users) {
        Map<User, Map<User, MatchSummary>> res = new HashMap<>(users.size());

        // Initialize matchResult map
        for (User user : users) {
            res.put(user, new HashMap<User, MatchSummary>());
        }

        for (int i = 0; i < users.size(); i++){
            for (int j = i + 1; j < users.size(); j++) {
                User firstUser = users.get(i);
                User secondUser = users.get(j);
                List<String> intersection1 =
                        listUtils.intersection(firstUser.otherDefs, secondUser.selfDefs);
                List<String> intersection2 =
                        listUtils.intersection(firstUser.selfDefs, secondUser.otherDefs);

                if (intersection1.size() == 0 || intersection2.size() == 0) {
                    setMutualSummary(res,
                            new MatchSummary(MatchResult.NON_MATCHING_SELF_DEFINITION, null, null),
                            firstUser, secondUser);
                    continue;
                }

                if(!firstUser.gender.equals(secondUser.gender)) {
                    setMutualSummary(res,
                            new MatchSummary(MatchResult.NON_MATCHING_GENDER, null, null),
                            firstUser, secondUser);
                    continue;
                }

                double score = 0.0;
                List<String> sharedInterests = new ArrayList<>();
                List<String> nonSharedInterests = new ArrayList<>();
                for (String interestKey : firstUser.interests.keySet()) {
                    if (!secondUser.interests.containsKey(interestKey)) {
                        continue;
                    }

                    List<String> list1 = firstUser.interests.get(interestKey);
                    List<String> list2 = secondUser.interests.get(interestKey);
                    score += interestsScore(list1,
                            list2);
                    sharedInterests.addAll(listUtils.intersection(list1, list2));
                    nonSharedInterests.addAll(listUtils.difference(list2,list1));
                }

                if (score > 1) {
                    setMutualSummary(res,
                            new MatchSummary(MatchResult.MATCH, sharedInterests, nonSharedInterests),
                            firstUser, secondUser);
                }
                else {
                    setMutualSummary(res,
                            new MatchSummary(MatchResult.NON_MATCHING_INTERESTS, sharedInterests, nonSharedInterests),
                            firstUser, secondUser);
                }
            }
        }
        return res;
    }

    private void setMutualSummary(Map<User, Map<User, MatchSummary>> res, MatchSummary summary,
                                  User user1, User user2) {
        res.get(user1).put(user2, summary);
        res.get(user2).put(user1, summary);
    }


    private double interestsScore(java.util.List<String> list1, java.util.List<String> list2) {
        java.util.List<String> intersection = listUtils.intersection(list1, list2);

        // TODO: Document the weird ∑2^-i scores.
        double score = 0;
        for (int i = 0; i < intersection.size(); i++) {
            score += Math.pow(2, -i);
        }
        return score;
    }
}
