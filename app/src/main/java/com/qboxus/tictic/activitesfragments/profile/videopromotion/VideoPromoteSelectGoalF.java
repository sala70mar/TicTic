package com.qboxus.tictic.activitesfragments.profile.videopromotion;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.FragmentVideoPromoteSelectGoalBinding;


public class VideoPromoteSelectGoalF extends Fragment {


    FragmentVideoPromoteSelectGoalBinding binding;


    public VideoPromoteSelectGoalF() {
    }

    public static VideoPromoteSelectGoalF newInstance() {
        VideoPromoteSelectGoalF fragment = new VideoPromoteSelectGoalF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_video_promote_select_goal, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.tabVideoViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(1);
            }
        });

        binding.tabMoreWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(2);
            }
        });

        binding.tabMoreFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelection(3);
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VideoPromoteStepsA.requestPromotionModel.getPromoteGoal()==1)
                {
                    int counts=VideoPromoteStepsA.adapter.getItemCount();
                    VideoPromoteStepsA.progressBar.setMax(4);

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
                else
                if (VideoPromoteStepsA.requestPromotionModel.getPromoteGoal()==2)
                {
                    int counts=VideoPromoteStepsA.adapter.getItemCount();
                    VideoPromoteStepsA.progressBar.setMax(5);

                    if (counts>(counts+1))
                    {
                        VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                        VideoPromoteStepsA.progressBar.setProgress((counts),true);
                    }
                    else
                    {
                        VideoPromoteStepsA.adapter.addFrag(VideoPromoteWebsiteF.newInstance());
                        VideoPromoteStepsA.adapter.notifyItemInserted((counts+1));
                        VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                        VideoPromoteStepsA.progressBar.setProgress((counts),true);
                    }

                }
                else
                if (VideoPromoteStepsA.requestPromotionModel.getPromoteGoal()==3)
                {
                    int counts=VideoPromoteStepsA.adapter.getItemCount();
                    VideoPromoteStepsA.progressBar.setMax(4);

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
            }
        });
    }

    private void initControl() {

        updateSelection(0);
    }

    private void updateSelection(int select) {
        VideoPromoteStepsA.requestPromotionModel.setPromoteGoal(select);
        switch (select)
        {
            case 1:
            {
                binding.ivVideoViewsSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivMoreWebsiteSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivMoreFollowersSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.btnNext.setEnabled(true);
                binding.btnNext.setClickable(true);
            }
            break;
            case 2:
            {
                binding.ivVideoViewsSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivMoreWebsiteSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.ivMoreFollowersSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.btnNext.setEnabled(true);
                binding.btnNext.setClickable(true);
            }
            break;
            case 3:
            {
                binding.ivVideoViewsSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivMoreWebsiteSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_un_selected));
                binding.ivMoreFollowersSelection.setImageDrawable(
                        ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ic_circle_selection));
                binding.btnNext.setEnabled(true);
                binding.btnNext.setClickable(true);
            }
            break;
            default:
            {
                binding.btnNext.setEnabled(false);
                binding.btnNext.setClickable(false);
            }
            break;
        }
    }
}