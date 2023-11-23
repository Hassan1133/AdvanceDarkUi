package com.example.Models;

import java.io.Serializable;

public class PointsWithdrawModel implements Serializable {
    private String date, transactionId, uid, status, bankName, accountNumber;
    private Double amount, points;
    private Double teamPoints, userPoints;

    public PointsWithdrawModel() {
    }

    public PointsWithdrawModel(String date, String transactionId, String uid, String status, String bankName, String accountNumber, Double amount, Double points, Double teamPoints, Double userPoints) {
        this.date = date;
        this.transactionId = transactionId;
        this.uid = uid;
        this.status = status;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.points = points;
        this.teamPoints = teamPoints;
        this.userPoints = userPoints;
    }

    public String getDate() {
        return date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getPoints() {
        return points;
    }

    public Double getTeamPoints() {
        return teamPoints;
    }

    public Double getUserPoints() {
        return userPoints;
    }
}

