package com.qboxus.tictic.activitesfragments.livestreaming.model;

import java.io.Serializable;

public class CameraRequestModel implements Serializable {
    public String requestState;

    public CameraRequestModel() {
    }

    public String getRequestState() {
        return requestState;
    }

    public void setRequestState(String requestState) {
        this.requestState = requestState;
    }
}
