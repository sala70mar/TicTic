package com.qboxus.tictic.activitesfragments.profile.uservideos;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qboxus.tictic.activitesfragments.profile.ProfileTabF;
import com.qboxus.tictic.activitesfragments.profile.creatorplaylist.CreatePlaylistA;
import com.qboxus.tictic.activitesfragments.WatchVideosA;
import com.qboxus.tictic.adapters.PlaylistTitleAdapter;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.adapters.MyVideosAdapter;
import com.qboxus.tictic.models.PlaylistTitleModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.simpleclasses.SpacesItemDecoration;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserVideoF extends Fragment {

    RecyclerView recyclerView;
    ArrayList<HomeModel> dataList;
    MyVideosAdapter adapter;
    View view;
    Context context;
    TextView tvTitleNoData,tvMessageNoData;
    RelativeLayout noDataLayout;
    NewVideoBroadCast mReceiver;

    public RelativeLayout tabCreatePlaylist;
    public ImageView ivClosePlaylist;

    RecyclerView playlistRecyclerview;
    PlaylistTitleAdapter playlistTitleAdapter;
    ArrayList<PlaylistTitleModel> playlistList=new ArrayList<>();


    int pageCount = 0;
    boolean ispostFinsh;
    ProgressBar loadMoreProgress;
    GridLayoutManager linearLayoutManager;

    String isUserAlreadyBlock="0";
    String userId="",userName="";
    boolean is_my_profile = true;


    public UserVideoF() {

    }

    public UserVideoF(boolean is_my_profile, String userId, String userName,String isUserAlreadyBlock) {
        this.is_my_profile = is_my_profile;
        this.userId = userId;
        this.userName=userName;
        this.isUserAlreadyBlock=isUserAlreadyBlock;
    }

    public static UserVideoF newInstance(boolean is_my_profile, String userId, String userName,String isUserAlreadyBlock) {
        UserVideoF fragment = new UserVideoF(is_my_profile,userId,userName,isUserAlreadyBlock);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    private class NewVideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Variables.reloadMyVideosInner = false;
            pageCount = 0;
            callApiMyvideos(true);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_video, container, false);

        context = getContext();

        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        recyclerView = view.findViewById(R.id.recylerview);
        linearLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        dataList = new ArrayList<>();
        adapter = new MyVideosAdapter(context, dataList, "myProfile", (view, pos, object) -> {
            HomeModel item = (HomeModel) object;
            openWatchVideo(pos);

        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiMyvideos(false);
                    }
                }


            }
        });


        ivClosePlaylist=view.findViewById(R.id.ivClosePlaylist);
        ivClosePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabCreatePlaylist.setVisibility(View.GONE);
            }
        });
        tabCreatePlaylist=view.findViewById(R.id.tabCreatePlaylist);
        tabCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), CreatePlaylistA.class);
                resultInfoAgainCallback.launch(intent);
            }
        });
        noDataLayout = view.findViewById(R.id.no_data_layout);

        setupPlaylistRecyclerbview();

        tvTitleNoData=view.findViewById(R.id.tvTitleNoData);
        tvMessageNoData=view.findViewById(R.id.tvMessageNoData);

        mReceiver = new NewVideoBroadCast();
        getActivity().registerReceiver(mReceiver, new IntentFilter("newVideo"));

        return view;
    }


    private void setupPlaylistRecyclerbview() {
        playlistRecyclerview=view.findViewById(R.id.playlistRecyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        playlistRecyclerview.setLayoutManager(layoutManager);
        int spacingInPixels = view.getContext().getResources().getDimensionPixelSize(R.dimen._6sdp);
        playlistRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        playlistTitleAdapter=new PlaylistTitleAdapter(playlistList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                PlaylistTitleModel itemUpdate=playlistList.get(pos);
                if (itemUpdate.getId().equals("0"))
                {
                    Intent intent=new Intent(view.getContext(), CreatePlaylistA.class);
                    resultInfoAgainCallback.launch(intent);
                }
                else
                {
                    openPlaylistVideo(itemUpdate.getId(),itemUpdate.getName());
                }
            }
        });
        playlistRecyclerview.setAdapter(playlistTitleAdapter);

    }

    FragmentCallBack callBackForDetailRefresh;
    public void updateUserPlaylist(JSONArray playlistArray, String verifiedId, FragmentCallBack callBackForDetailRefresh) {
        this.callBackForDetailRefresh=callBackForDetailRefresh;
        try {
            playlistList.clear();
            if (playlistArray.length()>0)
            {
                if (userId.equals(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
                {
                    PlaylistTitleModel model=new PlaylistTitleModel();
                    model.setId("0");
                    model.setName("");
                    playlistList.add(model);
                }
            }

            for (int i=0;i<playlistArray.length();i++)
            {
                JSONObject object=playlistArray.getJSONObject(i);
                PlaylistTitleModel model=new PlaylistTitleModel();
                model.setId(object.optString("id"));
                model.setName(object.optString("name"));
                playlistList.add(model);
            }
            playlistTitleAdapter.notifyDataSetChanged();

            if (playlistList.size()>0)
            {
                tabCreatePlaylist.setVisibility(View.GONE);
                playlistRecyclerview.setVisibility(View.VISIBLE);
            }
            else
            {

                if (userId.equals(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
                {
                    if (verifiedId.equals("1"))
                    {
                        tabCreatePlaylist.setVisibility(View.VISIBLE);
                    }
                }
                playlistRecyclerview.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception : "+e);
        }

    }



    ActivityResultLauncher<Intent> resultInfoAgainCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            if (callBackForDetailRefresh!=null)
                            {
                                Bundle bundle=new Bundle();
                                bundle.putBoolean("isShow",data.getBooleanExtra("isShow",false));
                                callBackForDetailRefresh.onResponce(bundle);
                            }
                        }
                    }
                }
            });



    public void updatePlaylistCreate() {
        if (userId.equals(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
        {
            tabCreatePlaylist.setVisibility(View.VISIBLE);
        }
        else
        {
            tabCreatePlaylist.setVisibility(View.GONE);
        }
    }


    // open the videos in full screen on click
    private void openPlaylistVideo(String id,String playlistName) {

        Intent intent = new Intent(getActivity(), WatchVideosA.class);
        intent.putExtra("playlist_id", id);
        intent.putExtra("position", 0);
        intent.putExtra("pageCount", pageCount);
        intent.putExtra("userId",userId);
        intent.putExtra("playlistName",playlistName);
        intent.putExtra("whereFrom","playlistVideo");
        resultInfoAgainCallback.launch(intent);
    }



    private void setNoData() {
        if (is_my_profile)
        {
            tvTitleNoData.setVisibility(View.GONE);
            tvMessageNoData.setVisibility(View.GONE);
        }
        else
        {
            tvTitleNoData.setVisibility(View.GONE);
            tvMessageNoData.setVisibility(View.VISIBLE);
            tvMessageNoData.setText(view.getContext().getString(R.string.this_user_has_not_publish_any_video));
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                pageCount = 0;
                callApiMyvideos(true);
            }, 200);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    Boolean isApiRun = false;

    //this will get the all videos data of user and then parse the data
    private void callApiMyvideos(boolean isScrollToTop) {
        if (isUserAlreadyBlock.equalsIgnoreCase("1"))
        {
            tvTitleNoData.setText(view.getContext().getString(R.string.alert));
            tvMessageNoData.setText(view.getContext().getString(R.string.you_are_block_by)+" "+userName);
        }
        else
        {
            setNoData();
        }

        if (dataList == null)
            dataList = new ArrayList<>();

        isApiRun = true;
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));

            if (!is_my_profile) {
                parameters.put("other_user_id", userId);
            }
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showVideosAgainstUserID, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                isApiRun = false;
                parseData(resp,isScrollToTop);
            }
        });


    }

    public void parseData(String responce,boolean isScrollToTop) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                ArrayList<HomeModel> temp_list = new ArrayList<>();
                JSONArray public_array = msg.optJSONArray("public");

                HashMap<String,HomeModel> pinnedVideo=new HashMap<>();
                for (int i = 0; i < public_array.length(); i++) {
                    JSONObject itemdata = public_array.optJSONObject(i);

                    JSONObject video = itemdata.optJSONObject("Video");
                    JSONObject user = itemdata.optJSONObject("User");
                    JSONObject sound = itemdata.optJSONObject("Sound");
                    JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                    JSONObject userPushNotification = user.optJSONObject("PushNotification");

                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);

                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        if (item.pin.equals("1"))
                        {
                            pinnedVideo.put(item.video_id,item);
                        }
                        else
                        {
                            temp_list.add(item);
                        }
                    }

                }
                Paper.book("PinnedVideo").write("pinnedVideo",pinnedVideo);

                if (pageCount == 0) {
                    dataList.clear();
                    dataList.addAll(temp_list);
                } else {
                    dataList.addAll(temp_list);
                }

                for (String key:pinnedVideo.keySet())
                {
                    HomeModel itemModel=pinnedVideo.get(key);
                    dataList.add(0,itemModel);
                }
                if (isScrollToTop)
                {
                    recyclerView.smoothScrollToPosition(0);
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
                view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.no_data_layout).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        } finally {
            loadMoreProgress.setVisibility(View.GONE);
        }
    }

    public void updateUserData(String userId,String userName,String isUserAlreadyBlock)
    {
        pageCount = 0;
        this.userId=userId;
        this.userName=userName;
        this.isUserAlreadyBlock=isUserAlreadyBlock;
        callApiMyvideos(true);
    }


    // open the videos in full screen on click
    private void openWatchVideo(int postion) {

        Intent intent = new Intent(getActivity(), WatchVideosA.class);
        intent.putExtra("arraylist", dataList);
        intent.putExtra("position", postion);
        intent.putExtra("pageCount", pageCount);
        intent.putExtra("userId",userId);
        intent.putExtra("whereFrom","userVideo");
        resultCallback.launch(intent);
    }

    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            if (Paper.book("pinnedRefresh").contains("refresh"))
                            {
                                Paper.book("pinnedRefresh").destroy();
                                pageCount = 0;
                                callApiMyvideos(true);
                            }
                            else
                            {
                                dataList.clear();
                                dataList.addAll((ArrayList<HomeModel>) data.getSerializableExtra("arraylist"));
                                pageCount=data.getIntExtra("pageCount",0);
                                adapter.notifyDataSetChanged();

                            }

                        }
                    }
                }
            });


}
