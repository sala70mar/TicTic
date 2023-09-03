package com.qboxus.tictic.activitesfragments.profile.usersstory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.StoryModel;

import java.util.ArrayList;

public class StoryPagerAdapter extends FragmentStateAdapter {

    ArrayList<StoryModel> allDataList;
    FragmentCallBack callBack;

    public StoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<StoryModel> allDataList, FragmentCallBack callBack) {
        super(fragmentActivity);
        this.allDataList=allDataList;
        this.callBack=callBack;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        StoryItemF fragment=new StoryItemF(allDataList,position,callBack);
        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return allDataList.size();
    }
}
