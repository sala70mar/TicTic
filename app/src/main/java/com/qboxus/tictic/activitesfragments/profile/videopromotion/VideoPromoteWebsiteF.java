package com.qboxus.tictic.activitesfragments.profile.videopromotion;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.FragmentVideoPromoteWebsiteBinding;
import com.qboxus.tictic.simpleclasses.Functions;


public class VideoPromoteWebsiteF extends Fragment {

    FragmentVideoPromoteWebsiteBinding binding;


    public VideoPromoteWebsiteF() {
    }

    public static VideoPromoteWebsiteF newInstance() {
        VideoPromoteWebsiteF fragment = new VideoPromoteWebsiteF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_video_promote_website, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.tabLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(1);
            }
        });
        binding.tabShopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(2);
            }
        });
        binding.tabSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(3);
            }
        });
        binding.tabContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(4);
            }
        });
        binding.tabApplyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(5);
            }
        });
        binding.tabBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(6);
            }
        });

        binding.etWebsiteUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                updateWebURLStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoPromoteStepsA.requestPromotionModel.setWebsiteULR(""+binding.etWebsiteUrl.getText().toString());

                int counts=VideoPromoteStepsA.adapter.getItemCount();
                VideoPromoteStepsA.progressBar.setMax(5);

                if (counts>(counts+1))
                {
                    VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                    VideoPromoteStepsA.progressBar.setProgress((counts),true);
                }
                else
                {
                    VideoPromoteStepsA.adapter.addFrag(VideoPromoteSelectAudienceF.newInstance());
                    VideoPromoteStepsA.adapter.notifyItemInserted((counts+1));
                    VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                    VideoPromoteStepsA.progressBar.setProgress((counts),true);
                }
            }
        });
    }

    private void updateWebURLStatus() {
        if (Functions.isWebUrl(binding.etWebsiteUrl.getText().toString())) {
            binding.btnNext.setEnabled(true);
            binding.btnNext.setClickable(true);
            binding.etWebsiteUrl.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.d_bottom_gray_line));
            binding.etWebsiteUrl.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
            binding.etWebsiteUrl.setError(null,null);
        } else {
            binding.btnNext.setEnabled(false);
            binding.btnNext.setClickable(false);
            binding.etWebsiteUrl.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.d_bottom_red_line));
            binding.etWebsiteUrl.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.redColor));
            binding.etWebsiteUrl.setError(binding.getRoot().getContext().getString(R.string.must_enter_your_website_link_for_promotion));
            binding.etWebsiteUrl.requestFocus();
        }
    }

    private void updateSelection(int select) {
        VideoPromoteStepsA.requestPromotionModel.setWebsiteLandingPage(select);
        updateWebURLStatus();
        switch (select)
        {
            case 1:
            {
                binding.ivLearnMore.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivShopNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivSignup.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivContactUs.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivApplyNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivBookNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
            }
            break;
            case 2:
            {
                binding.ivLearnMore.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivShopNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivSignup.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivContactUs.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivApplyNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivBookNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
            }
            break;
            case 3:
            {
                binding.ivLearnMore.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivShopNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivSignup.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivContactUs.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivApplyNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivBookNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
            }
            break;
            case 4:
            {
                binding.ivLearnMore.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivShopNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivSignup.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivContactUs.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivApplyNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivBookNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
            }
            break;
            case 5:
            {
                binding.ivLearnMore.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivShopNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivSignup.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivContactUs.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivApplyNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivBookNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
            }
            break;
            case 6:
            {
                binding.ivLearnMore.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivShopNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivSignup.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivContactUs.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivApplyNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivBookNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
            }
            break;
            default:
            {
                binding.ivLearnMore.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivShopNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivSignup.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivContactUs.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivApplyNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivBookNow.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
            }
        }
    }

    private void initControl() {
        updateSelection(1);
    }
}