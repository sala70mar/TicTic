package com.qboxus.tictic.activitesfragments.spaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.models.TopicModel;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.ActivityInterestBinding;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class InterestPreferenceA extends AppCompatLocaleActivity implements View.OnClickListener{

    ActivityInterestBinding binding;
    ArrayList<TopicModel> selectedTopic=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        binding = DataBindingUtil.setContentView(InterestPreferenceA.this,
                R.layout.activity_interest);
        InitControl();
    }


    private void InitControl() {
        binding.ivBack.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.ivBack);

        binding.saveBtn.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.saveBtn);

        getTopicsCategoryLists();
    }




     ArrayList<TopicModel> topicModels = new ArrayList<>();

    private void getTopicsCategoryLists() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id",Functions.getSharedPreference(this).getString(Variables.U_ID,""));
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }

        VolleyRequest.JsonPostRequest(this, ApiLinks.showTopics, parameters, Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                parseResponseData(resp);
            }
        });

    }

    private void parseResponseData(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                topicModels.clear();

                JSONArray msgArray=jsonObject.getJSONArray("msg");
                 for (int i=0;i<msgArray.length();i++)
                {
                    JSONObject innerObject=msgArray.getJSONObject(i);

                    JSONObject topic=innerObject.optJSONObject("Topic");
                    TopicModel model= DataParsing.getTopicDataModel(topic);
                    topicModels.add(model);
                }

            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : parseResponseData "+e);
        }
        finally {
            binding.progressBar.setVisibility(View.GONE);

            if (topicModels.isEmpty()) {
                binding.tabNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(binding.getRoot().getContext().getString(R.string.no_topic_found));
            } else {
                binding.tabNoData.setVisibility(View.GONE);
                populateDataList(topicModels);
            }
        }
    }

    private void populateDataList(ArrayList<TopicModel> listData) {
        for (int i=0;i<listData.size();i++)
        {
            TopicModel itemModel=listData.get(i);

            RelativeLayout tabTag = (RelativeLayout) LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.item_topic, null);
            LinearLayout innerView=tabTag.findViewById(R.id.innerView);
            SimpleDraweeView ivTag=innerView.findViewById(R.id.ivTag);
            View ivFrameTag=innerView.findViewById(R.id.ivFrameTag);
            TextView tvTag=innerView.findViewById(R.id.tvTag);
            tvTag.setText(""+itemModel.getTitle());

            tabTag.setTag(i);
            ivTag.setController(Functions.frescoImageLoad(binding.getRoot().getContext(),
                    ""+itemModel.getTitle(),
                    (int) binding.getRoot().getContext().getResources().getDimension(R.dimen._9sdp)
                    ,itemModel.getImage(),ivTag));


                tvTag.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),
                        R.color.black));
                tabTag.setActivated(false);
                ivFrameTag.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.lightgraycolor));


            tabTag.setOnClickListener(v -> {

                if(selectedTopic.contains(itemModel)){
                    selectedTopic.remove(itemModel);
                    tvTag.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),
                            R.color.black));
                    tabTag.setActivated(false);
                    ivFrameTag.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.lightgraycolor));

                }
                else if(selectedTopic.size()<1) {
                    selectedTopic.add(itemModel);
                    tvTag.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),
                            R.color.white));
                    tabTag.setActivated(true);
                    ivFrameTag.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.appColor));

                }
                binding.countTxt.setText(""+selectedTopic.size());

//                if (tabTag.isActivated())
//                {
//                                }
//                else
//                {
//                                  }

            });
            binding.chipGroup.addView(tabTag);
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivBack:
            {
               finish();
            }
            break;

            case R.id.saveBtn:
            {
                Intent bundle=new Intent();
                bundle.putExtra("isShow",true);
                bundle.putExtra("dataList",selectedTopic);
                setResult(RESULT_OK,bundle);
                finish();
            }
            break;
        }
    }

}