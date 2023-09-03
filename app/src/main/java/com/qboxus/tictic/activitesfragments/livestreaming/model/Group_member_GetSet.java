package com.qboxus.tictic.activitesfragments.livestreaming.model;

import android.text.TextUtils;

import java.io.Serializable;

public class Group_member_GetSet implements Serializable {

    String user_id,user_name,user_pic,verified;
    String role="user";

    public String getVerified() {
        if (verified==null || TextUtils.isEmpty(verified))
        {
            return "";
        }
        else
        {
            return verified;
        }
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getUser_id() {
        if (user_id==null || TextUtils.isEmpty(user_id))
        {
            return "";
        }
        else
        {
            return user_id;
        }
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        if (user_name==null || TextUtils.isEmpty(user_name))
        {
            return "";
        }
        else
        {
            return user_name;
        }
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pic() {
        if (user_pic==null || TextUtils.isEmpty(user_pic))
        {
            return "";
        }
        else
        {
            return user_pic;
        }
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public String getRole() {
        if (role==null || TextUtils.isEmpty(role))
        {
            return "";
        }
        else
        {
            return role;
        }
    }

    public void setRole(String role) {
        this.role = role;
    }
}
