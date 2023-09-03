package com.qboxus.tictic.models;

import java.io.Serializable;

public class PlaylistHomeModel  implements Serializable {
    HomeModel model;
    boolean isSelection;

    public PlaylistHomeModel() {
    }

    public HomeModel getModel() {
        return model;
    }

    public void setModel(HomeModel model) {
        this.model = model;
    }

    public boolean isSelection() {
        return isSelection;
    }

    public void setSelection(boolean selection) {
        isSelection = selection;
    }
}
