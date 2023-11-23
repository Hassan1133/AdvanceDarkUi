package com.example.Models;

import android.graphics.drawable.Drawable;

public class PaymentOptionsModel {
    private String id, name;
    private Drawable drawable;

    public PaymentOptionsModel(String id, String name, Drawable drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Drawable getDrawable() {
        return drawable;
    }
}

