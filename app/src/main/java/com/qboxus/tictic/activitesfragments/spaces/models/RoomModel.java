package com.qboxus.tictic.activitesfragments.spaces.models;

import java.io.Serializable;
import java.util.ArrayList;


public class RoomModel implements Serializable {
    String id,adminId,title,privacyType,created;
    ArrayList<HomeUserModel> userList;

    ArrayList<TopicModel> topicModels;

    public RoomModel() {
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(String privacyType) {
        this.privacyType = privacyType;
    }

    public ArrayList<HomeUserModel> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<HomeUserModel> userList) {
        this.userList = userList;
    }

    public ArrayList<TopicModel> getTopicModels() {
        return topicModels;
    }

    public void setTopicModels(ArrayList<TopicModel> topicModels) {
        this.topicModels = topicModels;
    }
}
