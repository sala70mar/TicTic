package com.qboxus.tictic.activitesfragments.spaces.models;

import com.qboxus.tictic.models.UserModel;

import java.io.Serializable;

public class UserSuggestionModel implements Serializable {
    UserModel userModel;

    public UserSuggestionModel() {
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
