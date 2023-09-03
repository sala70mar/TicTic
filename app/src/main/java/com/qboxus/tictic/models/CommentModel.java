package com.qboxus.tictic.models;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/5/2019.
 */

public class CommentModel {
    public String video_id,videoOwnerId, userId,user_name, first_name, last_name,comments, created;
    public String comment_id, pin_comment_id,isLikedByOwner,isVerified;
    public String liked;
    public String like_count;
    public String item_count_replies;
    public ArrayList<CommentModel> arrayList;
    public boolean isExpand;
    public String comment_reply_id, comment_reply, reply_create_date, arraylist_size, replay_user_name, replay_user_url, parent_comment_id;
    public String comment_reply_liked, reply_liked_count;

    private String profile_pic;

    public CommentModel() {
    }

    public String getProfile_pic() {
        if (!profile_pic.contains(Variables.http)) {
            profile_pic = Constants.BASE_URL + profile_pic;
        }
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
