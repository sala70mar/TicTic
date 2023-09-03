package com.qboxus.tictic.activitesfragments.profile.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.FragmentDetailMsgBinding;

public class DetailMsgF extends BottomSheetDialogFragment {


    FragmentDetailMsgBinding binding;
    public DetailMsgF() {
        // Required empty public constructor
    }


    public static DetailMsgF newInstance(String title,String description) {
        DetailMsgF fragment = new DetailMsgF();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("description",description);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_detail_msg, container, false);

        Bundle bundle=getArguments();

        binding.titleTxt.setText(bundle.getString("title"));
        binding.descriptionTxt.setText(bundle.getString("description"));

        return binding.getRoot();


    }
}