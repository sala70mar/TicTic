package com.qboxus.tictic.activitesfragments;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.qboxus.tictic.activitesfragments.profile.creatorplaylist.CreatePlaylistStepTwoF;
import com.qboxus.tictic.activitesfragments.profile.creatorplaylist.ShowPlaylistF;
import com.qboxus.tictic.models.CreatePlaylistModel;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;

import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qboxus.tictic.adapters.ViewPagerStatAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.simpleclasses.DebounceClickHandler;
import com.qboxus.tictic.simpleclasses.VerticalViewPager;
import com.volley.plus.VPackages.VolleyRequest;
import com.qboxus.tictic.Constants;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.services.UploadService;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;


public class WatchVideosA extends AppCompatLocaleActivity implements FragmentCallBack {


    Context context;
    ArrayList<HomeModel> dataList=new ArrayList<>();
    String playlistName="";
    SwipeRefreshLayout swiperefresh;
    int pageCount = 0;
    boolean isApiRuning = false;
    Handler handler;
    RelativeLayout uploadVideoLayout;
    ImageView uploadingThumb;
    UploadingVideoBroadCast mReceiver;
    String whereFrom="";
    int currentPositon=0;
    TextView tvPlaylistTitle;
    RelativeLayout tabPlaylist;
    View tabSneekbarView;
    String userId="";
    HashMap<String,HomeModel> playlistMapList=new HashMap<>();
    ImageView ivEditPlaylist;
    private static ProgressBar progressBar;
    private static TextView tvProgressCount;

    //this is use for use same class functionality from different activities
    int fragmentConainerId;


    @Override
    public void onResponce(Bundle bundle) {
        if (bundle.getString("action").equalsIgnoreCase("deleteVideo"))
        {
            dataList.remove(bundle.getInt("position"));
            Log.d(Constants.tag,"notify data : "+dataList.size());
            if (dataList.size()==0)
            {
                onBackPressed();
            }
        }
        else
        if (bundle.getString("action").equalsIgnoreCase("pinned"))
        {
            Paper.book("pinnedRefresh").write("refresh",true);
            HomeModel itemUpdate=dataList.get(bundle.getInt("position"));
            itemUpdate.pin=bundle.getString("pin","0");
            dataList.set(bundle.getInt("position"),itemUpdate);
            pagerSatetAdapter.refreshStateSet(false);
            pagerSatetAdapter.notifyDataSetChanged();
        }
    }


