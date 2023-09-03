package com.qboxus.tictic.activitesfragments.soundlists;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;

import android.view.View;

import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

public class SoundListMainA extends AppCompatLocaleActivity implements View.OnClickListener {

    protected TabLayout tablayout;
    Context context;
    protected ViewPager2 pager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_sound_list_main);

        initControl();
        actionControl();
    }

    private void actionControl() {
        findViewById(R.id.goBack).setOnClickListener(this);
    }

    private void initControl() {
        context=SoundListMainA.this;
        SetTabs();
    }


    public void SetTabs() {
        adapter = new ViewPagerAdapter(this);
        pager = (ViewPager2) findViewById(R.id.viewpager);
        tablayout = (TabLayout) findViewById(R.id.groups_tab);

        pager.setOffscreenPageLimit(2);
        registerFragmentWithPager();
        pager.setAdapter(adapter);
        addTabs();

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tablayout.getTabAt(position).select();
            }
        });

    }

    private void addTabs() {
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tablayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0)
                {
                    tab.setText(context.getString(R.string.discover));
                }
                else
                if (position==1)
                {
                    tab.setText(context.getString(R.string.my_fav));
                }
            }
        });
        tabLayoutMediator.attach();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                onBackPressed();
                break;
        }
    }

    private void registerFragmentWithPager() {
        adapter.addFrag(DiscoverSoundListF.newInstance());
        adapter.addFrag(FavouriteSoundF.newInstance());
    }


}
