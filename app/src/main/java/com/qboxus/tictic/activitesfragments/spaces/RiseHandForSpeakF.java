package com.qboxus.tictic.activitesfragments.spaces;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.FragmentRiseHandForSpeakBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;


public class RiseHandForSpeakF extends BottomSheetDialogFragment implements View.OnClickListener{


    FragmentRiseHandForSpeakBinding binding;
    FragmentCallBack callBack;

    public RiseHandForSpeakF(FragmentCallBack callBack) {
        this.callBack = callBack;
    }

    public RiseHandForSpeakF() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_rise_hand_for_speak, container, false);
        initControl();
        return binding.getRoot();
    }

    private void initControl() {
        binding.tabRiseHandForSpeak.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabRiseHandForSpeak);
        binding.tabNeverMind.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabNeverMind);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tabRiseHandForSpeak:
            {
                performAction("riseHandForSpeak");
            }
            break;
            case R.id.tabNeverMind:
            {
                performAction("neverMind");
            }
            break;
        }
    }

    private void performAction(String action) {
        Bundle bundle=new Bundle();
        bundle.putBoolean("isShow",true);
        bundle.putString("action",action);
        callBack.onResponce(bundle);
        dismiss();
    }


}