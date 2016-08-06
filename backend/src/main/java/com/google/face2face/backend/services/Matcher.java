package com.google.face2face.backend.services;

import com.google.face2face.backend.User;
import com.google.face2face.backend.utils.ListUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Matcher {

    private ListUtils listUtils = new ListUtils();

    public double[][] calculateScores(List<User> users) {
        double[][] scoreMatrix = new double[users.size()][users.size()];

        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                User firstUser = users.get(i);
                User secondUser = users.get(j);
                List<String> intersection = listUtils.intersection(firstUser.otherDefs, secondUser.selfDefs);

                if (intersection.size() == 0) continue;
                // TODO: Decide on whether other-gendered users can match male/female-gendered users
                //       and if so change the following line.
                if(!firstUser.gender.equalsIgnoreCase(secondUser.gender)) continue;

                double score = 0.0;
                for (String interestKey : firstUser.interests.keySet()) {
                    if (!secondUser.interests.containsKey(interestKey)) {
                        continue;
                    }

                    score += interestsScore(firstUser.interests.get(interestKey),
                            secondUser.interests.get(interestKey));
                }

                scoreMatrix[i][j] = score;
                scoreMatrix[j][i] = score;
            }
        }
        return scoreMatrix;
    }

    private double interestsScore(java.util.List<String> list1, java.util.List<String> list2) {
        java.util.List<String> intersection = listUtils.intersection(list1, list2);

        double score = 0;
        for (int i = 0; i < intersection.size(); i++) {
            score += Math.pow(2, -i);
        }
        return score;
    }

    public int getMatchForUser(int userIndex, double[][] scoreMatrix) {
        double maxMatch = -1;
        int userWithMaxMatch = -1;
        for (int i = 0; i < scoreMatrix.length; i++) {
            if (i == userIndex) continue;
            double currentValue = scoreMatrix[userIndex][i];
            if (currentValue > maxMatch && currentValue > 2) {
                maxMatch = currentValue;
                userWithMaxMatch = i;
            }
        }
        return userWithMaxMatch;
    }

    public List<Integer> getMatchesForUser(int userIndex, double[][] scoreMatrix) {
        List<Integer> matchedUsersIndices = new ArrayList<>();
        for (int i = 0; i < scoreMatrix.length; i++) {
            if (i == userIndex) continue;
            double currentValue = scoreMatrix[userIndex][i];
            if (currentValue > 2) {
                matchedUsersIndices.add(i);
            }
        }
        return matchedUsersIndices;
    }
}
