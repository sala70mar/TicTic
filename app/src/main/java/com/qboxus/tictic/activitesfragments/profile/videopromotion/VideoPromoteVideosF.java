package com.qboxus.tictic.activitesfragments.profile.videopromotion;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.qboxus.tictic.R;
import com.qboxus.tictic.adapters.VideosPlaylistSelectionAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentVideoPromoteVideosBinding;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.HomeSelectionModel;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class VideoPromoteVideosF extends Fragment {

    FragmentVideoPromoteVideosBinding binding;
    ArrayList<HomeSelectionModel> dataList=new ArrayList<>();
    VideosPlaylistSelectionAdapter adapter;
    GridLayoutManager linearLayoutManager;
    HomeSelectionModel itemUpdate;
    int pageCount = 0;
    boolean ispostFinsh;

    public VideoPromoteVideosF() {
    }

    public static VideoPromoteVideosF newInstance() {
        VideoPromoteVideosF fragment = new VideoPromoteVideosF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_video_promote_videos, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemUpdate!=null && itemUpdate.getModel().video_id!=null)
                {
                    VideoPromoteStepsA.requestPromotionModel.setSelectedVideo(itemUpdate.getModel());

                    int counts=VideoPromoteStepsA.adapter.getItemCount();
                    if (counts>(counts+1))
                    {
                        VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                        VideoPromoteStepsA.progressBar.setProgress((counts),true);
                    }
                    else
                    {
                        VideoPromoteStepsA.adapter.addFrag(VideoPromoteResultF.newInstance());
                        VideoPromoteStepsA.adapter.notifyItemInserted((counts+1));
                        VideoPromoteStepsA.viewpager.setCurrentItem((counts+1),true);
                        VideoPromoteStepsA.progressBar.setProgress((counts),true);
                    }
                }
                else
                {
                    Functions.showToastOnTop(getActivity(),binding.promotionVideoContainer,binding.getRoot().getContext().getString(R.string.must_select_any_video));
                }


            }
        });
    }

    private void initControl() {
        setupRecyclerView();

        pageCount = 0;
        callApiMyvideos();
    }

    private void setupRecyclerView() {
        linearLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), 3);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(linearLayoutManager);
        binding.recylerview.setHasFixedSize(true);


        adapter = new VideosPlaylistSelectionAdapter(binding.getRoot().getContext(), dataList, (view, pos, object) -> {
            itemUpdate = dataList.get(pos);
            for (int i=0;i<dataList.size();i++)
            {
                HomeSelectionModel item=dataList.get(i);
                if (item.getModel().video_id.equals(itemUpdate.getModel().video_id))
                {
                    item.setSelect(true);
                }
                else
                {
                    item.setSelect(false);
                }
                dataList.set(i,item);
            }
            adapter.notifyDataSetChanged();
            updateVideoCount();


        });

        binding.recylerview.setAdapter(adapter);
        binding.recylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems,scrollInItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollInItem=linearLayoutManager.findFirstVisibleItemPosition();
                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                if (scrollInItem == 0)
                {
                    recyclerView.setNestedScrollingEnabled(true);
                }
                else
                {
                    recyclerView.setNestedScrollingEnabled(false);
                }
                if (userScrolled && (scrollOutitems == dataList.size() - 1)) {
                    userScrolled = false;

                    if (binding.loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        binding.loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiMyvideos();
                    }
                }


            }
        });
    }

    Boolean isApiRun = false;
    //this will get the all videos data of user and then parse the data
    private void callApiMyvideos() {
        if (dataList == null)
            dataList = new ArrayList<>();

        isApiRun = true;
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, ""));
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showVideosAgainstUserID, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                isApiRun = false;
                parseData(resp);
            }
        });


    }

    public void parseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                ArrayList<HomeSelectionModel> temp_list = new ArrayList<>();


                JSONArray public_array = msg.optJSONArray("public");


                for (int i = 0; i < public_array.length(); i++) {
                    JSONObject itemdata = public_array.optJSONObject(i);

                    JSONObject video = itemdata.optJSONObject("Video");
                    JSONObject user = itemdata.optJSONObject("User");
                    JSONObject sound = itemdata.optJSONObject("Sound");
                    JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                    JSONObject userPushNotification = user.optJSONObject("PushNotification");

                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);
                    HomeSelectionModel itemModel=new HomeSelectionModel();
                    itemModel.setModel(item);
                    itemModel.setSelect(false);

                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        temp_list.add(itemModel);
                    }
                }

                if (pageCount == 0) {
                    dataList.clear();
                    dataList.addAll(temp_list);
                } else {
                    dataList.addAll(temp_list);
                }

                adapter.notifyDataSetChanged();
            }
            else
            {
                if (pageCount==0)
                {
                    pageCount=0;
                    dataList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            if (dataList.isEmpty()) {
                binding.tabNoData.setVisibility(View.VISIBLE);
            } else {
                binding.tabNoData.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            binding.loadMoreProgress.setVisibility(View.GONE);
        }
    }

    private void updateVideoCount() {
        if (itemUpdate.isSelect())
        {
            binding.btnNext.setEnabled(true);
            binding.btnNext.setClickable(true);
        }
        else
        {
            binding.btnNext.setEnabled(false);
            binding.btnNext.setClickable(false);
        }

    }
}