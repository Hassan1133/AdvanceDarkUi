package com.example.Models;

public class FAQsModel {
    private String question, answer, key;

    public FAQsModel() {
    }

    public FAQsModel(String question, String answer, String key) {
        this.question = question;
        this.answer = answer;
        this.key = key;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

