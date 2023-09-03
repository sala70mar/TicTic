package com.qboxus.tictic.models;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.Variables;

public class SliderModel {

    public String id;
    private String url,image;

    public SliderModel() {
    }

    public String getUrl() {
        if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        if (!image.contains(Variables.http)) {
            image = Constants.BASE_URL + image;
        }
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}