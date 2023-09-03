package com.qboxus.tictic.activitesfragments.profile;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.qboxus.tictic.activitesfragments.profile.favourite.FavouriteVideosF;
import com.qboxus.tictic.activitesfragments.profile.likedvideos.LikedVideoF;
import com.qboxus.tictic.activitesfragments.search.SearchHashTagsF;
import com.qboxus.tictic.activitesfragments.soundlists.FavouriteSoundF;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.R;


public class FavouriteTabF extends Fragment {

    Context context;
    View view;

    public FavouriteTabF() {
    }

    public static FavouriteTabF newInstance() {
        FavouriteTabF fragment = new FavouriteTabF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_favourite_tab, container, false);

        context =view.getContext();

        setTabs();
        return view;
    }

    // set up the favourite video sound and hastage fragment
    protected TabLayout tabLayout;
    protected ViewPager2 menuPager;
    ViewPagerAdapter adapter;

    public void setTabs() {

        adapter = new ViewPagerAdapter(this);
        menuPager = (ViewPager2) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

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

    private void registerFragmentWithPager() {
        adapter.addFrag(FavouriteVideosF.newInstance());
        adapter.addFrag(FavouriteSoundF.newInstance());
        adapter.addFrag(SearchHashTagsF.newInstance("favourite"));
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
                    tab.setText(context.getString(R.string.hashtags_));
                }

            }
        });
        tabLayoutMediator.attach();
    }




}