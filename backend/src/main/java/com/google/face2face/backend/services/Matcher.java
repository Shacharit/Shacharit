package com.google.face2face.backend.services;

import com.google.face2face.backend.User;
import com.google.face2face.backend.utils.ListUtils;


import java.util.HashMap;
import java.util.List;

public class Matcher {

    private ListUtils listUtils = new ListUtils();

    public double[][] matchUsers(List<User> users) {
        double[][] scoreMatrix = new double[users.size()][users.size()];

        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                double score = interestsScore(users.get(i).interests.get("movies"), users.get(j).interests.get
                        ("movies"));
                score += interestsScore(users.get(i).interests.get("hobbies"), users.get(j).interests.get("hobbies"));
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
        int userWithMaxMatch = 0;
        for (int i = 0; i < scoreMatrix.length; i++) {
            if (i == userIndex) continue;
            double v = scoreMatrix[i][userIndex];
            if (v > maxMatch && v > 2) {
                maxMatch = v;
                userWithMaxMatch = i;
            }
        }
        return userWithMaxMatch;
    }

}
