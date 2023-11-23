package com.example.Models;

import java.io.Serializable;

public class AreaModel implements Serializable {
    private String title, description, address;
    private Double lat, lng;

    public AreaModel() {
    }

    public AreaModel(String title, Double lat, Double lng) {
        this.title = title;
        this.lat = lat;
        this.lng = lng;


    }

    public AreaModel(String title, String description, String address, Double lat, Double lng) {

        this.title = title;
        this.description = description;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
