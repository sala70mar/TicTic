package com.qboxus.tictic.activitesfragments.livestreaming.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qboxus.tictic.activitesfragments.livestreaming.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

public class ConcertSelectionA extends BaseActivity {

    RelativeLayout tabAdd,tabMinus,tabStartLive;
    EditText etVorzCoins,etDescription;
    TextView tvUserName;
    ImageView ivBack,ivSetting;
    RadioButton rbPrivate,rbPublic;
    RadioGroup rgType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_concert_selection);

        initControl();
        actionControl();
    }

    private void actionControl() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConcertSelectionA.super.onBackPressed();
            }
        });

        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingClicked();
            }
        });

        tabMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(TextUtils.isEmpty(etVorzCoins.getText().toString())))
                {
                    substractNumber(etVorzCoins.getText().toString());
                }
            }
        });

        tabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(TextUtils.isEmpty(etVorzCoins.getText().toString())))
                {
                    if (!(TextUtils.isEmpty(etVorzCoins.getText().toString())))
                    {
                        addNumber(etVorzCoins.getText().toString());
                    }
                }
            }
        });

        tabStartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRoleActivity();
            }
        });

    }

    private void substractNumber(String numberStr) {
        int number=Integer.valueOf(numberStr);
        if (number>0)
        {
            number=number-1;
        }
        etVorzCoins.setText(""+number);
    }

    private void addNumber(String numberStr) {
        int number=Integer.valueOf(numberStr);
        if (number<1000)
        {
            number=number+1;
        }
        etVorzCoins.setText(""+number);
    }

    public void onSettingClicked() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    private void initControl() {
        tabAdd=findViewById(R.id.tabAdd);
        tabMinus=findViewById(R.id.tabMinus);
        tabStartLive=findViewById(R.id.tabStartLive);
        etVorzCoins=findViewById(R.id.etVorzCoins);
        etDescription=findViewById(R.id.etDescription);
        tvUserName=findViewById(R.id.tvUserName);
        ivBack=findViewById(R.id.ivBack);
        ivSetting=findViewById(R.id.ivSetting);
        rgType=findViewById(R.id.rgType);
        rbPrivate=findViewById(R.id.rbPrivate);
        rbPublic=findViewById(R.id.rbPublic);


        setUpScreenData();
    }

    String userId,userName,userPic,streamingId;
    int userRole;
    private void setUpScreenData() {
        Intent bundle = getIntent();
        if (bundle != null) {
            userId = bundle.getStringExtra("userId");
            userName = bundle.getStringExtra("userName");
            userPic = bundle.getStringExtra("userPicture");
            userRole = bundle.getIntExtra("userRole", io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
            streamingId=bundle.getStringExtra("streamingId");
        }
        tvUserName.setText(userName);
    }


    // open  the live streaming of othe user or open userself
    public void gotoRoleActivity() {

        final Intent intent = new Intent();
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userPicture", userPic);
        intent.putExtra("userRole", userRole);
        intent.putExtra("description", ""+etDescription.getText().toString());
        intent.putExtra("secureCode", "");
        intent.putExtra("streamingId",streamingId);
        intent.putExtra("joinStreamPrice",Integer.valueOf(""+etVorzCoins.getText().toString()));
        intent.putExtra(Constants.KEY_CLIENT_ROLE, userRole);
        config().setChannelName(streamingId);
        if (rbPrivate.isChecked())
        {
            intent.putExtra("onlineType", "oneTwoOne");
            intent.setClass(ConcertSelectionA.this, SingleCastStreamer.class);
        }
        else
        {
            intent.putExtra("dualStreaming",rbPublic.isChecked());
            intent.putExtra("onlineType", "multicast");
            intent.setClass(ConcertSelectionA.this, MulticastStreamerA.class);
        }
        startActivity(intent);
        finish();

    }
}