package com.qboxus.tictic.models;


import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.Variables;

import java.io.Serializable;

/**
 * Created by qboxus on 2/22/2019.
 */


public class SoundsModel implements Serializable {

    public String id, sound_name, description, section, duration, date_created, fav;
    private String acc_path;
    private String thum;

    public SoundsModel() {
    }

    public String getAcc_path() {
        if (!acc_path.contains(Variables.http)) {
            acc_path = Constants.BASE_URL + acc_path;
        }
        return acc_path;
    }

    public void setAcc_path(String acc_path) {
        this.acc_path = acc_path;
    }

    public String getThum() {
        if (!thum.contains(Variables.http)) {
            thum = Constants.BASE_URL + thum;
        }
        return thum;
    }

    public void setThum(String thum) {
        this.thum = thum;
    }
}
