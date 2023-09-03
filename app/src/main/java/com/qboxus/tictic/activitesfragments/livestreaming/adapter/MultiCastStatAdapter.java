package com.qboxus.tictic.activitesfragments.livestreaming.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.qboxus.tictic.activitesfragments.livestreaming.fragments.MultipleStreamerListF;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.activitesfragments.livestreaming.activities.MultiViewLiveA;

import java.util.ArrayList;


public class MultiCastStatAdapter extends FragmentStatePagerAdapter {

    private static int PAGE_REFRESH_STATE= PagerAdapter.POSITION_UNCHANGED;
    ArrayList<LiveUserModel> dataList = new ArrayList<>();
    MultiViewLiveA activity;

    public MultiCastStatAdapter(@NonNull FragmentManager fm,ArrayList<LiveUserModel> dataList, MultiViewLiveA activity) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.dataList=dataList;
        this.activity=activity;

    }

    public void refreshStateSet(boolean isRefresh) {
        if (isRefresh)
        {
            PAGE_REFRESH_STATE=PagerAdapter.POSITION_NONE;
        }
        else
        {
            PAGE_REFRESH_STATE=PagerAdapter.POSITION_UNCHANGED;
        }
    }


    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PAGE_REFRESH_STATE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        LiveUserModel item=dataList.get(position);
        MultipleStreamerListF fragment = new MultipleStreamerListF( item, activity);
        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }
}