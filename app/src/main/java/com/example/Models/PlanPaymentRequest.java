package com.example.Models;

public class PlanPaymentRequest {
    private String title, description, speed, id, price, uid, image,key,status;
    private int amount;


    public PlanPaymentRequest() {
    }

    public PlanPaymentRequest(String title, String description, String speed, String id, String price, String uid, String image, String status, int amount,String key) {
        this.title = title;
        this.description = description;
        this.speed = speed;
        this.id = id;
        this.price = price;
        this.uid = uid;
        this.image = image;
        this.status = status;
        this.amount = amount;
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

    public String getUid() {
        return uid;
    }

    public int getAmount() {
        return amount;
    }

    public String getImage() {
        return image;
    }
}
