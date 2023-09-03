package com.qboxus.tictic.activitesfragments.livestreaming.model;

import java.io.Serializable;

public class StreamingUserModel implements Serializable {

    String id,userId,name,picture,diamond;
    boolean isMonthlySender,isMonthlyViewer,isDailySender,isBlock,isInvite;

    public StreamingUserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isInvite() {
        return isInvite;
    }

    public void setInvite(boolean invite) {
        isInvite = invite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDiamond() {
        return diamond;
    }

    public void setDiamond(String diamond) {
        this.diamond = diamond;
    }

    public boolean isMonthlySender() {
        return isMonthlySender;
    }

    public void setMonthlySender(boolean monthlySender) {
        isMonthlySender = monthlySender;
    }

    public boolean isMonthlyViewer() {
        return isMonthlyViewer;
    }

    public void setMonthlyViewer(boolean monthlyViewer) {
        isMonthlyViewer = monthlyViewer;
    }

    public boolean isDailySender() {
        return isDailySender;
    }

    public void setDailySender(boolean dailySender) {
        isDailySender = dailySender;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
