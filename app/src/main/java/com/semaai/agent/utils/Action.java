package com.semaai.agent.utils;

public class Action {
    private int mAction;
    String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Action() {

    }

    public int getValue() {
        return mAction;
    }
}