    private class UploadingVideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Functions.isMyServiceRunning(context, UploadService.class)) {
                uploadVideoLayout.setVisibility(View.VISIBLE);
                Bitmap bitmap = Functions.base64ToBitmap(Functions.getSharedPreference(context).getString(Variables.UPLOADING_VIDEO_THUMB, ""));
                if (bitmap != null)
                    uploadingThumb.setImageBitmap(bitmap);

            } else {
                uploadVideoLayout.setVisibility(View.GONE);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try { getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); }catch (Exception e){}
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_watch_videos);
        fragmentConainerId=R.id.watchVideo_F;
        context = WatchVideosA.this;
        tabSneekbarView=findViewById(R.id.tabSneekbarView);
        tabPlaylist=findViewById(R.id.tabPlaylist);
        ivEditPlaylist=findViewById(R.id.ivEditPlaylist);
        tvPlaylistTitle=findViewById(R.id.tvPlaylistTitle);
        tvProgressCount=findViewById(R.id.tvProgressCount);
        progressBar=findViewById(R.id.progressBar);
        tabPlaylist.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaylist();
            }
        }));
        ivEditPlaylist.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlaylistSetting();
            }
        }));
        whereFrom=getIntent().getStringExtra("whereFrom");
        userId=getIntent().getStringExtra("userId");
        pageCount=getIntent().getIntExtra("pageCount",0);
        currentPositon=getIntent().getIntExtra("position",0);
        if (whereFrom.equalsIgnoreCase("IdVideo"))
        {
            dataList.clear();
            callApiForSinglevideos(getIntent().getStringExtra("video_id"),true);
            tabPlaylist.setVisibility(View.GONE);
            tabSneekbarView.setVisibility(View.GONE);
            ivEditPlaylist.setVisibility(View.GONE);
        }
        else
        if (whereFrom.equalsIgnoreCase("discoverTagedVideo"))
        {
            dataList.clear();
            callApiForTagedVideos();
            tabPlaylist.setVisibility(View.GONE);
            tabSneekbarView.setVisibility(View.GONE);
            ivEditPlaylist.setVisibility(View.GONE);
        }
        else
        if (whereFrom.equalsIgnoreCase("playlistVideo"))
        {
            dataList.clear();
            playlistName=getIntent().getStringExtra("playlistName");
            callApiForPlaylistVideos(getIntent().getStringExtra("playlist_id"),true);
            tabPlaylist.setVisibility(View.VISIBLE);
            tabSneekbarView.setVisibility(View.VISIBLE);
            tvPlaylistTitle.setText(context.getString(R.string.playlist)+" . "+playlistName);

        }
        else
        {
            ArrayList<HomeModel> arrayList= (ArrayList<HomeModel>) getIntent().getSerializableExtra("arraylist");
            dataList.clear();
            dataList.addAll(arrayList);
            if (dataList.get(currentPositon).playlistId.equals("0"))
            {
                tabPlaylist.setVisibility(View.GONE);
                tabSneekbarView.setVisibility(View.GONE);
            }
            else
            {
                tabPlaylist.setVisibility(View.VISIBLE);
                tabSneekbarView.setVisibility(View.VISIBLE);
                tvPlaylistTitle.setText(context.getString(R.string.playlist)+" . "+dataList.get(currentPositon).playlistName);
            }
            if(dataList.get(currentPositon).video_user_id.equals(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
            {
                if (dataList.get(currentPositon).playlistId.equals("0"))
                {
                    ivEditPlaylist.setVisibility(View.GONE);
                }
                else
                {
                    ivEditPlaylist.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                ivEditPlaylist.setVisibility(View.GONE);
            }
        }

        handler = new Handler(Looper.getMainLooper());
        findViewById(R.id.goBack).setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        }));
        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setProgressViewOffset(false, 0, 200);

        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPositon=0;
                pageCount = 0;
                dataList.clear();
                callVideoApi();
            }
        });


        uploadVideoLayout = findViewById(R.id.upload_video_layout);
        uploadingThumb = findViewById(R.id.uploading_thumb);
        mReceiver = new UploadingVideoBroadCast();
        registerReceiver(mReceiver, new IntentFilter("uploadVideo"));


        if (Functions.isMyServiceRunning(context, UploadService.class)) {
            uploadVideoLayout.setVisibility(View.VISIBLE);
            Bitmap bitmap = Functions.base64ToBitmap(Functions.getSharedPreference(context).getString(Variables.UPLOADING_VIDEO_THUMB, ""));
            if (bitmap != null)
                uploadingThumb.setImageBitmap(bitmap);
        }

        setTabs(false);
        setUpPreviousScreenData();
    }




    private void setUpPreviousScreenData() {
        for (HomeModel item : dataList) {
            pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager, this,fragmentConainerId));
        }
        pagerSatetAdapter.refreshStateSet(false);
        pagerSatetAdapter.notifyDataSetChanged();

        menuPager.setCurrentItem(currentPositon,true);
    }


    public static FragmentCallBack uploadingCallback=new FragmentCallBack() {
        @Override
        public void onResponce(Bundle bundle) {
            if (bundle.getBoolean("isShow"))
            {
                int currentProgress=bundle.getInt("currentpercent",0);
                if (progressBar!=null && tvProgressCount!=null)
                {

                    progressBar.setProgress(currentProgress);
                    tvProgressCount.setText(currentProgress+"%");
                }
            }
        }
    };


    // set the fragments for all the videos list
    protected VerticalViewPager menuPager;
    ViewPagerStatAdapter pagerSatetAdapter;


    public void setTabs(boolean isListSet) {

        if (isListSet)
        {
            dataList.clear();
        }


        pagerSatetAdapter = new ViewPagerStatAdapter(getSupportFragmentManager(), menuPager, false, this);
        menuPager =  findViewById(R.id.viewpager);
        menuPager.setAdapter(pagerSatetAdapter);
        menuPager.setOffscreenPageLimit(1);
        menuPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0)
                {
                    swiperefresh.setEnabled(true);
                }
                else
                {
                    swiperefresh.setEnabled(false);
                }
                if (position == 0 && (pagerSatetAdapter !=null && pagerSatetAdapter.getCount()>0)) {
                    VideosListF fragment = (VideosListF) pagerSatetAdapter.getItem(menuPager.getCurrentItem());
                    fragment.setData();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.setPlayer(true);
                        }
                    }, 200);

                }
                currentPositon=menuPager.getCurrentItem();
                setupPlaylist();
                Log.d(Constants.tag,"Check : check "+(position+1)+"    "+(dataList.size()-1)+"      "+(dataList.size() > 2 && (dataList.size() - 1) == position));
                Log.d(Constants.tag,"Test : Test "+(position+1)+"    "+(dataList.size()-5)+"      "+(dataList.size() > 5 && (dataList.size() - 5) == (position+1)));
                if (dataList.size() > 5 && (dataList.size() -5) == (position+1)) {
                    if(!isApiRuning) {
                        pageCount++;
                        callVideoApi();
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupPlaylist() {
        if (dataList.size()>0)
        {
            if (dataList.get(currentPositon).playlistId.equals("0"))
            {
                tabPlaylist.setVisibility(View.GONE);
                tabSneekbarView.setVisibility(View.GONE);
            }
            else
            {
                tabSneekbarView.setVisibility(View.VISIBLE);
                tabPlaylist.setVisibility(View.VISIBLE);
                tvPlaylistTitle.setText(context.getString(R.string.playlist)+" . "+dataList.get(currentPositon).playlistName);
            }
            if(dataList.get(currentPositon).video_user_id.equals(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
            {
                if (dataList.get(currentPositon).playlistId.equals("0"))
                {
                    ivEditPlaylist.setVisibility(View.GONE);
                }
                else
                {
                    ivEditPlaylist.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                ivEditPlaylist.setVisibility(View.GONE);
            }
        }
    }


    private void callApiForSinglevideos(String videoId,boolean isFirstTime) {

        try {
            JSONObject parameters = new JSONObject();
            parameters.put("user_id", userId);
            parameters.put("video_id", videoId);

            VolleyRequest.JsonPostRequest(this, ApiLinks.showVideoDetail, parameters,Functions.getHeaders(this), new Callback() {
                @Override
                public void onResponce(String resp) {
                    swiperefresh.setRefreshing(false);
                    singalVideoParseData(resp,isFirstTime);
                }
            });

        }catch (Exception e) {
            Functions.printLog(Constants.tag, e.toString());
        }
    }





    public void singalVideoParseData(String responce,boolean isFirstTime) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                ArrayList<HomeModel> temp_list = new ArrayList<>();

                JSONObject video = msg.optJSONObject("Video");
                JSONObject user = msg.optJSONObject("User");
                JSONObject sound = msg.optJSONObject("Sound");
                JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                JSONObject pushNotification = user.optJSONObject("PushNotification");

                {
                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, pushNotification);

                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        temp_list.add(item);
                    }


                    if(dataList.isEmpty()){
                        setTabs(true);
                    }
                    dataList.addAll(temp_list);
                }

                for (HomeModel item : temp_list) {
                    pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager, this,fragmentConainerId));
                }
                pagerSatetAdapter.refreshStateSet(false);
                pagerSatetAdapter.notifyDataSetChanged();

                if (isFirstTime)
                {
                    if (dataList.size()>0)
                    {
                        if (dataList.get(currentPositon).playlistId.equals("0"))
                        {
                            tabPlaylist.setVisibility(View.GONE);
                            tabSneekbarView.setVisibility(View.GONE);
                        }
                        else
                        {
                            tabSneekbarView.setVisibility(View.VISIBLE);
                            tabPlaylist.setVisibility(View.VISIBLE);
                            tvPlaylistTitle.setText(context.getString(R.string.playlist)+" . "+dataList.get(menuPager.getCurrentItem()).playlistName);
                        }
                        if(dataList.get(currentPositon).video_user_id.equals(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
                        {
                            if (dataList.get(currentPositon).playlistId.equals("0"))
                            {
                                ivEditPlaylist.setVisibility(View.GONE);
                            }
                            else
                            {
                                ivEditPlaylist.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            ivEditPlaylist.setVisibility(View.GONE);
                        }
                    }
                }
                else
                {
                    pagerSatetAdapter.notifyDataSetChanged();
                }

                setupPlaylist();

                if (dataList.size()>0)
                {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getIntent().hasExtra("video_comment"))
                            {
                                if (getIntent().getBooleanExtra("video_comment",false))
                                {
                                    VideosListF fragment = (VideosListF) pagerSatetAdapter.getItem(menuPager.getCurrentItem());
                                    if (Functions.checkLoginUser(WatchVideosA.this))
                                    {
                                        fragment.openComment(fragment.item);
                                    }

                                }
                            }
                        }
                    },200);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            if(pageCount >0)
                pageCount--;

        } finally {
            isApiRuning = false;
        }

    }

    private void showPlaylistSetting() {
        Context wrapper = new ContextThemeWrapper(context, R.style.AlertDialogCustom);
        PopupMenu popup = new PopupMenu(wrapper, ivEditPlaylist);

        popup.getMenuInflater().inflate(R.menu.menu_playlist_setting, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.setGravity(Gravity.TOP | Gravity.RIGHT);
        }

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menuEdit:
                    {
                        editUserPlaylist();
                    }
                    break;
                    case R.id.menuDelete:
                    {
                        Functions.showDoubleButtonAlert(context, context.getString(R.string.playlist_setting),context.getString(R.string.are_you_sure_to_delete_this_playlist),
                                context.getString(R.string.cancel_), context.getString(R.string.delete), false, new FragmentCallBack() {
                                    @Override
                                    public void onResponce(Bundle bundle) {
                                        if (bundle.getBoolean("isShow",false))
                                        {
                                            deletePlaylist();
                                        }
                                    }
                                });
                    }
                    break;
                }
                return true;
            }
        });

    }

    private void editUserPlaylist() {
        CreatePlaylistModel playlistModel=new CreatePlaylistModel();
        playlistModel.setName(playlistName);
        CreatePlaylistStepTwoF f = new CreatePlaylistStepTwoF(false,new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (!(bundle.getBoolean("isShow")))
                {
                    currentPositon=0;
                    Log.d(Constants.tag,"Update List");
                    pageCount = 0;
                    dataList.clear();
                    callVideoApi();
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putSerializable("model",playlistModel);
        bundle.putSerializable("playlistMapList",playlistMapList);
        bundle.putString("playlist_id",getIntent().getStringExtra("playlist_id"));
        f.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.watchVideo_F, f,"EditPlaylistFromStepTwoF").addToBackStack("EditPlaylistFromStepTwoF").commit();
    }

    private void deletePlaylist() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", getIntent().getStringExtra("playlist_id"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(WatchVideosA.this,false,false);
        VolleyRequest.JsonPostRequest(WatchVideosA.this, ApiLinks.deletePlaylist, parameters,Functions.getHeaders(WatchVideosA.this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(WatchVideosA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        moveBack();
                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception: "+e);
                }

            }
        });

    }


    private void openPlaylist() {
        if (!(dataList.size()>0))
        {
            Toast.makeText(context, context.getString(R.string.refresh_playlist_to_open_list_detail), Toast.LENGTH_SHORT).show();
            return;
        }
        ShowPlaylistF fragment = new ShowPlaylistF(dataList, ""+dataList.get(currentPositon).video_id
                , ""+dataList.get(currentPositon).playlistId, ""+dataList.get(currentPositon).video_user_id
                , ""+dataList.get(currentPositon).playlistName, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {

                    if (bundle.getString("type").equalsIgnoreCase("videoPlay"))
                    {

                        currentPositon=bundle.getInt("position",0);
                        menuPager.setCurrentItem(currentPositon,true);
                    }
                    else
                    if (bundle.getString("type").equalsIgnoreCase("deletePlaylist"))
                    {
                        moveBack();
                    }
                    else
                    if (bundle.getString("type").equalsIgnoreCase("deletePlaylistVideo"))
                    {
                        currentPositon=bundle.getInt("position",0);
                        pagerSatetAdapter.refreshStateSet(true);
                        pagerSatetAdapter.removeFragment(currentPositon);
                        pagerSatetAdapter.refreshStateSet(false);
                        dataList.remove(currentPositon);
                        if (dataList.size()==0)
                        {
                            onBackPressed();
                        }
                        else
                        {
                            currentPositon=currentPositon-1;
                            menuPager.setCurrentItem(currentPositon,true);
                        }
                    }
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "");
    }

    public void callVideoApi() {
        isApiRuning = true;
        if (whereFrom.equalsIgnoreCase("playlistVideo")) {

            callApiForPlaylistVideos(getIntent().getStringExtra("playlist_id"),false);
        }
        else
        if (whereFrom.equalsIgnoreCase("userVideo")) {
            callApiForUserVideos();
        }
        else
        if (whereFrom.equalsIgnoreCase("likedVideo")) {

            callApiForLikedVideos();
        }
        else
        if (whereFrom.equalsIgnoreCase("privateVideo")) {

            callApiForPrivateVideos();
        }
        else
        if (whereFrom.equalsIgnoreCase("tagedVideo") ||
                whereFrom.equalsIgnoreCase("discoverTagedVideo")) {

            callApiForTagedVideos();
        }
        else
        if (whereFrom.equalsIgnoreCase("videoSound"))
        {
            callApiForSoundVideos();
        }
        else
        if (whereFrom.equalsIgnoreCase("IdVideo"))
        {
            callApiForSinglevideos(getIntent().getStringExtra("video_id"),false);
        }


    }

    // api for get the videos list from server
    private void callApiForSoundVideos() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("sound_id", getIntent().getStringExtra("soundId"));
            parameters.put("device_id", getIntent().getStringExtra("deviceId"));
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(WatchVideosA.this, ApiLinks.showVideosAgainstSound, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                swiperefresh.setRefreshing(false);
                parseSoundVideoData(resp);
            }
        });


    }

    public void parseSoundVideoData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                ArrayList<HomeModel> temp_list = new ArrayList<>();

                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);

                    JSONObject video = itemdata.optJSONObject("Video");
                    JSONObject user = itemdata.optJSONObject("User");
                    JSONObject sound = itemdata.optJSONObject("Sound");
                    JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                    JSONObject userPushNotification = user.optJSONObject("PushNotification");

                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);
                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        temp_list.add(item);
                    }

                }

                if(dataList.isEmpty()){
                    setTabs(true);
                }
                dataList.addAll(temp_list);

                for (HomeModel item : temp_list) {
                    pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager,this,fragmentConainerId));
                }
                pagerSatetAdapter.refreshStateSet(false);
                pagerSatetAdapter.notifyDataSetChanged();
                setupPlaylist();

            }

        } catch (Exception e) {
            e.printStackTrace();

            if(pageCount >0)
                pageCount--;

        } finally {
            isApiRuning = false;
        }

    }

    // api for get the videos list from server
    private void callApiForTagedVideos() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("hashtag", getIntent().getStringExtra("hashtag"));
            parameters.put("starting_point", "" + pageCount);


        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(WatchVideosA.this, ApiLinks.showVideosAgainstHashtag, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                swiperefresh.setRefreshing(false);
                parseHashtagVideoData(resp);
            }
        });


    }



    public void parseHashtagVideoData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                ArrayList<HomeModel> temp_list = new ArrayList<>();

                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    JSONObject video = itemdata.optJSONObject("Video");
                    JSONObject user = video.optJSONObject("User");
                    JSONObject sound = video.optJSONObject("Sound");
                    JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                    JSONObject userPushNotification = user.optJSONObject("PushNotification");

                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);

                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        temp_list.add(item);
                    }


                }

                if(dataList.isEmpty()){
                    setTabs(true);
                }
                dataList.addAll(temp_list);

                for (HomeModel item : temp_list) {
                    pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager, this,fragmentConainerId));
                }
                pagerSatetAdapter.refreshStateSet(false);
                pagerSatetAdapter.notifyDataSetChanged();
                menuPager.setCurrentItem(currentPositon,true);

                setupPlaylist();
            }

        } catch (Exception e) {
            e.printStackTrace();

            if(pageCount >0)
                pageCount--;

        } finally {
            isApiRuning = false;
        }

    }

    public void parsePrivateVideoData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                JSONArray public_array = msg.optJSONArray("private");

                ArrayList<HomeModel> temp_list = new ArrayList<>();

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
                        temp_list.add(item);
                    }

                }

                if(dataList.isEmpty()){
                    setTabs(true);
                }
                dataList.addAll(temp_list);

                for (HomeModel item : temp_list) {
                    pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager,this,fragmentConainerId));
                }
                pagerSatetAdapter.refreshStateSet(false);
                pagerSatetAdapter.notifyDataSetChanged();

                setupPlaylist();
            }

        } catch (Exception e) {
            e.printStackTrace();

            if(pageCount >0)
                pageCount--;

        } finally {
            isApiRuning = false;
        }

    }

    public void parseLikedVideoData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                ArrayList<HomeModel> temp_list = new ArrayList<>();


                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);

                    JSONObject video = itemdata.optJSONObject("Video");
                    JSONObject user = video.optJSONObject("User");
                    JSONObject sound = video.optJSONObject("Sound");
                    JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                    JSONObject userPushNotification = user.optJSONObject("PushNotification");

                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);


                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        temp_list.add(item);
                    }


                }

                if(dataList.isEmpty()){
                    setTabs(true);
                }
                dataList.addAll(temp_list);

                for (HomeModel item : temp_list) {
                    pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager,this,fragmentConainerId));
                }
                pagerSatetAdapter.refreshStateSet(false);
                pagerSatetAdapter.notifyDataSetChanged();

                setupPlaylist();
            }

        } catch (Exception e) {
            e.printStackTrace();

            if(pageCount >0)
                pageCount--;

        } finally {
            isApiRuning = false;
        }

    }

    public void parsePlalistVideoData(String responce,boolean isFirstTime) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                ArrayList<HomeModel> temp_list = new ArrayList<>();


                JSONArray public_array = msg.optJSONArray("PlaylistVideo");


                for (int i = 0; i < public_array.length(); i++) {
                    JSONObject itemdata = public_array.optJSONObject(i);

                    JSONObject video = itemdata.optJSONObject("Video");
                    JSONObject sound = video.optJSONObject("Sound");
                    JSONObject user = video.optJSONObject("User");
                    JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                    JSONObject userPushNotification = user.optJSONObject("PushNotification");

                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);
                    item.playlistVideoId=itemdata.optString("id");
                    item.playlistId=msg.getJSONObject("Playlist").optString("id");
                    item.playlistName=msg.getJSONObject("Playlist").optString("name");

                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        playlistMapList.put(item.video_id,item);
                        temp_list.add(item);
                    }


                }


                if(dataList.isEmpty()){
                    setTabs(true);
                }
                dataList.addAll(temp_list);

                for (HomeModel item : temp_list) {
                    pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager,this,fragmentConainerId));
                }
                pagerSatetAdapter.refreshStateSet(false);
                pagerSatetAdapter.notifyDataSetChanged();
                menuPager.setCurrentItem(currentPositon,true);

                if (isFirstTime)
                {
                    setupPlaylist();
                }

            }

        } catch (JSONException e) {
            Log.d(Constants.tag,"Error: Exception: "+e);

            if(pageCount >0)
                pageCount--;

        } finally {
            isApiRuning = false;
        }

    }

    public void parseMyVideoData(String responce) {

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
                        temp_list.add(item);
                    }

                }
                if (pinnedVideo!=null)
                {
                    Paper.book("PinnedVideo").write("pinnedVideo",pinnedVideo);
                }

                if(dataList.isEmpty()){
                    setTabs(true);
                }
                dataList.addAll(temp_list);

                for (HomeModel item : temp_list) {
                    pagerSatetAdapter.addFragment(new VideosListF(false, item, menuPager,this,fragmentConainerId));
                }
                pagerSatetAdapter.refreshStateSet(false);
                pagerSatetAdapter.notifyDataSetChanged();

                setupPlaylist();
            }

        } catch (Exception e) {
            e.printStackTrace();

            if(pageCount >0)
                pageCount--;

        } finally {
            isApiRuning = false;
        }

    }

    // api for get the videos list from server
    private void callApiForPlaylistVideos(String platlistId,boolean iSFirstTime) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", platlistId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(WatchVideosA.this, ApiLinks.showPlaylists, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(WatchVideosA.this,resp);
                swiperefresh.setRefreshing(false);
                parsePlalistVideoData(resp,iSFirstTime);
            }
        });


    }

    // api for get the videos list from server
    private void callApiForUserVideos() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));

            if (!(userId.equalsIgnoreCase(Functions.getSharedPreference(context).getString(Variables.U_ID, "")))) {
                parameters.put("other_user_id", userId);
            }
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(WatchVideosA.this, ApiLinks.showVideosAgainstUserID, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                swiperefresh.setRefreshing(false);
                parseMyVideoData(resp);
            }
        });


    }

    // api for get the videos list from server
    private void callApiForLikedVideos() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyRequest.JsonPostRequest(WatchVideosA.this, ApiLinks.showUserLikedVideos, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                swiperefresh.setRefreshing(false);
                parseLikedVideoData(resp);
            }
        });


    }

    // api for get the videos list from server
    private void callApiForPrivateVideos() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userId);
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyRequest.JsonPostRequest(WatchVideosA.this, ApiLinks.showVideosAgainstUserID, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                swiperefresh.setRefreshing(false);
                parsePrivateVideoData(resp);
            }
        });


    }


    private static int callbackVideoLisCode=3292;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==callbackVideoLisCode)
        {
            Bundle bundle=new Bundle();
            bundle.putBoolean("isShow",true);
            VideosListF.videoListCallback.onResponce(bundle);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (pagerSatetAdapter != null && pagerSatetAdapter.getCount() > 0) {
            VideosListF fragment = (VideosListF) pagerSatetAdapter.getItem(menuPager.getCurrentItem());
            fragment.mainMenuVisibility(true);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isShow", true);
        intent.putExtra("arraylist", dataList);
        intent.putExtra("pageCount", pageCount);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if(pagerSatetAdapter !=null && pagerSatetAdapter.getCount()>0) {

            VideosListF fragment = (VideosListF) pagerSatetAdapter.getItem(menuPager.getCurrentItem());
            fragment.mainMenuVisibility(false);
        }
    }


    public void moveBack()
    {
        Intent intent = new Intent();
        intent.putExtra("isShow", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}
