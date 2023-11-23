package com.example.Models;

public class PointsModel {
    private String key, uid, time;
    // private Integer robotCount;
    private Double points;
    private boolean status;

    public PointsModel() {
    }

    public PointsModel(String key, String uid, String time, Double points, boolean status) {
        this.key = key;
        this.uid = uid;
        this.time = time;
        this.points = points;
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Double getPoints() {
        return points;
    }
}

