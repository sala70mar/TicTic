package com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager;

import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomJoinStatusModel implements Serializable {
    String roomId;
    HomeUserModel myModel;
    ArrayList<HomeUserModel> userList;

    public RoomJoinStatusModel() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public HomeUserModel getMyModel() {
        return myModel;
    }

    public void setMyModel(HomeUserModel myModel) {
        this.myModel = myModel;
    }

    public ArrayList<HomeUserModel> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<HomeUserModel> userList) {
        this.userList = userList;
    }
}
