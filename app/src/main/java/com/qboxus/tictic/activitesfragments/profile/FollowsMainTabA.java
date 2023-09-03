package com.qboxus.tictic.activitesfragments.profile;

import com.google.android.material.tabs.TabLayoutMediator;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.qboxus.tictic.activitesfragments.profile.followtabs.FollowerUserF;
import com.qboxus.tictic.activitesfragments.profile.followtabs.FollowingUserF;
import com.qboxus.tictic.activitesfragments.profile.followtabs.SuggestedUserF;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

public class FollowsMainTabA extends AppCompatLocaleActivity {

    Context context;
    TextView tvTitle;
    String userName="",userId="",followerCount="",followingCount="";
    boolean isSelf=false;
    String fromWhere="";

    protected TabLayout tabLayout;
    protected ViewPager2 menuPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(FollowsMainTabA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_follows_main_tab_);

        context = FollowsMainTabA.this;
        tvTitle=findViewById(R.id.tvTitle);

        followingCount=getIntent().getStringExtra("followingCount");
        followerCount=getIntent().getStringExtra("followerCount");
        userId=getIntent().getStringExtra("id");
        userName=getIntent().getStringExtra("userName");
        fromWhere=getIntent().getStringExtra("from_where");
        tvTitle.setText(Functions.showUsername(userName));
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowsMainTabA.super.onBackPressed();
            }
        });

        if (userId==null)
        {
            userId="";
        }

        if (userId.equalsIgnoreCase(Functions.getSharedPreference(context).getString(Variables.U_ID, "")))
        {
            isSelf=true;
        }
        else
        {
            isSelf=false;
        }

        SetTabs();
    }


    public void SetTabs() {
        adapter = new ViewPagerAdapter(this);
        menuPager = (ViewPager2) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        menuPager.setOffscreenPageLimit(3);
        registerFragmentWithPager();
        menuPager.setAdapter(adapter);
        addTabs();

        menuPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        if (fromWhere.equalsIgnoreCase("following"))
        {
            tabLayout.getTabAt(0).select();
        }
        else
        if (fromWhere.equalsIgnoreCase("fan"))
        {
            tabLayout.getTabAt(1).select();
        }
        else
        {
            tabLayout.getTabAt(2).select();
        }

    }

    private void addTabs() {
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, menuPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0)
                {
                    tab.setText(context.getString(R.string.following)+" "+followingCount);
                }
                else
                if (position==1)
                {
                    tab.setText(context.getString(R.string.followers)+" "+followerCount);
                }
                else
                if (position==2)
                {
                    tab.setText(context.getString(R.string.suggested));
                }
            }
        });
        tabLayoutMediator.attach();
    }

    private void registerFragmentWithPager() {
        adapter.addFrag(FollowingUserF.newInstance(userId, userName, isSelf, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                TabLayout.Tab updateTab=tabLayout.getTabAt(0);
                if (bundle.getBoolean("isShow"))
                {
                    int count=Integer.valueOf(followingCount)+1;
                    followingCount=""+count;
                }
                else
                {
                    int count=Integer.valueOf(followingCount)-1;
                    followingCount=""+count;
                }
                isActivityCallback=true;
                updateTab.setText(context.getString(R.string.following)+" "+followingCount);
            }
        }));
        adapter.addFrag(FollowerUserF.newInstance(userId, isSelf, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                TabLayout.Tab updateTab=tabLayout.getTabAt(0);
                if (bundle.getBoolean("isShow"))
                {
                    int count=Integer.valueOf(followingCount)+1;
                    followingCount=""+count;
                }
                else
                {
                    int count=Integer.valueOf(followingCount)-1;
                    followingCount=""+count;
                }
                isActivityCallback=true;
                updateTab.setText(context.getString(R.string.following)+" "+followingCount);
            }
        }));
        adapter.addFrag(SuggestedUserF.newInstance(userId, isSelf, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                TabLayout.Tab updateTab=tabLayout.getTabAt(1);
                if (bundle.getBoolean("isShow"))
                {
                    int count=Integer.valueOf(followerCount)+1;
                    followerCount=""+count;
                }
                else
                {
                    int count=Integer.valueOf(followerCount)-1;
                    followerCount=""+count;
                }
                isActivityCallback=true;
                updateTab.setText(context.getString(R.string.followers)+" "+followerCount);
            }
        }));
    }


    boolean isActivityCallback=false;
    @Override
    public void onBackPressed() {
        if(isActivityCallback)
        {
            Intent intent = new Intent();
            intent.putExtra("isShow", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

}