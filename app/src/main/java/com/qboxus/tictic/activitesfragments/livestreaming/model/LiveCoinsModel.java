package com.qboxus.tictic.activitesfragments.livestreaming.model;

import java.io.Serializable;

public class LiveCoinsModel implements Serializable {
    public String sendedCoins,userId,userName,userPic;

    public LiveCoinsModel() {
        sendedCoins="0";
        userId="";
        userName="";
        userPic="";
    }

    public String getSendedCoins() {
        return sendedCoins;
    }

    public void setSendedCoins(String sendedCoins) {
        this.sendedCoins = sendedCoins;
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

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
