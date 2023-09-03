package com.qboxus.tictic.models;

import android.util.Log;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.Variables;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by qboxus on 2/18/2019.
 */

public class HomeModel implements Serializable {
    public String user_id="", username="", first_name="", last_name="", verified="";
    public String video_id="", video_description="", created_date="";
    public String promote="";
    public String sound_id="", sound_name="", video_user_id="";
    private String sound_url_acc="", sound_url_mp3="",sound_pic="",video_url="",profile_pic="",gif="",thum="";

    public String privacy_type="", allow_comments="", allow_duet="", liked="", like_count="", video_comment_count="", views="", duet_video_id="",
            duet_username="",favourite_count="",share="",duration="0",pin="0";

    //for playlist
    public String playlistVideoId;
    public String playlistId="", playlistName="";
    //for video block
    public String block="",aws_label="";
    //repost
    public String repost_video_id="", repost_user_id="",repost="";
    // additional param
    public String favourite;
    public String follow_status_button;

    public PrivacyPolicySettingModel apply_privacy_model;

    public PushNotificationSettingModel apply_push_notification_model;

    //user story manage
    public ArrayList<StoryModel> storyDataList;

    private PromotionModel promotionModel;

    public PromotionModel getPromotionModel() {
        return promotionModel;
    }

    public void setPromotionModel(PromotionModel promotionModel) {
        this.promotionModel = promotionModel;
    }

    public String getSound_url_acc() {
        if (!sound_url_acc.contains(Variables.http)) {
            sound_url_acc = Constants.BASE_URL + sound_url_acc;
        }
        return sound_url_acc;
    }

    public void setSound_url_acc(String sound_url_acc) {
        this.sound_url_acc = sound_url_acc;
    }

    public String getSound_url_mp3() {
        if (!sound_url_mp3.contains(Variables.http)) {
            sound_url_mp3 = Constants.BASE_URL + sound_url_mp3;
        }
        return sound_url_mp3;
    }

    public void setSound_url_mp3(String sound_url_mp3) {
        this.sound_url_mp3 = sound_url_mp3;
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

    public String getSound_pic() {
        if (!sound_pic.contains(Variables.http)) {
            sound_pic = Constants.BASE_URL + sound_pic;
        }
        return sound_pic;
    }

    public void setSound_pic(String sound_pic) {
        this.sound_pic = sound_pic;
    }

    public String getVideo_url() {
        if (!video_url.contains(Variables.http)) {
            video_url = Constants.BASE_URL + video_url;
        }
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getGif() {
        if (!gif.contains(Variables.http)) {
            gif = Constants.BASE_URL + gif;
        }
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
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
