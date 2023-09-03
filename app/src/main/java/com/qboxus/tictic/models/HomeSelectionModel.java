package com.qboxus.tictic.models;

import java.io.Serializable;

public class HomeSelectionModel implements Serializable {
    HomeModel model;
    boolean isSelect;

    public HomeSelectionModel() {
    }

    public HomeModel getModel() {
        return model;
    }

    public void setModel(HomeModel model) {
        this.model = model;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
