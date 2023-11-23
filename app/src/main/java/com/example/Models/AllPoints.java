package com.example.Models;

public class AllPoints {
    private Double teamPoints, userPoints;
    private String uid;

    public AllPoints(Double teamPoints, Double userPoints, String uid) {
        this.teamPoints = teamPoints;
        this.userPoints = userPoints;
        this.uid = uid;
    }

    public AllPoints() {
    }

    public Double getTeamPoints() {
        return teamPoints;
    }

    public Double getUserPoints() {
        return userPoints;
    }

    public String getUid() {
        return uid;
    }
}

