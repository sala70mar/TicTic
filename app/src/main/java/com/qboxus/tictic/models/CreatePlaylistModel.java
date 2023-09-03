package com.qboxus.tictic.models;

import java.io.Serializable;
import java.util.HashMap;

public class CreatePlaylistModel implements Serializable {
    String name;
    HashMap<String, HomeSelectionModel> itemCountList;

    public CreatePlaylistModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, HomeSelectionModel> getItemCountList() {
        return itemCountList;
    }

    public void setItemCountList(HashMap<String, HomeSelectionModel> itemCountList) {
        this.itemCountList = itemCountList;
    }

}