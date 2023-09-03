package com.qboxus.tictic.activitesfragments.storyeditors;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.activitesfragments.soundlists.DiscoverSoundListF;
import com.qboxus.tictic.adapters.StoryStickerAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.databinding.FragmentStoryStickersBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class StoryStickersF extends Fragment {


    FragmentCallBack callBack;
    FragmentStoryStickersBinding binding;
    StoryStickerAdapter adapter;
    ArrayList<String> dataList=new ArrayList<>();


    int pageCount = 0;
    boolean ispostFinsh;
    GridLayoutManager linearLayoutManager;

    public StoryStickersF(FragmentCallBack callBack) {
        this.callBack=callBack;
    }

    public StoryStickersF() {
    }


    public static StoryStickersF newInstance(FragmentCallBack callBack) {
        StoryStickersF fragment = new StoryStickersF(callBack);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_story_stickers, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {

    }

    private void initControl() {
        setupAdapter();
    }

    private void setupAdapter() {
        linearLayoutManager=new GridLayoutManager(binding.getRoot().getContext(),4);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(linearLayoutManager);
        adapter=new StoryStickerAdapter(dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                String item=dataList.get(pos);
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                bundle.putString("type","sticker");
                bundle.putString("data",item);
                callBack.onResponce(bundle);
            }
        });
        binding.recylerview.setAdapter(adapter);
        binding.recylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

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

                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                if (userScrolled && (scrollOutitems == dataList.size() - 1)) {
                    userScrolled = false;

                    if (binding.loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        binding.loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        getStickerList();
                    }
                }


            }
        });



    }

    private void getStickerList() {
        if (dataList.isEmpty())
        {
            binding.tabNoData.setVisibility(View.VISIBLE);
        }

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,""));
            parameters.put("type", "0");
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showStickers,  parameters, Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                binding.progressBar.setVisibility(View.GONE);
                ArrayList<String> temp_list = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        JSONArray msgArray = response.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);

                            JSONObject stickerObj = itemdata.optJSONObject("Sticker");

                            String item = stickerObj.optString("image");
                            temp_list.add(item);
                        }



                        if (pageCount == 0) {
                            dataList.clear();
                            dataList.addAll(temp_list);
                        } else {
                            dataList.addAll(temp_list);
                        }
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: comment"+e);
                } finally {
                    binding.loadMoreProgress.setVisibility(View.GONE);
                    getUpdateStatus();
                }
            }
        });

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible)
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    pageCount=0;
                    getStickerList();
                }
            },200);
        }
    }

    private void getUpdateStatus() {
        adapter.notifyDataSetChanged();
        if (dataList.size()>0)
        {
            binding.tabNoData.setVisibility(View.GONE);
        }
        else
        {
            binding.tabNoData.setVisibility(View.VISIBLE);
        }
    }

}