package com.example.Models;

public class TimeModel {
    private String timestamp;
    private boolean status;

    public TimeModel() {
    }

    public TimeModel(String timestamp, boolean status) {
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isStatus() {
        return status;
    }
}
