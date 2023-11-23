package com.example.Models;

public class Notifications {
    private String title, description, key, date;

    public Notifications(String title, String description, String key, String date) {
        this.title = title;
        this.description = description;
        this.key = key;
        this.date = date;
    }

    public Notifications() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }
}
