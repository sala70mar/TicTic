package com.qboxus.tictic.models;

import java.io.Serializable;

public class OptionSelectionModel implements Serializable {
    String id,title;

    public OptionSelectionModel() {
    }

    public OptionSelectionModel(String id, String title) {
        this.id = id;
        this.title = title;
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
}
