package com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager;

import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;

import java.io.Serializable;
import java.util.ArrayList;

public class MainStreamingModel implements Serializable {
    StreamModel model;
    ArrayList<HomeUserModel> userList;

    public MainStreamingModel() {
    }

    public StreamModel getModel() {
        return model;
    }

    public void setModel(StreamModel model) {
        this.model = model;
    }

    public ArrayList<HomeUserModel> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<HomeUserModel> userList) {
        this.userList = userList;
    }
}
