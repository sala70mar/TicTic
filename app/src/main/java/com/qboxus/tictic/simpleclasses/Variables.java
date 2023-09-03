package com.qboxus.tictic.simpleclasses;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by qboxus on 2/15/2019.
 */

public class Variables {


    public Variables() {
    }

    public static final String DEVICE = "android";
    //defult video heigh is 620X1102
    public static final int videoHeight=1102;
    public static final int videoWidth=620;
    public static final String SelectedAudio_AAC = "SelectedAudio.aac";

    public static final String APP_STORY_EDITED_FOLDER = "Hided/";
    public static final String APP_OUTPUT_FOLDER = "Output/";
    public static final String APP_HIDED_FOLDER = "HidedTicTic/";
    public static final String DRAFT_APP_FOLDER = "Draft/";

    public static final String onlineUser = "OnlineUsers";

    public static String is_puchase = "is_puchase";
    public static String outputfile2 = APP_HIDED_FOLDER + "output2.mp4";


    public static String videoChunk = APP_HIDED_FOLDER + "videoChunk";

    public static String output_filter_file = APP_HIDED_FOLDER + "output-filtered.mp4";
    public static String gallery_trimed_video = APP_HIDED_FOLDER + "gallery_trimed_video.mp4";
    public static String gallery_resize_video = APP_HIDED_FOLDER + "gallery_resize_video.mp4";

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences settingsPreferences;
    public static final String SETTING_PREF_NAME = "SETTING_PREF_NAME";
    public static final String PREF_NAME = "pref_name";
    public static final String U_ID = "u_id";
    public static final String U_WALLET = "u_wallet";
    public static final String U_PAYOUT_ID = "u_payout_id";
    public static final String U_NAME = "u_name";
    public static final String U_PHONE_NO = "Phone_No";
    public static final String U_EMAIL = "User_Email";
    public static final String U_SOCIAL_ID = "Social_Id";
    public static final String IS_VERIFIED = "is_verified";
    public static final String IS_VERIFICATION_APPLY = "is_verification_apply";
    public static final String U_PIC = "u_pic";
    public static final String U_GIF = "u_gif";
    public static final String U_PROFILE_VIEW = "u_profile_view";
    public static final String F_NAME = "f_name";
    public static final String L_NAME = "l_name";
    public static final String GENDER = "u_gender";
    public static final String U_BIO = "U_bio";
    public static final String U_LINK = "U_link";
    public static final String REFERAL_CODE = "referal_code";
    public static final String IS_LOGIN = "is_login";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String DEVICE_IP = "device_ip";
    public static final String DEVICE_LAT = "device_lat";
    public static final String DEVICE_LNG = "device_lng";
    public static final String AUTH_TOKEN = "api_token";
    public static final String DEVICE_ID = "device_id";
    public static final String UPLOADING_VIDEO_THUMB = "uploading_video_thumb";
    public static final String APP_LANGUAGE = "app_language";
    public static final String APP_LANGUAGE_CODE = "app_language_code";
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final String DEFAULT_LANGUAGE = "English";
    public static final String countryRegion = "countryRegion";
    public static final String IsExtended = "IsExtended";
    public static final String IsPrivacyPolicyAccept = "IsPrivacyPolicyAccept";

    public static final String ShowAdvertAfter="show_advert_after";
    public static final String CoinWorth="coin_worth";
    public static final String AddType="add_type";
    public static final String VideoCompression="video_compression";

    //Paper DB collection
    public static final String MultiAccountKey = "Accounts";
    public static final String PrivacySetting = "Setting";
    public static final String PrivacySettingModel = "PrivacySettingModel";
    public static final String PushSettingModel = "PushSettingModel";
    public static final String PromoAds = "Promo";
    public static final String PromoAdsModel = "ads";


    public static String selectedSoundId = "null";
    public static boolean reloadMyVideos = false;
    public static boolean reloadMyVideosInner = false;
    public static boolean reloadMyLikesInner = false;
    public static boolean reloadMyNotification = false;

    //this variable is used to handle compression between gallery and camera upload video
    public static boolean isCompressionApplyOnStart = false;
    public static final String GIF_FIRSTPART = "https://media.giphy.com/media/";
    public static final String GIF_SECONDPART = "/100w.gif";


    public static final String http = "http";
    public static final String https = "https";

    public static final SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static final SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);

    public static String VideoDirectory = "Videos";


    public static String liked_your_video_en="liked your video";
    public static String has_posted_a_video_en="has posted a video";
    public static String started_following_you_en="started following you";
    public static String commented_en="commented:";
    public static String is_live_now_en="is live now";
    public static String mentioned_you_in_a_comment_en="mentioned you in a comment:";
    public static String replied_to_your_comment_en="replied to your comment:";

    public static String liked_your_video_ar="أحب الفيديو الخاص بك";
    public static String has_posted_a_video_ar="نشر مقطع فيديو";
    public static String started_following_you_ar="بدات الاحقك";
    public static String commented_ar="علق:";
    public static String is_live_now_ar="يعيش الآن";
    public static String mentioned_you_in_a_comment_ar="ذكرك في تعليق:";
    public static String replied_to_your_comment_ar="رد على تعليقك:";


    //current followerlist
    public static HashMap<String,String> followMapList=new HashMap<>();


    public static final String roomKey = "LiveRoom";

    public static final String roomInvitation = "RoomInvitation";

    public static final String roomchat = "RoomChat";

    public static final String roomUsers = "Users";

    public static final String roomUserWave = "UserWave";

    public static final String joinedKey = "Joined";

    public static final String ACTION_KEY_CHANNEL_NAME = "ecHANEL";

}
