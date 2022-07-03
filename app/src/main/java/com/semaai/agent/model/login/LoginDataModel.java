package com.semaai.agent.model.login;

import java.io.Serializable;

public class LoginDataModel implements Serializable {

    String UserId;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
