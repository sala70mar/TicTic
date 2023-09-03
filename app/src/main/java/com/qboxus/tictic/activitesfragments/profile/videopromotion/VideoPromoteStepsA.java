package com.qboxus.tictic.activitesfragments.profile.videopromotion;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.ActivityVideoPromoteStepsBinding;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.RequestPromotionModel;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONObject;

public class VideoPromoteStepsA extends AppCompatLocaleActivity {

    public static ViewPagerAdapter adapter;
    public static ViewPager2 viewpager;
    public static ProgressBar progressBar;
    ActivityVideoPromoteStepsBinding binding;
    public static RequestPromotionModel requestPromotionModel;
    HomeModel selectedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_video_promote_steps);

        initControl();
        actionControl();
    }

    private void actionControl() {
        showAdsDetailSticker();
    }

    public void showAdsDetailSticker(){
        JSONObject params=new JSONObject();
        try {
            params.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyRequest.JsonPostRequest(VideoPromoteStepsA.this, ApiLinks.showAddSettings, params,Functions.getHeaders(VideoPromoteStepsA.this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(VideoPromoteStepsA.this,resp);
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");
                    if(code!=null && code.equals("200")){
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        requestPromotionModel.setVideoViewsStat(msgObj.optLong("video_views",0));
                        requestPromotionModel.setWebsiteStat(msgObj.optLong("website_visits",0));
                        requestPromotionModel.setFollowerStat(msgObj.optLong("followers",0));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }


            }
        });

    }


    private void initControl() {
        requestPromotionModel=new RequestPromotionModel();
        if (getIntent().hasExtra("modelData"))
        {
            selectedVideo= (HomeModel) getIntent().getSerializableExtra("modelData");
            requestPromotionModel.setSelectedVideo(selectedVideo);
        }
        else
        {
            requestPromotionModel.setSelectedVideo(null);
        }
        SetTabs();
    }



    public void SetTabs() {
        progressBar=binding.progressBar;
        viewpager=binding.viewpager;
        adapter = new ViewPagerAdapter(this);
        viewpager.setOffscreenPageLimit(1);
        registerFragmentWithPager();
        viewpager.setAdapter(adapter);
        viewpager.setUserInputEnabled(false);
    }

    private void registerFragmentWithPager() {
        adapter.addFrag(VideoPromoteSelectGoalF.newInstance());
    }

    @Override
    public void onBackPressed() {
        int Counts=viewpager.getCurrentItem();
        if (Counts<1)
        {
            super.onBackPressed();
        }
        else
        {
            try {
                adapter.removeFrag(Counts);
                adapter.notifyItemRemoved(Counts);
            }catch (Exception e){Log.d(Constants.tag,"Exception Index: "+e);}
            Counts=Counts-1;
            viewpager.setCurrentItem(Counts,true);
            progressBar.setProgress(Counts);
        }

    }
}