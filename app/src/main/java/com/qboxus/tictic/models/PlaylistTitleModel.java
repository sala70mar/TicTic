package com.qboxus.tictic.models;

import java.io.Serializable;

public class PlaylistTitleModel implements Serializable {
    String id,name;

    public PlaylistTitleModel() {
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
}
