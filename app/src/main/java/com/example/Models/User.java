package com.example.Models;

import java.io.Serializable;

public class User implements Serializable {
    private String name, email, address, refId, phone, uid, profile, userType;
    private boolean activePlan, freeze, block, login;

    public User() {
    }

    public User(String name, String email, String address, String refId, String phone, String uid, String userType, boolean activePlan, boolean freeze, boolean block) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.refId = refId;
        this.phone = phone;
        this.uid = uid;
        this.userType = userType;
        this.activePlan = activePlan;
        this.freeze = freeze;
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getRefId() {
        return refId;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid() {
        return uid;
    }

    public String getProfile() {
        return profile;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isActivePlan() {
        return activePlan;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public boolean isBlock() {
        return block;
    }

    public boolean isLogin() {
        return login;
    }
}

