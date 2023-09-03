package com.qboxus.tictic.activitesfragments.livestreaming.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.qboxus.tictic.activitesfragments.livestreaming.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;


public class StreamingMainA extends BaseActivity implements View.OnClickListener{

    RelativeLayout tabStartLive;
    ImageView ivBack,ivSetting;
    TextView tvUserName;
    EditText etDescription;
    String userId, userName, userPicture,streamingId;
    int userRole;
    PinView pinView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this,getClass(),false);
        setContentView(R.layout.activity_main_streaming);

        Intent bundle = getIntent();
        if (bundle != null) {
            userId = bundle.getStringExtra("userId");
            userName = bundle.getStringExtra("userName");
            userPicture = bundle.getStringExtra("userPicture");
            userRole = bundle.getIntExtra("userRole", io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
            streamingId=bundle.getStringExtra("streamingId");
        }
        initUI();
    }

    private void initUI() {
        ivBack=findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        ivSetting=findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(this);
        etDescription=findViewById(R.id.etDescription);
        tvUserName=findViewById(R.id.tvUserName);
        pinView=findViewById(R.id.pinView);
        tabStartLive = findViewById(R.id.tabStartLive);
        tabStartLive.setOnClickListener(this);

        tvUserName.setText(userName);

    }



    public void onSettingClicked() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }




    // open  the live streaming of othe user or open userself
    public void gotoRoleActivity() {

        final Intent intent = new Intent();
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userPicture", userPicture);
        intent.putExtra("userRole", userRole);
        intent.putExtra("description", ""+etDescription.getText().toString());
        intent.putExtra("secureCode", ""+pinView.getText().toString());
        intent.putExtra("streamingId",streamingId);
        intent.putExtra(Constants.KEY_CLIENT_ROLE, userRole);
        config().setChannelName(streamingId);
        if (getIntent().hasExtra("onlineType"))
        {
            intent.putExtra("onlineType", "oneTwoOne");
            intent.putExtra("bookingId", getIntent().getStringExtra("bookingId"));
            intent.setClass(StreamingMainA.this, SingleCastStreamer.class);
        }
        else
        {
            intent.putExtra("onlineType", "multicast");
            intent.setClass(StreamingMainA.this, MulticastStreamerA.class);
        }
        startActivity(intent);
        finish();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivBack:
            {
                StreamingMainA.super.onBackPressed();
            }
            break;
            case R.id.ivSetting:
            {
                onSettingClicked();
            }
            break;
            case R.id.tabStartLive:
            {
                gotoRoleActivity();
            }
            break;
        }
    }

}
