package com.qboxus.tictic.activitesfragments.profile.videopromotion;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentVideoPromoteCustomBinding;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONObject;


public class VideoPromoteCustomF extends Fragment {


    FragmentVideoPromoteCustomBinding binding;

    public VideoPromoteCustomF() {
    }

    public static VideoPromoteCustomF newInstance() {
        VideoPromoteCustomF fragment = new VideoPromoteCustomF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_video_promote_custom, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.tvGenderAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               updateGenderSelection(1);
            }
        });
        binding.tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGenderSelection(2);
            }
        });
        binding.tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGenderSelection(3);
            }
        });

        binding.tvAgeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAgeSelection(1);
            }
        });
        binding.tvFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAgeSelection(2);
            }
        });
        binding.tvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAgeSelection(3);
            }
        });
        binding.tvThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAgeSelection(4);
            }
        });
        binding.tvForth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAgeSelection(5);
            }
        });
        binding.tvFifth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAgeSelection(6);
            }
        });
        binding.tvSixth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAgeSelection(7);
            }
        });


        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(binding.etAudienceName.getText().toString()))
                {
                    Toast.makeText(binding.getRoot().getContext(), binding.getRoot().getContext().getString(R.string.must_enter_audience_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                addAudienceToPromoteUserVideo();
            }
        });

    }

    public void addAudienceToPromoteUserVideo(){
        JSONObject params=new JSONObject();
        try {
            params.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,""));
            params.put("name", ""+binding.etAudienceName.getText().toString());
            params.put("min_age", ""+getMinimumAge(VideoPromoteStepsA.requestPromotionModel.getAge()));
            params.put("max_age", ""+getMaximumAge(VideoPromoteStepsA.requestPromotionModel.getAge()));
            params.put("gender", ""+getGender(VideoPromoteStepsA.requestPromotionModel.getGender()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.addAudience, params,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");
                    if(code!=null && code.equals("200")){
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        JSONObject audienceObj=msgObj.getJSONObject("Audience");
                        VideoPromoteStepsA.requestPromotionModel.setAudienceId(audienceObj.optString("id"));

                        moveToNextStep();
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }


            }
        });

    }

    private void moveToNextStep() {
        int counts=VideoPromoteStepsA.adapter.getItemCount();
        if (counts>(counts+1))
        {
            VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
            VideoPromoteStepsA.progressBar.setProgress((counts),true);
        }
        else
        {
            VideoPromoteStepsA.adapter.addFrag(VideoPromoteStepSelectBudgetF.newInstance());
            VideoPromoteStepsA.adapter.notifyItemInserted((counts+1));
            VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
            VideoPromoteStepsA.progressBar.setProgress((counts),true);
        }
    }

    private String getMaximumAge(int age) {
        if (age==2)
        {
            return "17";
        }
        else
        if (age==3)
        {
            return "24";
        }
        else
        if (age==4)
        {
            return "34";
        }
        else
        if (age==5)
        {
            return "44";
        }
        else
        if (age==6)
        {
            return "54";
        }
        else
        if (age==7)
        {
            return "200";
        }
        else
        {
            return "200";
        }
    }

    private String getMinimumAge(int age) {
        if (age==2)
        {
            return "13";
        }
        else
        if (age==3)
        {
            return "18";
        }
        else
        if (age==4)
        {
            return "25";
        }
        else
        if (age==5)
        {
            return "35";
        }
        else
        if (age==6)
        {
            return "45";
        }
        else
        if (age==7)
        {
            return "55";
        }
        else
        {
            return "1";
        }
    }

    private String getGender(int gender) {
        if (gender==2)
        {
            return "female";
        }
        else
        if (gender==3)
        {
            return "male";
        }
        else
        {
            return "all";
        }
    }

    private void updateAgeSelection(int select) {
        VideoPromoteStepsA.requestPromotionModel.setAge(select);
        switch (select)
        {
            case 1:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 2:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 3:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 4:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 5:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 6:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 7:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
            }
            break;
            default:
            {
                binding.tvAgeAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvAgeAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvFirst.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFirst.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSecond.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSecond.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvThird.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvThird.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvForth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvForth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFifth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFifth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvSixth.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvSixth.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
        }
    }

    private void updateGenderSelection(int select) {
        VideoPromoteStepsA.requestPromotionModel.setGender(select);
        switch (select)
        {
            case 1:
            {
                binding.tvGenderAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvGenderAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvFemale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFemale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvMale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvMale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 2:
            {
                binding.tvGenderAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvGenderAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFemale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvFemale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvMale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvMale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
            break;
            case 3:
            {
                binding.tvGenderAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvGenderAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvFemale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFemale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvMale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvMale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
            }
            break;
            default:
            {
                binding.tvGenderAll.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
                binding.tvGenderAll.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_selection));
                binding.tvFemale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvFemale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
                binding.tvMale.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.black));
                binding.tvMale.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.tab_ractengle_unselection));
            }
        }
    }

    private void initControl() {
        updateAgeSelection(1);
        updateGenderSelection(1);
    }
}