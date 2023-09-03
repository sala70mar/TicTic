package com.qboxus.tictic;

public class Constants {

    public static final String BASE_URL ="https://domain.com/mobileapp_api/";
    public static final String API_KEY="API_KEY";
    //for referal deep linking
    public static final String REFERRAL_LINK = BASE_URL+"ref/?code=";

    //here we place dummy key please replace it with your orignal one
    public static final String API_URL = "https://apis.argear.io/";
    public static final String API_KEY_ARGEAR = "3z6zz8d3837885565z69z1z3";
    public static final String SECRET_KEY = "zz72254308z4zzz5zzz275zz12e80d6be3228b47de47c2c5d7619z04z0z13476";
    public static final String AUTH_KEY = "U2FsdGVkX1+vDOmFtNUtJOD5s7mHBHE19FU6iXXCDvlH8O1jcyC67rceesJ81fWMOc5zzzzzzzzzz4zzzzzzzz==";


    public final static String APP_AD_COLONY_ID = "APP_AD_COLONY_ID";
    public final static String AD_COLONY_BANNER_ID = "AD_COLONY_BANNER_ID";
    public final static String AD_COLONY_INTERSTITIAL_ID = "AD_COLONY_INTERSTITIAL_ID";
    public final static String[] AD_COLONY_UNIT_Zone_Ids = new String[]{AD_COLONY_BANNER_ID, AD_COLONY_INTERSTITIAL_ID};


    public static final String privacy_policy="https://google.com";
    public static final String terms_conditions="https://google.com";


    // if you want a user can't share a video from your app then you have to set this value to true
    public static final boolean IS_SECURE_INFO = false;


    // if you show the ad on after every specific video count
    public static final int SHOW_AD_ON_EVERY=8;


    // if you want a video thumnail image show rather then a video gif then set the below value to false.
    public static final boolean IS_SHOW_GIF = true;

    // if you want to disbale all the toasts in the app
    public static final boolean IS_TOAST_ENABLE = false;


    // if you want to add a limit that a user can watch only 6 video then change the below value to true
    // if you want to change the demo videos limit count then set the count as you want
    public static final boolean IS_DEMO_APP = false;
    public static final int DEMO_APP_VIDEOS_COUNT = 6;


    // maximum time to record the video for now it is 30 sec
    public static int MAX_RECORDING_DURATION = 600000;
    public static int RECORDING_DURATION = 30000;


    // minimum time of recode a video for now it is 5 sec
    public static int MIN_TIME_RECORDING = 5000;

    // max photo allowed for photo video upload
    public static int MAX_PICS_ALLOWED_FOR_VIDEO = 5;
    //max time for photo videos
    public static int MAX_TIME_FOR_VIDEO_PICS = 10;

    // minimum trim chunk time span of a video for now it is 5 sec
    public static int MIN_TRIM_TIME = 5;
    // maximum trim chunk time span of a video for now it is 30 sec
    public static int MAX_TRIM_TIME = 30;


    //video description char limit during posting the video
    public final static int VIDEO_DESCRIPTION_CHAR_LIMIT = 250;

    // Username char limit during signup and edit the account
    public static final int USERNAME_CHAR_LIMIT = 30;

    // user profile bio char limit during edit the profile
    public static final int BIO_CHAR_LIMIT = 150;


    // set the profile image max size for now it is 400 * 400
    public static final int PROFILE_IMAGE_SQUARE_SIZE=300;
    public static final String CURRENCY="$";
    // Make product ids of different prices on google play console and place that ids in it.
    public static final String COINS0="100";
    public static final String PRICE0=CURRENCY+"1";
    public static final String Product_ID0="android.test.purchased";

    public static final String COINS1="500";
    public static final String PRICE1=CURRENCY+"5";
    public static final String Product_ID1="com.qboxus.tictic.coin2";

    public static final String COINS2="2000";
    public static final String PRICE2=CURRENCY+"20";
    public static final String Product_ID2="com.qboxus.tictic.coin3";

    public static final String COINS3="5000";
    public static final String PRICE3=CURRENCY+"50";
    public static final String Product_ID3="com.qboxus.tictic.coin4";

    public static final String COINS4="10000";
    public static final String PRICE4=CURRENCY+"100";
    public static final String Product_ID4="com.qboxus.tictic.coin5";


    // The tag name you want to print all the log
    public static String tag = "tictic_";


    public static final int ALL_IMAGE_DEFAULT_SIZE=500;
}
