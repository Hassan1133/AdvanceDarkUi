package com.example.Models;

public class ShakehandBitRate {
    private Double points, amount, zucobit;
    private String date, key;

    public ShakehandBitRate() {
    }

    public ShakehandBitRate(Double points, Double amount, Double zucobit, String date, String key) {
        this.points = points;
        this.amount = amount;
        this.zucobit = zucobit;
        this.date = date;
        this.key = key;
    }

    public Double getPoints() {
        return points;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getZucobit() {
        return zucobit;
    }

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }
}
