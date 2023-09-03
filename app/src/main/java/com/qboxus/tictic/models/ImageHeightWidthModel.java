package com.qboxus.tictic.models;

import java.io.Serializable;

public class ImageHeightWidthModel implements Serializable {
    int imageHeight,imageWidth;

    public ImageHeightWidthModel() {
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
}
