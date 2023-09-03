package com.qboxus.tictic.activitesfragments.profile.settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.volley.plus.VPackages.VolleyRequest;
import com.qboxus.tictic.Constants;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import org.json.JSONObject;

public class UpdateEmailPhoneA extends AppCompatLocaleActivity implements View.OnClickListener {

    TextView tvTitle;
    RelativeLayout tabPhoneNo,tabEmail;
    EditText edtEmail, edtPhoneNo;
    CountryCodePicker ccp;
    Button btnEmailNext;
    String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(UpdateEmailPhoneA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this,getClass(),false);
        setContentView(R.layout.activity_update_email_phone);

        initControl();
    }

    private void initControl() {
        tvTitle=findViewById(R.id.tvTitle);
        tabPhoneNo=findViewById(R.id.tabPhoneNo);
        tabEmail=findViewById(R.id.tabEmail);
        edtEmail =findViewById(R.id.email_edit);
        edtPhoneNo =findViewById(R.id.phone_edit);
        findViewById(R.id.goBack).setOnClickListener(this);
        ccp =findViewById(R.id.ccp);
        ccp.registerPhoneNumberTextView(edtPhoneNo);
        btnEmailNext=findViewById(R.id.btnSendCodeEmail);
        btnEmailNext.setOnClickListener(this);
        findViewById(R.id.btnSendCodePhone).setOnClickListener(this);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                // check the email validation during user typing
                String txtName = edtEmail.getText().toString();
                if (txtName.length() > 0) {
                    btnEmailNext.setEnabled(true);
                    btnEmailNext.setClickable(true);
                } else {
                    btnEmailNext.setEnabled(false);
                    btnEmailNext.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setUpScreenData();
    }

    private void setUpScreenData() {
        if (getIntent().getStringExtra("type").equalsIgnoreCase("email"))
        {
            tvTitle.setText(getString(R.string.update_email));
            tabPhoneNo.setVisibility(View.GONE);
            tabEmail.setVisibility(View.VISIBLE);
        }
        else
        if (getIntent().getStringExtra("type").equalsIgnoreCase("phone"))
        {
            tvTitle.setText(getString(R.string.update_phone));
            tabPhoneNo.setVisibility(View.VISIBLE);
            tabEmail.setVisibility(View.GONE);
        }
    }


    public boolean checkPhoneValidation() {

        final String st_phone = edtPhoneNo.getText().toString();

        if (TextUtils.isEmpty(st_phone)) {
            edtPhoneNo.setError(getString(R.string.enter_valid_phone_no));
            edtPhoneNo.setFocusable(true);
            return false;
        }


        if (!ccp.isValid()) {
            edtPhoneNo.setError(getString(R.string.enter_valid_phone_no));
            edtPhoneNo.setFocusable(true);
            return false;
        }

        phoneNo= edtPhoneNo.getText().toString();
        phoneNo=Functions.applyPhoneNoValidation(phoneNo,ccp.getSelectedCountryCodeWithPlus());

        return true;
    }


    private void callApiOtpForEmail() {

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("user_id", Functions.getSharedPreference(UpdateEmailPhoneA.this).getString(Variables.U_ID,""));
            parameters.put("email", edtEmail.getText().toString());
            parameters.put("verify","0");
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(UpdateEmailPhoneA.this, false, false);
        VolleyRequest.JsonPostRequest(UpdateEmailPhoneA.this, ApiLinks.changeEmailAddress, parameters, Functions.getHeaders(this),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(UpdateEmailPhoneA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        moveToVerificationScreen(edtEmail.getText().toString());
                    } else {
                        Functions.showToast(UpdateEmailPhoneA.this, jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private void callApiOtpForPhone() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(UpdateEmailPhoneA.this).getString(Variables.U_ID,""));
            parameters.put("phone",phoneNo);
            parameters.put("verify","0");
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(UpdateEmailPhoneA.this, false, false);
        VolleyRequest.JsonPostRequest(UpdateEmailPhoneA.this, ApiLinks.changePhoneNo, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(UpdateEmailPhoneA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        moveToVerificationScreen(phoneNo);
                    } else {
                        Functions.showToast(UpdateEmailPhoneA.this, jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    // this will check the validations like none of the field can be the empty
    public boolean checkEmailValidation() {

        final String st_email = edtEmail.getText().toString();

        if (TextUtils.isEmpty(st_email)) {
            edtEmail.setError(getString(R.string.enter_valid_email));
            edtEmail.setFocusable(true);
            return false;
        }

        else if(!Functions.isValidEmail(st_email)){
            edtEmail.setError(getString(R.string.enter_valid_email));
            edtEmail.setFocusable(true);
            return false;
        }

        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendCodePhone:
                if (checkPhoneValidation()) {

                    Log.d(Constants.tag,"Phone : "+phoneNo);
                    callApiOtpForPhone();
                }
                break;
            case R.id.btnSendCodeEmail:
            {
                if (checkEmailValidation())
                {
                    Log.d(Constants.tag,"Email : "+ edtEmail.getText().toString());
                    callApiOtpForEmail();
                }
            }
            break;
            case R.id.goBack:
            {
                UpdateEmailPhoneA.super.onBackPressed();
            }
            break;
        }
    }

    private void moveToVerificationScreen(String data) {
        Intent intent=new Intent(UpdateEmailPhoneA.this,UpdateEmailPhoneNoVerification.class);
        intent.putExtra("type",getIntent().getStringExtra("type"));
        intent.putExtra("data",data);
        resultCallback.launch(intent);

    }

    private void moveBack() {
        Intent intent = new Intent();
        intent.putExtra("isShow", true);
        setResult(RESULT_OK, intent);
        finish();
    }


    // start trimming activity
    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            moveBack();
                        }

                    }
                }
            });
}