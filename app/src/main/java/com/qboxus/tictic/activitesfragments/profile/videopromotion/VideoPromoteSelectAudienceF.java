package com.qboxus.tictic.activitesfragments.profile.videopromotion;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.soundlists.DiscoverSoundListF;
import com.qboxus.tictic.adapters.PromotionAudiencesAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentVideoPromoteSelectAudienceBinding;
import com.qboxus.tictic.models.PromotionAudiencesModel;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class VideoPromoteSelectAudienceF extends Fragment {

    FragmentVideoPromoteSelectAudienceBinding binding;
    ArrayList<PromotionAudiencesModel> dataList=new ArrayList<>();
    PromotionAudiencesAdapter adapter;
    PromotionAudiencesModel itemUpdate;


    public VideoPromoteSelectAudienceF() {
    }

    public static VideoPromoteSelectAudienceF newInstance() {
        VideoPromoteSelectAudienceF fragment = new VideoPromoteSelectAudienceF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_video_promote_select_audience, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VideoPromoteStepsA.requestPromotionModel.getAudienceType()==2)
                {

                    int counts=VideoPromoteStepsA.adapter.getItemCount();
                    if (VideoPromoteStepsA.requestPromotionModel.getPromoteGoal()==2)
                    {
                        VideoPromoteStepsA.progressBar.setMax(6);
                    }
                    else
                    {
                        VideoPromoteStepsA.progressBar.setMax(5);
                    }

                    if (VideoPromoteStepsA.requestPromotionModel.getSelectedVideo()==null)
                    {
                        int progressCount=VideoPromoteStepsA.progressBar.getMax()+1;
                        VideoPromoteStepsA.progressBar.setMax(progressCount);
                    }

                    if (counts>(counts+1))
                    {
                        VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                        VideoPromoteStepsA.progressBar.setProgress((counts),true);
                    }
                    else
                    {
                        VideoPromoteStepsA.adapter.addFrag(VideoPromoteCustomF.newInstance());
                        VideoPromoteStepsA.adapter.notifyItemInserted((counts+1));
                        VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                        VideoPromoteStepsA.progressBar.setProgress((counts),true);
                    }
                }
                else
                {
                    int counts=VideoPromoteStepsA.adapter.getItemCount();
                    VideoPromoteStepsA.progressBar.setMax(4);

                    if (VideoPromoteStepsA.requestPromotionModel.getSelectedVideo()==null)
                    {
                        int progressCount=VideoPromoteStepsA.progressBar.getMax()+1;
                        VideoPromoteStepsA.progressBar.setMax(progressCount);
                    }

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
            }
        });
    }

    private void initControl() {
        setupRecyclerView();
        callApiMyAudience();
    }

    private void callApiMyAudience() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showAudiences, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                parseData(resp);
            }
        });
    }

    public void parseData(String responce) {
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                dataList.clear();

                PromotionAudiencesModel automaticModel=new PromotionAudiencesModel();
                automaticModel.setId("");
                automaticModel.setName(binding.getRoot().getContext().getString(R.string.automatic_app_chooses_for_you_));
                automaticModel.setMin_age("");
                automaticModel.setMax_age("");
                automaticModel.setGender("");
                automaticModel.setSelected(false);
                dataList.add(automaticModel);

                PromotionAudiencesModel customModel=new PromotionAudiencesModel();
                customModel.setId("0");
                customModel.setName(binding.getRoot().getContext().getString(R.string.custom));
                customModel.setMin_age("");
                customModel.setMax_age("");
                customModel.setGender("");
                customModel.setSelected(false);
                dataList.add(customModel);

                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i).getJSONObject("Audience");

                    PromotionAudiencesModel model=new PromotionAudiencesModel();
                    model.setId(itemdata.optString("id"));
                    model.setName(itemdata.optString("name"));
                    model.setMin_age(itemdata.optString("min_age"));
                    model.setMax_age(itemdata.optString("max_age"));
                    model.setGender(itemdata.optString("gender"));
                    model.setSelected(false);

                    dataList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }


    private void setupRecyclerView() {
        LinearLayoutManager layoutManager= new LinearLayoutManager(binding.getRoot().getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(layoutManager);
        binding.recylerview.setHasFixedSize(true);
        adapter = new PromotionAudiencesAdapter(binding.getRoot().getContext(), dataList, (view, pos, object) -> {
            itemUpdate = dataList.get(pos);
            for (int i=0;i<dataList.size();i++)
            {
                PromotionAudiencesModel item=dataList.get(i);
                item.setSelected(false);
                dataList.set(i,item);
            }
            itemUpdate.setSelected(true);
            dataList.set(pos,itemUpdate);
            UpdateButtonStatus();
            adapter.notifyDataSetChanged();


        });
        binding.recylerview.setAdapter(adapter);
    }

    private void UpdateButtonStatus() {
        if (itemUpdate.getId().equals(""))
        {
            VideoPromoteStepsA.requestPromotionModel.setAudienceType(1);
            VideoPromoteStepsA.requestPromotionModel.setSelectAudience(null);
            binding.btnNext.setEnabled(true);
            binding.btnNext.setClickable(true);
        }
        else
        if (itemUpdate.getId().equals("0"))
        {
            VideoPromoteStepsA.requestPromotionModel.setAudienceType(2);
            VideoPromoteStepsA.requestPromotionModel.setSelectAudience(null);
            binding.btnNext.setEnabled(true);
            binding.btnNext.setClickable(true);
        }
        else
        {
            VideoPromoteStepsA.requestPromotionModel.setAudienceType(3);
            VideoPromoteStepsA.requestPromotionModel.setSelectAudience(itemUpdate);
            binding.btnNext.setEnabled(true);
            binding.btnNext.setClickable(true);
        }
    }

}