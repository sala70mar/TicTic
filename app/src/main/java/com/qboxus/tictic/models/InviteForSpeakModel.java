package com.qboxus.tictic.models;

import java.io.Serializable;

public class InviteForSpeakModel implements Serializable {
    public String invite,userId,userName;

    public InviteForSpeakModel() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
