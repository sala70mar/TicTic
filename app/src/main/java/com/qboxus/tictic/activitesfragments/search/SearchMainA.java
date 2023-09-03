package com.qboxus.tictic.activitesfragments.search;


import com.google.android.material.tabs.TabLayoutMediator;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.qboxus.tictic.adapters.RecentSearchAdapter;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;
import java.util.ArrayList;

import io.paperdb.Paper;

public class SearchMainA extends AppCompatLocaleActivity implements View.OnClickListener {


    Context context;
    public static EditText searchEdit;
    TextView search_btn;
    protected TabLayout tabLayout;
    protected ViewPager2 menuPager;
    ViewPagerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(SearchMainA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_search_main);
        context =SearchMainA.this;

        searchEdit = findViewById(R.id.search_edit);

        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);


        showRecentSearch();

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchEdit.getText().toString().length() > 0) {
                    search_btn.setVisibility(View.VISIBLE);

                } else {
                    search_btn.setVisibility(View.GONE);
                }

                showRecentSearch();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEdit.setFocusable(true);
        UIUtil.showKeyboard(context, searchEdit);


        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();

                    findViewById(R.id.recent_layout).setVisibility(View.GONE);
                    addSearchKey(searchEdit.getText().toString());

                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.clear_all_txt).setOnClickListener(this);
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
                    tab.setText(context.getString(R.string.users));
                }
                else
                if (position==1)
                {
                    tab.setText(context.getString(R.string.videos));
                }
                else
                if (position==2)
                {
                    tab.setText(context.getString(R.string.sounds));
                }
                else
                if (position==3)
                {
                    tab.setText(context.getString(R.string.hashtags_));
                }
            }
        });
        tabLayoutMediator.attach();
    }


    private void registerFragmentWithPager() {
        adapter.addFrag(SearchUserF.newInstance("user"));
        adapter.addFrag(SearchVideoF.newInstance("video"));
        adapter.addFrag(SearchSoundF.newInstance("sound"));
        adapter.addFrag(SearchHashTagsF.newInstance("hashtag"));
    }








    public void addSearchKey(String search_key) {
        ArrayList<String> search_list = (ArrayList<String>) Paper.book("Search").read("RecentSearch", new ArrayList<String>());
        search_list.add(search_key);
        Paper.book("Search").write("RecentSearch", search_list);
    }


    // this method will get the recent searched list from local db

    RecyclerView recyclerView;
    RecentSearchAdapter recentsearchAdapter;
    ArrayList<String> searchQueryList = new ArrayList<>();

    public void showRecentSearch() {
        ArrayList<String> search_list = (ArrayList<String>) Paper.book("Search").read("RecentSearch", new ArrayList<String>());

        try {
            searchQueryList.clear();
            if (search_list!=null && search_list.size()>0)
            {
                searchQueryList.addAll(search_list);
            }
        }
        catch (Exception e){
            Log.d(Constants.tag,"Exception: "+e);
        }

        if (searchQueryList.isEmpty()) {
            findViewById(R.id.recent_layout).setVisibility(View.GONE);
            return;
        } else {
            findViewById(R.id.recent_layout).setVisibility(View.VISIBLE);
        }


        findViewById(R.id.recent_layout).setVisibility(View.VISIBLE);
        recentsearchAdapter = new RecentSearchAdapter(context, searchQueryList, new AdapterClickListener() {
            @Override
            public void onItemClick(View v, int pos, Object object) {

                if (v.getId() == R.id.delete_btn) {
                    searchQueryList.remove(object);
                    recentsearchAdapter.notifyDataSetChanged();

                    search_list.remove(object);
                    Paper.book("Search").write("RecentSearch", search_list);
                } else {

                    String search = (String) object;
                    searchEdit.setText(search);
                    performSearch();
                    findViewById(R.id.recent_layout).setVisibility(View.GONE);
                }

            }
        });
        recyclerView = findViewById(R.id.recylerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recentsearchAdapter);

        if (recentsearchAdapter != null) {
            FilterList(""+searchEdit.getText().toString());
            return;
        }

    }

    private void FilterList(CharSequence s) {
        try {
            ArrayList<String> filter_list=new ArrayList<>();
            for (String model:searchQueryList)
            {
                if (model.toLowerCase().contains(s.toString().toLowerCase()))
                {
                    filter_list.add(model);
                }
            }

            if (filter_list.size()>0)
            {
                recentsearchAdapter.filter(filter_list);
            }

        }
        catch (Exception e)
        {
            Functions.printLog(Constants.tag,"Error : "+e);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.search_btn:
                Functions.hideSoftKeyboard(SearchMainA.this);
                performSearch();
                findViewById(R.id.recent_layout).setVisibility(View.GONE);
                addSearchKey(searchEdit.getText().toString());
                break;

            case R.id.clear_all_txt:
                Paper.book("Search").delete("RecentSearch");
                showRecentSearch();
                break;


        }
    }

    private void performSearch() {
        SetTabs();
    }


    @Override
    public void onBackPressed() {
        finish();
       overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
