package com.qboxus.tictic.activitesfragments.profile.settings;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.profile.analytics.AnalyticsA;
import com.qboxus.tictic.activitesfragments.profile.videopromotion.VideoPromoteStepsA;
import com.qboxus.tictic.databinding.ActivityCreatorToolsBinding;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

public class CreatorToolsA extends AppCompatLocaleActivity {

    ActivityCreatorToolsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_creator_tools);

        initControl();
        actionControl();
    }

    private void actionControl() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.tabAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAnalytics();
            }
        });
        binding.tabPromoteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPromotionHistory();
            }
        });
        binding.tabPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPromotion();
            }
        });
    }

    private void openAnalytics() {
        Intent intent=new Intent(binding.getRoot().getContext(), AnalyticsA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    private void openPromotionHistory() {
        Intent intent=new Intent(binding.getRoot().getContext(), PromotionHistoryA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    private void openPromotion() {
        Intent intent=new Intent(binding.getRoot().getContext(), VideoPromoteStepsA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    private void initControl() {
    }
}