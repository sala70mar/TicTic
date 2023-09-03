package com.qboxus.tictic.activitesfragments.profile.usersstory;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.viewpager2.widget.ViewPager2;

import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.StoryModel;
import com.qboxus.tictic.models.StoryVideoModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;

public class ViewStoryA extends AppCompatLocaleActivity {

    public static ViewPager2 mPager;
    public StoryPagerAdapter adapter;
    public int selectedPosition;
    ArrayList<StoryModel> storyDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        hideNavigation();
        setContentView(R.layout.activity_view_story);
        Intent intent = getIntent();
        selectedPosition = intent.getIntExtra("position",0);
        storyDataList = (ArrayList<StoryModel>) intent.getSerializableExtra("storyList");


        setupPager();

    }

    private void setupPager() {
        mPager = findViewById(R.id.viewPager);
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        adapter = new StoryPagerAdapter(this, storyDataList,storyDeleteCallback);
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(selectedPosition);
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectedPosition=position;
            }
        });
        mPager.setUserInputEnabled(false);
    }



    FragmentCallBack storyDeleteCallback=new FragmentCallBack() {
        @Override
        public void onResponce(Bundle bundle) {
            if (bundle.getBoolean("isShow",false))
            {
                if (bundle.getString("action").equals("deleteItem"))
                {
                    int itemPostion=bundle.getInt("itemPos",0);
                    StoryModel itemSelected=storyDataList.get(selectedPosition);
                    ArrayList<StoryVideoModel> videoList=itemSelected.getVideoList();
                    if (videoList.size()>0)
                    {
                        videoList.remove(itemPostion);
                        itemSelected.setVideoList(videoList);
                        storyDataList.set(selectedPosition,itemSelected);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        storyDataList.remove(selectedPosition);
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        }
    };


    // this will hide the bottom mobile navigation controll
    public void hideNavigation() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isShow", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}
