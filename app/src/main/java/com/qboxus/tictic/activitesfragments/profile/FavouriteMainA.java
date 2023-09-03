package com.qboxus.tictic.activitesfragments.profile;

import com.google.android.material.tabs.TabLayoutMediator;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.qboxus.tictic.activitesfragments.profile.favourite.FavouriteVideosF;
import com.qboxus.tictic.activitesfragments.search.SearchHashTagsF;
import com.qboxus.tictic.activitesfragments.soundlists.FavouriteSoundF;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

public class FavouriteMainA extends AppCompatLocaleActivity {

    Context context;
    protected TabLayout tabLayout;
    protected ViewPager2 menuPager;
    ViewPagerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_favourite_main_);

        initControl();
        actionControl();
    }

    private void actionControl() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouriteMainA.super.onBackPressed();
            }
        });
    }

    private void initControl() {
        context = FavouriteMainA.this;

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

    }


    private void addTabs() {
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, menuPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0)
                {
                    tab.setText(context.getString(R.string.videos));
                }
                else
                if (position==1)
                {
                    tab.setText(context.getString(R.string.sounds));
                }
                else
                if (position==2)
                {
                    tab.setText(context.getString(R.string.hashtag));
                }
            }
        });
        tabLayoutMediator.attach();
    }


    private void registerFragmentWithPager() {
        adapter.addFrag(FavouriteVideosF.newInstance());
        adapter.addFrag(FavouriteSoundF.newInstance());
        adapter.addFrag(SearchHashTagsF.newInstance("favourite"));
    }


}
