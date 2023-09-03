package com.qboxus.tictic.models;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.Variables;

public class FollowingModel {

    public String fb_id, username, first_name, last_name, gender, bio;
    public String follow_status_button;
    public boolean is_select,isFollow;
    public String notificationType;
    private String gifLink,profile_pic;

    public String getProfile_pic() {
        if (!profile_pic.contains(Variables.http)) {
            profile_pic = Constants.BASE_URL + profile_pic;
        }
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getGifLink() {
        return gifLink;
    }

    public void setGifLink(String gifLink) {
        this.gifLink = gifLink;
    }
}
