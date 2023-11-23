package com.example.Models;

public class ShareBalanceModel {
    private String sender,receiver,key,date;
    private double amount,totalAmount;

    public ShareBalanceModel(String sender, String receiver, String key, String date, double amount, double totalAmount) {
        this.sender = sender;
        this.receiver = receiver;
        this.key = key;
        this.date = date;
        this.amount = amount;
        this.totalAmount = totalAmount;
    }

    public ShareBalanceModel() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
