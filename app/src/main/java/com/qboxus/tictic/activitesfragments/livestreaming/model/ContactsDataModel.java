package com.qboxus.tictic.activitesfragments.livestreaming.model;

import java.io.Serializable;

public class ContactsDataModel implements Serializable {
    public String Username,uid,email,userId,Picture,firstName,lastName,verified;
    public Boolean online,isexits;
    public int imagecolor;

    public ContactsDataModel() {
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getIsexits() {
        return isexits;
    }

    public void setIsexits(Boolean isexits) {
        this.isexits = isexits;
    }

    public int getImagecolor() {
        return imagecolor;
    }

    public void setImagecolor(int imagecolor) {
        this.imagecolor = imagecolor;
    }
}
