package com.qboxus.tictic.activitesfragments.spaces.models;

import com.qboxus.tictic.models.UserModel;

import java.io.Serializable;

public class HomeUserModel implements Serializable {
    public UserModel userModel;
    public String userRoleType,mice,riseHand,online;

    public HomeUserModel() {
        mice="0";
        riseHand="0";
        online="0";
    }

    public String getRiseHand() {
        return riseHand;
    }

    public void setRiseHand(String riseHand) {
        this.riseHand = riseHand;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getUserRoleType() {
        return userRoleType;
    }

    public String getMice() {
        return mice;
    }

    public void setMice(String mice) {
        this.mice = mice;
    }

    public void setUserRoleType(String userRoleType) {
        this.userRoleType = userRoleType;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
