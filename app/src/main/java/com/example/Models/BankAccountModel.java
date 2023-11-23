package com.example.Models;

public class BankAccountModel {
    private String bankName, accountNumber, id;

    public BankAccountModel(String bankName, String accountNumber, String id) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.id = id;
    }

    public BankAccountModel() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

