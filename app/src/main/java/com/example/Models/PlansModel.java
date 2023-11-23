package com.example.Models;

import java.io.Serializable;

public class PlansModel implements Serializable {
    private String title, description, speed, id, price;
    private boolean status;

    public PlansModel(String title, String description, String speed, String id, String price) {
        this.title = title;
        this.description = description;
        this.speed = speed;
        this.id = id;
        this.price = price;
    }

    public PlansModel() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSpeed() {
        return speed;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public boolean isStatus() {
        return status;
    }

}
