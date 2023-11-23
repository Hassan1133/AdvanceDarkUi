package com.example.Models;

import java.io.Serializable;

public class Withdrawal implements Serializable {
    private String date, transactionId, userId, userName, status, bankName, accountNumber;
    private Double amount;

    public Withdrawal() {

    }

    public Withdrawal(String date, Double amount, String transactionId, String userId, String userName, String status) {
        this.date = date;
        this.amount = amount;
        this.transactionId = transactionId;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
    }

    public Withdrawal(String date, Double amount, String transactionId, String userId, String userName, String status, String bankName, String accountNumber) {
        this.date = date;
        this.amount = amount;
        this.transactionId = transactionId;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}

