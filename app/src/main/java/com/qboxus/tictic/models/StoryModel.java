package com.qboxus.tictic.models;

import java.io.Serializable;
import java.util.ArrayList;

public class StoryModel implements Serializable {

    private UserModel userModel;
    ArrayList<StoryVideoModel> videoList;

    public StoryModel() {
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public ArrayList<StoryVideoModel> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<StoryVideoModel> videoList) {
        this.videoList = videoList;
    }
}
