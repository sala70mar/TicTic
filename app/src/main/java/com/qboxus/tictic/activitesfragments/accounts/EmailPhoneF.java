package com.qboxus.tictic.activitesfragments.accounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.models.UserRegisterModel;
import com.qboxus.tictic.R;

// this fragment is a email or phone fragment
public class EmailPhoneF extends Fragment implements View.OnClickListener {
    View view;
    protected TabLayout tabLayout;
    protected ViewPager2 pager;
    private ViewPagerAdapter adapter;
    TextView signupTxt;
    String fromWhere;
    UserRegisterModel userRegisterModel = new UserRegisterModel();

    public EmailPhoneF(String fromWhere) {
        this.fromWhere = fromWhere;
    }

    public EmailPhoneF() {
        //empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        view.findViewById(R.id.goBack).setOnClickListener(this);
    }

    private void initControl() {
        signupTxt = view.findViewById(R.id.signup_txt);
        Bundle bundle = getArguments();
        userRegisterModel = (UserRegisterModel) bundle.getSerializable("user_model");
        if (fromWhere != null && fromWhere != null) {
            if (fromWhere.equals("login")) {
                signupTxt.setText(view.getContext().getString(R.string.login));
            }
        }
        SetTabs();
    }

    public void SetTabs() {
        adapter = new ViewPagerAdapter(this);
        pager = (ViewPager2) view.findViewById(R.id.pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        pager.setOffscreenPageLimit(2);
        registerFragmentWithPager();
        pager.setAdapter(adapter);
        addTabs();

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }


    private void addTabs() {
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0)
                {
                    tab.setText(view.getContext().getString(R.string.phone));
                }
                else
                if (position==1)
                {
                    tab.setText(view.getContext().getString(R.string.email));
                }
            }
        });
        tabLayoutMediator.attach();

    }


    private void registerFragmentWithPager() {
        adapter.addFrag(PhoneF.newInstance(userRegisterModel, fromWhere));
        adapter.addFrag(EmailF.newInstance(userRegisterModel, fromWhere));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goBack:
                getActivity().onBackPressed();
                break;
        }

    }

}