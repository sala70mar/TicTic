package com.qboxus.tictic.activitesfragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import org.json.JSONArray;
import org.json.JSONObject;

import io.paperdb.Paper;

public class SplashA extends AppCompatLocaleActivity {

    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigation();
        Functions.setLocale(Functions.getSharedPreference(SplashA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_splash);


        apiCallHit();
    }

    private void apiCallHit() {
        callApiForGetad();
        callApiForGetSettings();

        if (Functions.getSharedPreference(this).getString(Variables.DEVICE_ID, "0").equals("0"))
        {
            callApiRegisterDevice();
        }
        else
        {
            setTimer();
        }
    }

    private void callApiForGetSettings() {

        JSONObject params=new JSONObject();
        VolleyRequest.JsonPostRequest(SplashA.this, ApiLinks.showSettings, params,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(SplashA.this,resp);
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");

                    if(code!=null && code.equals("200")){

                        JSONArray msg=jsonObject.optJSONArray("msg");
                        for (int i = 0; i < msg.length(); i++) {
                            JSONObject jsonObject1 = msg.optJSONObject(i);
                            JSONObject settings = jsonObject1.optJSONObject("Setting");
                            String type = settings.getString("type");


                            if (type.equalsIgnoreCase("show_advert_after")) {
                                Functions.getSettingsPreference(SplashA.this).edit().putInt(Variables.ShowAdvertAfter, settings.optInt("value", 0)).commit();
                            }

                            if (type.equalsIgnoreCase("coin_worth")) {
                                Functions.getSettingsPreference(SplashA.this).edit().putString(Variables.CoinWorth, settings.optString("value", "0")).commit();
                            }

                            if (type.equalsIgnoreCase("add_type")) {
                                Functions.getSettingsPreference(SplashA.this).edit().putString(Variables.AddType, settings.optString("value", "0")).commit();
                            }

                            if (type.equalsIgnoreCase("video_compression")) {
                                Functions.getSettingsPreference(SplashA.this).edit().putString(Variables.VideoCompression, settings.optString("value", "2000")).commit();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }



    private void callApiForGetad() {

        JSONObject params=new JSONObject();
        try {
            if (Functions.getSharedPreference(SplashA.this).getBoolean(Variables.IS_LOGIN,false))
            {
                params.put("user_id",Functions.getSharedPreference(SplashA.this).getString(Variables.U_ID,""));
            }
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }
        VolleyRequest.JsonPostRequest(SplashA.this, ApiLinks.showVideoDetailAd, params,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(SplashA.this,resp);
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");

                    if(code!=null && code.equals("200")){
                        JSONObject msg=jsonObject.optJSONObject("msg");
                        JSONObject video=msg.optJSONObject("Video");
                        JSONObject user=msg.optJSONObject("User");
                        JSONObject sound = msg.optJSONObject("Sound");
                        JSONObject pushNotification=user.optJSONObject("PushNotification");
                        JSONObject privacySetting=user.optJSONObject("PrivacySetting");
                        HomeModel item = Functions.parseVideoData(user, sound, video, privacySetting, pushNotification);
                        item.promote="1";
                        Paper.book(Variables.PromoAds).write(Variables.PromoAdsModel,item);
                    }
                    else
                    {
                        Paper.book(Variables.PromoAds).destroy();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });


    }


    // show the splash for 3 sec
    public void setTimer() {
        countDownTimer = new CountDownTimer(2500, 500) {

            public void onTick(long millisUntilFinished) {
                // this will call on every 500 ms
            }

            public void onFinish() {

                if (Functions.getSettingsPreference(SplashA.this).getBoolean(Variables.IsPrivacyPolicyAccept,false)) {

                    Intent intent = new Intent(SplashA.this, MainMenuActivity.class);

                    if (getIntent().getExtras() != null) {
                        try {
                            // its for multiple account notification handling
                            String userId = getIntent().getStringExtra("receiver_id");
                            Functions.setUpSwitchOtherAccount(SplashA.this, userId);
                        } catch (Exception e) {
                        }

                        intent.putExtras(getIntent().getExtras());
                        setIntent(null);
                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                }
                else {
                    openWebUrl(getString(R.string.terms_of_use),Constants.terms_conditions);
                }

            }
        }.start();
    }



    public void openWebUrl(String title, String url) {
        Intent intent=new Intent(SplashA.this, WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("from","splash");
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(countDownTimer!=null)
        countDownTimer.cancel();
    }

    // register the device on server on application open
    public void callApiRegisterDevice() {

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        JSONObject param = new JSONObject();
        try {
            param.put("key", androidId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(this, ApiLinks.registerDevice, param,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(SplashA.this,resp);

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        setTimer();
                        JSONObject msg = jsonObject.optJSONObject("msg");
                        JSONObject Device = msg.optJSONObject("Device");
                        SharedPreferences.Editor editor2 = Functions.getSharedPreference(SplashA.this).edit();
                        editor2.putString(Variables.DEVICE_ID, Device.optString("id")).commit();
                    }
                    else
                    {
                        setTimer();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }

    // this will hide the bottom mobile navigation controll
    public void hideNavigation() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

    }

}
