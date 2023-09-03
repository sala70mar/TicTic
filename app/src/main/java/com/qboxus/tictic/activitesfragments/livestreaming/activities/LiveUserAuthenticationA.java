package com.qboxus.tictic.activitesfragments.livestreaming.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

public class LiveUserAuthenticationA extends AppCompatLocaleActivity implements View.OnClickListener {

    RelativeLayout tabStartLive;
    LiveUserModel selectLiveModel;
    PinView pinView;
    TextView tvUserName;
    SimpleDraweeView ivProfile,ivSmallProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_live_user_authentication);

        InitControl();
    }

    private void InitControl() {
        selectLiveModel= (LiveUserModel) getIntent().getSerializableExtra("userModel");
        pinView=findViewById(R.id.pinView);
        tabStartLive=findViewById(R.id.tabStartLive);
        tabStartLive.setOnClickListener(this);
        ivProfile=findViewById(R.id.ivProfile);
        tvUserName=findViewById(R.id.tvUserName);
        ivSmallProfile=findViewById(R.id.ivSmallProfile);

        setUpScreenData();
    }

    private void setUpScreenData() {
        tvUserName.setText(selectLiveModel.getUserName());
        ivProfile.setController(Functions.frescoBlurImageLoad(selectLiveModel.getUserPicture(),LiveUserAuthenticationA.this,75));
        ivSmallProfile.setController(Functions.frescoImageLoad(selectLiveModel.getUserPicture(),ivSmallProfile,false));
    }


    // watch the streaming of user which will be live
    public void openTicTicLive() {
        finish();
        final Intent intent = new Intent();
        intent.putExtra("user_id", selectLiveModel.getUserId());
        intent.putExtra("user_name", selectLiveModel.getUserName());
        intent.putExtra("user_picture", selectLiveModel.getUserPicture());
        intent.putExtra("user_role", io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        intent.putExtra("onlineType", "multicast");
        intent.putExtra("description", selectLiveModel.getDescription());
        intent.putExtra("secureCode", ""+pinView.getText().toString());
        intent.putExtra("dataList",getIntent().getSerializableExtra("dataList"));
        intent.putExtra("position",getIntent().getIntExtra("position",0));
        intent.setClass(LiveUserAuthenticationA.this, MultiViewLiveA.class);
        startActivity(intent);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tabStartLive:
            {
                if (selectLiveModel.getSecureCode().equals(pinView.getText().toString()))
                {
                    LiveUsersA.unlockStream.put(selectLiveModel.getUserId(),pinView.getText().toString());
                    openTicTicLive();
                }
                else
                {
                    Toast.makeText(LiveUserAuthenticationA.this, getString(R.string.add_correct_passcode), Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }
}