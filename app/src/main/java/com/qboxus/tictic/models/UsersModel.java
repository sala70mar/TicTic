package com.qboxus.tictic.models;

import java.io.Serializable;

public class UsersModel implements Serializable {

    public String fb_id, username, first_name, last_name, gender,
            profile_pic, videos, followers_count;

    public boolean isSelected=false;

}
