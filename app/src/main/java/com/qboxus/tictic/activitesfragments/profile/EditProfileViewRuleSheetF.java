package com.qboxus.tictic.activitesfragments.profile;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.databinding.FragmentEditProfileViewRuleSheetBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;


public class EditProfileViewRuleSheetF extends BottomSheetDialogFragment{


    FragmentCallBack callback;
    FragmentEditProfileViewRuleSheetBinding binding;
    String profileView="";

    public EditProfileViewRuleSheetF() {
    }

    public EditProfileViewRuleSheetF(FragmentCallBack callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_edit_profile_view_rule_sheet, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                callback.onResponce(bundle);
                dismiss();
            }
        });

        binding.switchProfileViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfileViewStatus();
            }
        });
    }

    private void updateProfileViewStatus() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, "0"));
            if (binding.switchProfileViewHistory.isChecked()) {
                profileView="1";
            } else {
                profileView="0";
            }
            parameters.put("profile_view", profileView);

        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.editProfile, parameters,Functions.getHeaders(binding.getRoot().getContext()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    JSONArray msg = response.optJSONArray("msg");
                    if (code.equals("200")) {

                        SharedPreferences.Editor editor = Functions.getSharedPreference(binding.getRoot().getContext()).edit();
                        editor.putString(Variables.U_PROFILE_VIEW, profileView);
                        editor.commit();
                    } else {
                        if (msg != null) {
                            JSONObject jsonObject = msg.optJSONObject(0);
                            Functions.showToast(binding.getRoot().getContext(), jsonObject.optString("response"));
                        }
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });

    }



    private void initControl()
    {
        profileView= Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_PROFILE_VIEW,"0");
        if (profileView.equals("1"))
        {
            binding.switchProfileViewHistory.setChecked(true);
        }
        else
        {
            binding.switchProfileViewHistory.setChecked(false);
        }
    }

}