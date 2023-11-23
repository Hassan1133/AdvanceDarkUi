package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.Models.User;

public class PrefManager {
    private static final String MY_PREFERENCE_NAME = "com.Shakehand";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_NAME = "userName";
    private static final String USER_PHONE = "userPhone";
    private static final String USER_IMAGE = "userImage";
    private static final String REF_ID = "refId";
    private static final String UID = "uid";
    private static final String USER_ADDRESS = "address";
    private static final String ACTIVE_PLAN = "activeplan";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private final Context context;

    public PrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, "");
    }

    public void setUserEmail(String email) {
        editor.putString(USER_EMAIL, email);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME, "");
    }

    public void setUserName(String name) {
        editor.putString(USER_NAME, name);
        editor.apply();
    }

    public String getRefId() {
        return sharedPreferences.getString(REF_ID, "");
    }

    public void setRefId(String name) {
        editor.putString(REF_ID, name);
        editor.apply();
    }

    public String getUid() {
        return sharedPreferences.getString(UID, "");
    }

    public void setUid(String name) {
        editor.putString(UID, name);
        editor.apply();
    }

    public String getUserPhone() {
        return sharedPreferences.getString(USER_PHONE, "");
    }

    public void setUserPhone(String name) {
        editor.putString(USER_PHONE, name);
        editor.apply();
    }

    public String getUserAddress() {
        return sharedPreferences.getString(USER_ADDRESS, "");
    }

    public void setUserAddress(String name) {
        editor.putString(USER_ADDRESS, name);
        editor.apply();
    }

    public boolean getActivePlan() {
        return sharedPreferences.getBoolean(ACTIVE_PLAN, false);
    }

    public void setActivePlan(boolean name) {
        editor.putBoolean(ACTIVE_PLAN, name);
        editor.apply();
    }

    // image
    public String getUserImage() {
        return sharedPreferences.getString(USER_IMAGE, "");
    }

    public void setUserImages(String name) {
        editor.putString(USER_IMAGE, name);
        editor.apply();
    }


    // set all details
    public void setUserDetails(User user) {
        try {
            editor.putString(USER_EMAIL, user.getEmail());
            editor.putString(USER_ADDRESS, user.getAddress());
            editor.putString(USER_NAME, user.getName());
            editor.putString(REF_ID, user.getRefId());
            editor.putString(UID, user.getUid());
            editor.putString(USER_PHONE, user.getPhone());
            editor.putBoolean(ACTIVE_PLAN, user.isActivePlan());
            editor.putString(USER_IMAGE, user.getProfile());
            editor.apply();
        } catch (Exception e) {
        }
    }

    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}

