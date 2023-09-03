package com.qboxus.tictic.activitesfragments.profile.analytics;

import java.io.Serializable;

public class KeyMatricsModel implements Serializable {
    public String id;
    public boolean isSelected;
    public String name,count;

    public KeyMatricsModel(String name, String count) {
        this.name = name;
        this.count = count;
    }
}
