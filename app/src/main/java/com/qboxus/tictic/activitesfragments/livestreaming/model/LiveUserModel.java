package com.qboxus.tictic.activitesfragments.livestreaming.model;

import android.text.TextUtils;

import java.io.Serializable;

public class LiveUserModel implements Serializable {

    public String streamingId;
    public String userId;
    public String userName;
    public String userPicture;
    public String onlineType;
    public String description;
    public String secureCode;
    public String joinStreamPrice;
    public String userCoins;
    public String isVerified;
    public String duetConnectedUserId;
    public boolean dualStreaming;
    public boolean isStreamJoinAllow;

    public LiveUserModel() {
    }


    public String getDuetConnectedUserId() {
        if (duetConnectedUserId==null || TextUtils.isEmpty(duetConnectedUserId))
        {
            return "";
        }
        else
        {
            return duetConnectedUserId;
        }
    }

    public void setDuetConnectedUserId(String duetConnectedUserId) {
        this.duetConnectedUserId = duetConnectedUserId;
    }

    public boolean isStreamJoinAllow() {
        return isStreamJoinAllow;
    }

    public void setStreamJoinAllow(boolean streamJoinAllow) {
        isStreamJoinAllow = streamJoinAllow;
    }

    public boolean isDualStreaming() {
        return dualStreaming;
    }

    public void setDualStreaming(boolean dualStreaming) {
        this.dualStreaming = dualStreaming;
    }

    public String getJoinStreamPrice() {
        if (joinStreamPrice==null || TextUtils.isEmpty(joinStreamPrice))
        {
            return "";
        }
        else
        {
            return joinStreamPrice;
        }
    }

    public void setJoinStreamPrice(String joinStreamPrice) {
        this.joinStreamPrice = joinStreamPrice;
    }

    public String getUserCoins() {
        if (userCoins==null || TextUtils.isEmpty(userCoins))
        {
            return "";
        }
        else
        {
            return userCoins;
        }
    }

    public void setUserCoins(String userCoins) {
        this.userCoins = userCoins;
    }

    public String getStreamingId() {
        if (streamingId==null || TextUtils.isEmpty(streamingId))
        {
            return "";
        }
        else
        {
            return streamingId;
        }
    }

    public void setStreamingId(String streamingId) {
        this.streamingId = streamingId;
    }

    public String getUserId() {
        if (userId==null || TextUtils.isEmpty(userId))
        {
            return "";
        }
        else
        {
            return userId;
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        if (userName==null || TextUtils.isEmpty(userName))
        {
            return "";
        }
        else
        {
            return userName;
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPicture() {
        if (userPicture==null || TextUtils.isEmpty(userPicture))
        {
            return "";
        }
        else
        {
            return userPicture;
        }
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getOnlineType() {
        if (onlineType==null || TextUtils.isEmpty(onlineType))
        {
            return "";
        }
        else
        {
            return onlineType;
        }
    }

    public void setOnlineType(String onlineType) {
        this.onlineType = onlineType;
    }

    public String getDescription() {
        if (description==null || TextUtils.isEmpty(description))
        {
            return "";
        }
        else
        {
            return description;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecureCode() {
        if (secureCode==null || TextUtils.isEmpty(secureCode))
        {
            return "";
        }
        else
        {
            return secureCode;
        }
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsVerified() {
        if (isVerified==null || TextUtils.isEmpty(isVerified))
        {
            return "";
        }
        else
        {
            return isVerified;
        }
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

}
