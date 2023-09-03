package com.qboxus.tictic.activitesfragments;


import static com.qboxus.tictic.activitesfragments.livestreaming.Constants.KEY_CLIENT_ROLE;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.adcolony.sdk.AdColonyZone;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.activitesfragments.livestreaming.activities.LiveUsersA;
import com.qboxus.tictic.activitesfragments.livestreaming.activities.MultiViewLiveA;
import com.qboxus.tictic.activitesfragments.livestreaming.activities.SingleCastJoinA;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.activitesfragments.profile.ProfileA;
import com.qboxus.tictic.activitesfragments.profile.usersstory.ViewStoryA;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderA;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderDuetA;
import com.qboxus.tictic.activitesfragments.walletandwithdraw.MyWallet;
import com.qboxus.tictic.adapters.StoryAdapter;
import com.qboxus.tictic.adapters.NotificationAdapter;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.models.NotificationModel;
import com.qboxus.tictic.models.StoryModel;
import com.qboxus.tictic.models.StoryVideoModel;
import com.qboxus.tictic.models.StreamInviteModel;
import com.qboxus.tictic.models.StreamJoinModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.services.UploadService;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.PermissionUtils;
import com.qboxus.tictic.simpleclasses.TicTic;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationA extends AppCompatLocaleActivity implements View.OnClickListener {
    Context context;
    PermissionUtils takePermissionUtils;
    NotificationAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<NotificationModel> datalist;
    SwipeRefreshLayout swiperefresh;
    NewVideoBroadCast mReceiver;
    LinearLayout dataContainer;
    ShimmerFrameLayout shimmerFrameLayout;
    NotificationModel selectedNotificationModel;
    String streamingType="";
    int selectedPosition;
    DatabaseReference rootRef;
    int pageCount = 0;
    boolean ispostFinsh;

    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;

    boolean isApiCall=false;


    SimpleDraweeView ivUserPic;
    LinearLayout tabCreateStory;

    RecyclerView storyRecyclerview;
    StoryAdapter storyAdapter;
    ArrayList<StoryModel> storyDataList=new ArrayList<>();
    StoryModel selectedStoryItem;


    public NotificationA() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(NotificationA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_notification);
        context = NotificationA.this;


        rootRef = FirebaseDatabase.getInstance().getReference();
        datalist = new ArrayList<>();
        tabCreateStory=findViewById(R.id.tabCreateStory);
        tabCreateStory.setOnClickListener(this);
        ivUserPic=findViewById(R.id.ivUserPic);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        dataContainer=findViewById(R.id.dataContainer);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        adapter = new NotificationAdapter(context, datalist, new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, NotificationModel item) {
                selectedPosition=postion;
                selectedNotificationModel=datalist.get(selectedPosition);
                switch (view.getId()) {
                    case R.id.watch_btn:
                        if (item.type.equals("live")) {
                            openLivedUser();
                        }else
                        if (item.type.equalsIgnoreCase("video_comment") || item.type.equalsIgnoreCase("video_like"))
                        {
                            openWatchVideoWithComment(item);
                        }else
                        {
                            openWatchVideo(item);
                        }

                        break;
                    case R.id.btnAcceptRequest:
                    {
                        streamingType=item.type;
                        if (item.type.equals("single") || item.type.equals("multiple")) {
                            takePermissionUtils=new PermissionUtils(NotificationA.this,mPermissionAcceptResult);
                            if (takePermissionUtils.isCameraRecordingPermissionGranted())
                            {
                                inviteRequestStatusUpdate(item.id,item.live_streaming_id,"1",postion);
                            }
                            else
                            {
                                takePermissionUtils.showCameraRecordingPermissionDailog(context.getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                            }
                        }

                    }
                    break;
                    case R.id.btnDeleteRequest:
                    {
                        streamingType=item.type;
                        if (item.type.equals("single") || item.type.equals("multiple")) {
                            takePermissionUtils=new PermissionUtils(NotificationA.this,mPermissionRejectResult);
                            if (takePermissionUtils.isCameraRecordingPermissionGranted())
                            {
                                inviteRequestStatusUpdate(item.id,item.live_streaming_id,"2",postion);
                            }
                            else
                            {
                                takePermissionUtils.showCameraRecordingPermissionDailog(context.getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                            }

                        }
                    }
                    break;
                    case R.id.follow_btn:
                    {
                        if (Functions.checkLoginUser(NotificationA.this))
                        {
                            followUnFollowUser();
                        }
                    }
                    break;
                    default:
                    {
                        streamingType=item.type;
                        if (item.type.equals("single")) {
                            openSingleStream();
                        }
                        else
                        if (item.type.equals("multiple")) {
                            openSingleStream();
                        }
                        else
                        {
                            openProfile(item);
                        }
                    }
                    break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                Functions.printLog("resp", "" + scrollOutitems);
                if (userScrolled && (scrollOutitems == datalist.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApi();
                    }
                }


            }
        });

        loadMoreProgress = findViewById(R.id.load_more_progress);


        findViewById(R.id.inbox_btn).setOnClickListener(this);
        swiperefresh =findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (datalist.size()<1)
                {
                    dataContainer.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmer();
                }
                pageCount = 0;
                callApi();
                callStoryApi();
            }
        });
        setupStoryRecyclerview();


        mReceiver = new NewVideoBroadCast();
       registerReceiver(mReceiver, new IntentFilter("newVideo"));

        if (!Functions.getSettingsPreference(context).getString(Variables.AddType,"none").equalsIgnoreCase("none")){

            if( Functions.getSettingsPreference(context).getString(Variables.AddType,"").equalsIgnoreCase("admob")){
                initBannerGoogleAd();
            }
            else if( Functions.getSettingsPreference(context).getString(Variables.AddType,"").equalsIgnoreCase("adcolony")){
                initBannerAd();
            }

        }
    }

    LinearLayout adView;
    private AdColonyAdView bannerAdColony;
    private void initBannerAd() {
        adView = findViewById(R.id.banneradColony);
        adView.setVisibility(View.VISIBLE);
        AdColonyAdViewListener bannerListener = new AdColonyAdViewListener() {

            // Code to be executed when an ad request is filled
            // or when an ad finishes loading.
            @Override
            public void onRequestFilled(AdColonyAdView adColonyAdView) {

                //Remove previous ad view if present.
                if (adView.getChildCount() > 0) {
                    adView.removeView(bannerAdColony);
                }
                adView.addView(adColonyAdView);
                bannerAdColony = adColonyAdView;
            }

            // Code to be executed when an ad request is not filled
            //or when an ad is not loaded.
            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                super.onRequestNotFilled(zone);
            }

            //Code to be executed when an ad opens
            @Override
            public void onOpened(AdColonyAdView ad) {
                super.onOpened(ad);
            }

            //Code to be executed when user closed an ad
            @Override
            public void onClosed(AdColonyAdView ad) {
                super.onClosed(ad);
            }

            // Code to be executed when the user clicks on an ad.
            @Override
            public void onClicked(AdColonyAdView ad) {
                super.onClicked(ad);
            }

            // called after onAdOpened(), when a user click opens another app
            // (such as the Google Play), backgrounding the current app
            @Override
            public void onLeftApplication(AdColonyAdView ad) {
                super.onLeftApplication(ad);
            }
        };

        // Optional Ad specific options to be sent with request
        AdColonyAdOptions adOptions = new AdColonyAdOptions();

        //Request Ad
        AdColony.requestAdView(Constants.AD_COLONY_BANNER_ID, bannerListener, AdColonyAdSize.BANNER, adOptions);
    }


    AdView adViewGoogle;
    public void initBannerGoogleAd() {
        adViewGoogle = findViewById(R.id.banneradGoogle);
        adViewGoogle.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewGoogle.loadAd(adRequest);
    }



    private void openSingleStream() {
        goLive();
    }


    private void followUnFollowUser() {
        Functions.callApiForFollowUnFollow(this,
                Functions.getSharedPreference(context).getString(Variables.U_ID, ""),
                selectedNotificationModel.user_id,
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {
                    }

                    @Override
                    public void onSuccess(String responce) {
                        try {
                            JSONObject jsonObject=new JSONObject(responce);
                            if (jsonObject.optString("code").equalsIgnoreCase("200"))
                            {
                                JSONObject msgObj=jsonObject.getJSONObject("msg");
                                UserModel userDetailModel=DataParsing.getUserDataModel(msgObj.getJSONObject("User"));
                                selectedNotificationModel.button=userDetailModel.getButton();
                                datalist.set(selectedPosition,selectedNotificationModel);
                                adapter.notifyDataSetChanged();
                            }

                        }
                        catch (Exception e)
                        {
                            Log.d(Constants.tag,"Exception: "+e);
                        }
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });

    }


    private void inviteRequestStatusUpdate(String id,String streamingId,String status,int postion) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", id);
            parameters.put("status", status);
            parameters.put("user_id",Functions.getSharedPreference(context).getString(Variables.U_ID,""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(NotificationA.this,false,false);
        VolleyRequest.JsonPostRequest(this, ApiLinks.acceptStreamingInvite, parameters,Functions.getHeaders(context), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(NotificationA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObj=new JSONObject(resp);
                    if (jsonObj.optString("code").equalsIgnoreCase("200"))
                    {
                        NotificationModel itemUpdate=datalist.get(postion);
                        itemUpdate.status=status;
                        datalist.set(postion,itemUpdate);
                        adapter.notifyDataSetChanged();

                        if (status.equalsIgnoreCase("1"))
                        {
                            acceptStreamInvitation(streamingId);
                        }
                        else
                        {
                            deleteStreamInvitation(streamingId);
                        }

                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });
    }

    private void deleteStreamInvitation(String streamingId) {
        rootRef.child("LiveStreamingUsers")
                .child(streamingId)
                .child("StreamInvite")
                .child(Functions.getSharedPreference(context).getString(Variables.U_ID,""))
                .removeValue();

    }

    private void acceptStreamInvitation(String streamingId) {
        StreamInviteModel itemUpdate=new StreamInviteModel();
        itemUpdate.setAccept(true);

        rootRef.child("LiveStreamingUsers")
                .child(streamingId)
                .child("StreamInvite")
                .child(Functions.getSharedPreference(context).getString(Variables.U_ID,""))
                .setValue(itemUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete())
                {
                   runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goLive();
                        }
                    });
                }
            }
        });

    }

    private void goLive() {
        rootRef.child("LiveStreamingUsers")
                .child(selectedNotificationModel.live_streaming_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            LiveUserModel selectLiveModel = snapshot.getValue(LiveUserModel.class);
                            if (selectLiveModel.getJoinStreamPrice()!=null)
                            {

                                if (selectLiveModel.getJoinStreamPrice().equalsIgnoreCase("0"))
                                {
                                  runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            joinStream(selectLiveModel);
                                        }
                                    });
                                }
                                else
                                {
                                    rootRef.child("LiveStreamingUsers").child(selectLiveModel.getStreamingId())
                                            .child("FeePaid").child(Functions.getSharedPreference(context).getString(Variables.U_ID,"")).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                          runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (snapshot.exists())
                                                    {
                                                        joinStream(selectLiveModel);
                                                    }
                                                    else
                                                    {
                                                        showPriceOffJoin(selectLiveModel);
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                           runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showPriceOffJoin(selectLiveModel);
                                                }
                                            });
                                        }
                                    });

                                }

                            }
                            else
                            {
                                Toast.makeText(context, selectedNotificationModel.username+" "+context.getString(R.string.is_offline_now), Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                          runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, selectedNotificationModel.username+" "+context.getString(R.string.is_offline_now), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void showPriceOffJoin(LiveUserModel selectLiveModel) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.price_to_join_stream_view);
        alertDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.d_round_white_background));

        RelativeLayout tabAccept = alertDialog.findViewById(R.id.tabAccept);
        ImageView closeBtn = alertDialog.findViewById(R.id.closeBtn);
        TextView tvJoiningAmount=alertDialog.findViewById(R.id.tvJoiningAmount);

        tvJoiningAmount.setText(""+selectLiveModel.getJoinStreamPrice()+" "+context.getString(R.string.coins_are_deducted_from_your_wallet_to_join_the_stream));


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                deductPriceFromWallet(selectLiveModel);
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void deductPriceFromWallet(LiveUserModel selectLiveModel) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID,"0"));
            parameters.put("live_streaming_id", selectLiveModel.getStreamingId());
            parameters.put("coin", selectLiveModel.getJoinStreamPrice());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(NotificationA.this,false,false);
        VolleyRequest.JsonPostRequest(this, ApiLinks.watchLiveStream,parameters, Functions.getHeaders(context),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(NotificationA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        UserModel userDetailModel= DataParsing.getUserDataModel(msgObj.getJSONObject("User"));
                        Functions.getSharedPreference(context).edit().putString(Variables.U_WALLET, ""+userDetailModel.getWallet()).commit();

                        String userId=Functions.getSharedPreference(context).getString(Variables.U_ID,"");
                        HashMap<String,String> map=new HashMap<>();
                        map.put("userId",userId);
                        rootRef.child("LiveStreamingUsers").child(selectLiveModel.getStreamingId())
                                .child("FeePaid").child(userId)
                                .setValue(map);
                        joinStream(selectLiveModel);
                    }
                    else
                    {
                        startActivity(new Intent(context, MyWallet.class));
                    }
                } catch (Exception e) {
                    android.util.Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });


    }


    private void joinStream(LiveUserModel selectLiveModel) {
        if (streamingType.equals("single"))
        {
            Functions.showLoader(NotificationA.this,false,false);
            rootRef.child("LiveStreamingUsers").child(selectLiveModel.getStreamingId()).child("JoinStream")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            HashMap<String, StreamJoinModel> joinStreamMap=new HashMap<>();
                            for (DataSnapshot postData:snapshot.getChildren())
                            {
                                StreamJoinModel item=postData.getValue(StreamJoinModel.class);
                                if(item!=null && item.getUserId()!=null)
                                {
                                    joinStreamMap.put(item.getUserId(),item);
                                }

                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Functions.cancelLoader();
                                    if (snapshot.exists())
                                    {
                                        if (joinStreamMap.keySet().size()>0)
                                        {
                                            if (joinStreamMap.keySet().size()==1)
                                            {
                                                if (joinStreamMap.containsKey(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
                                                {
                                                    goSingleLive(selectLiveModel);
                                                }
                                                else
                                                {
                                                    Toast.makeText(context, context.getString(R.string.streaming_already_join_by_an_other_user), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else
                                            {
                                                Toast.makeText(context, context.getString(R.string.streaming_already_join_by_an_other_user), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else
                                        {
                                            goSingleLive(selectLiveModel);
                                        }
                                    }
                                    else
                                    {
                                        goSingleLive(selectLiveModel);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                          runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Functions.cancelLoader();
                                }
                            });
                            Log.d(Constants.tag,"DatabaseError: "+error);
                        }
                    });


        }
        else
        if (streamingType.equals("multiple"))
        {
            ArrayList<LiveUserModel> dataList = new ArrayList<>();
            dataList.add(selectLiveModel);
            final Intent intent = new Intent();
            intent.putExtra("user_id", selectLiveModel.getUserId());
            intent.putExtra("user_name", selectLiveModel.getUserName());
            intent.putExtra("user_picture", selectLiveModel.getUserPicture());
            intent.putExtra("user_role", io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
            intent.putExtra("onlineType", "multicast");
            intent.putExtra("description", selectLiveModel.getDescription());
            intent.putExtra("secureCode", "");
            intent.putExtra("dataList",dataList);
            intent.putExtra("position",0);
            intent.setClass(context, MultiViewLiveA.class);
            startActivity(intent);
        }

    }

    private void goSingleLive(LiveUserModel selectLiveModel) {
        final Intent intent = new Intent(context, SingleCastJoinA.class);
        intent.putExtra("bookingId",selectLiveModel.getStreamingId());
        intent.putExtra("dataModel",selectLiveModel);
        intent.putExtra(KEY_CLIENT_ROLE, io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        TicTic ticTic = (TicTic)getApplication();
        ticTic.engineConfig().setChannelName(selectLiveModel.getStreamingId());
        startActivity(intent);
    }


    private ActivityResultLauncher<String[]> mPermissionRejectResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(NotificationA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(NotificationA.this,context.getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                    }
                    else
                    if (allPermissionClear)
                    {
                        inviteRequestStatusUpdate(selectedNotificationModel.id,selectedNotificationModel.live_streaming_id,"2",selectedPosition);
                    }

                }
            });


    private ActivityResultLauncher<String[]> mPermissionAcceptResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(NotificationA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(NotificationA.this,context.getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                    }
                    else
                    if (allPermissionClear)
                    {
                        inviteRequestStatusUpdate(selectedNotificationModel.id,selectedNotificationModel.live_streaming_id,"1",selectedPosition);
                    }

                }
            });



    private void setupStoryRecyclerview() {
        storyRecyclerview=findViewById(R.id.storyRecyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        storyRecyclerview.setLayoutManager(layoutManager);
        storyAdapter=new StoryAdapter(storyDataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                selectedStoryItem=storyDataList.get(pos);

                if (view.getId()==R.id.tabUserPic)
                {
                    Intent myIntent = new Intent(NotificationA.this, ViewStoryA.class);
                    myIntent.putExtra("storyList",storyDataList); //Optional parameters
                    myIntent.putExtra("position", pos); //Optional parameters
                    startActivity(myIntent);
                }
            }
        });
        storyRecyclerview.setAdapter(storyAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if ((pageCount == 0) || Variables.reloadMyNotification) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Variables.reloadMyNotification = false;

                    if (datalist.size()<1)
                    {
                        dataContainer.setVisibility(View.GONE);
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.startShimmer();
                    }

                    pageCount = 0;
                    callApi();
                    setupScreenData();
                    callStoryApi();
                }
            },200);
        }
    }


    private void setupScreenData() {
        String picUrl = Functions.getSharedPreference(NotificationA.this).getString(Variables.U_PIC, "null");
        ivUserPic.setController(Functions.frescoImageLoad(picUrl,R.drawable.ic_user_icon,ivUserPic,false));
    }


    // get the all notification from the server against the profile id
    public void callApi() {
        if (isApiCall)
        {
            return;
        }
        isApiCall=true;

        if (datalist == null)
            datalist = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Functions.getSharedPreference(NotificationA.this).getString(Variables.U_ID, "0"));
            jsonObject.put("starting_point", "" + pageCount);


        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(NotificationA.this, ApiLinks.showAllNotifications, jsonObject,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                isApiCall=false;
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                dataContainer.setVisibility(View.VISIBLE);
                swiperefresh.setRefreshing(false);
                parseData(resp);
            }
        });

    }


    // parse the data of the notification and place then on data model list
    public void parseData(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                ArrayList<NotificationModel> temp_list = new ArrayList<>();

                for (int i = 0; i < msg.length(); i++) {
                    JSONObject data = msg.getJSONObject(i);

                    JSONObject notification = data.optJSONObject("Notification");
                    JSONObject video = data.optJSONObject("Video");
                    UserModel senderUserDetailModel=DataParsing.getUserDataModel(data.optJSONObject("Sender"));
                    UserModel receiverUserDetailModel=DataParsing.getUserDataModel(data.optJSONObject("Receiver"));

                    NotificationModel item = new NotificationModel();

                    item.id = notification.optString("id");
                    item.status=notification.optString("status","0");
                    item.live_streaming_id=notification.optString("live_streaming_id","");
                    item.user_id = senderUserDetailModel.getId();
                    item.username = senderUserDetailModel.getUsername();
                    item.first_name = senderUserDetailModel.getFirstName();
                    item.last_name = senderUserDetailModel.getLastName();
                    item.button = senderUserDetailModel.getButton();

                    item.profile_pic = senderUserDetailModel.getProfilePic();
                    item.effected_fb_id = receiverUserDetailModel.getId();

                    item.type = notification.optString("type");

                    if (item.type.equalsIgnoreCase("video_comment") || item.type.equalsIgnoreCase("video_like")) {

                        item.video_id = video.optString("id");
                        item.video = video.optString("video");
                        item.thum = video.optString("thum");
                        item.gif = video.optString("gif");

                    }

                    item.string = notification.optString("string");
                    item.created = notification.optString("created");

                    temp_list.add(item);


                }

                if (pageCount == 0) {
                    datalist.clear();
                    datalist.addAll(temp_list);
                } else {
                    datalist.addAll(temp_list);
                }

                adapter.notifyDataSetChanged();

            }

            if (datalist.isEmpty()) {
                findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.no_data_layout).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loadMoreProgress.setVisibility(View.GONE);
        }
    }

    private void callStoryApi() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, "0"));
            parameters.put("starting_point", "0");
        }
        catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }

        VolleyRequest.JsonPostRequest(NotificationA.this, ApiLinks.showFriendsStories, parameters, Functions.getHeaders(this),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(NotificationA.this,resp);
                pareseStoryResponce(resp);
            }
        });
    }

    private void pareseStoryResponce(String resp) {
        try {
            JSONObject respObj=new JSONObject(resp);

            if (respObj.optString("code").equals("200"))
            {
                storyDataList.clear();
                JSONObject msgObj = respObj.getJSONObject("msg");
                JSONArray myUserArray = msgObj.getJSONArray("User");
                for (int i = 0; i < myUserArray.length(); i++) {

                    JSONObject obj = myUserArray.getJSONObject(i);
                    ArrayList<StoryVideoModel> userVideoList=new ArrayList<>();
                    UserModel userDetailModel=DataParsing.getUserDataModel(obj);

                    StoryModel storyItem=new StoryModel();
                    storyItem.setUserModel(userDetailModel);
                    JSONArray storyArray = obj.getJSONArray("Video");
                    for (int j=0; j<storyArray.length();j++)
                    {
                        JSONObject itemObj=storyArray.getJSONObject(j);
                        StoryVideoModel videoItem=DataParsing.getVideoDataModel(itemObj.optJSONObject("Video"));
                        userVideoList.add(videoItem);
                    }
                    storyItem.setVideoList(userVideoList);
                    if (userVideoList.size()>0)
                    {
                        storyDataList.add(storyItem);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

        } catch (Exception v) {
            Log.d(Constants.tag,"Exception story : "+v);
        }
        finally {
            updateStoryCount();
        }
    }


    private void updateStoryCount() {
        if (storyDataList.size()>0)
        {
            storyRecyclerview.setVisibility(View.VISIBLE);
        }
        else
        {
            storyRecyclerview.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inbox_btn:
            {
                openInboxF();
            }
            break;
            case R.id.tabCreateStory:
            {
                openStoryView();
            }
            break;
        }
    }

    private void openStoryView() {
        takePermissionUtils=new PermissionUtils(NotificationA.this,mPermissionResult);
        if (takePermissionUtils.isStorageCameraRecordingPermissionGranted()) {

            uploadNewVideo();
        }
        else
        {
            takePermissionUtils.showStorageCameraRecordingPermissionDailog(context.getString(R.string.we_need_storage_camera_recording_permission_for_make_new_video));
        }
    }

    private void uploadNewVideo() {
        Functions.makeDirectry(Functions.getAppFolder(NotificationA.this)+Variables.APP_HIDED_FOLDER);
        Functions.makeDirectry(Functions.getAppFolder(NotificationA.this)+Variables.DRAFT_APP_FOLDER);
        if (Functions.checkLoginUser(NotificationA.this))
        {
            if (Functions.isMyServiceRunning(NotificationA.this, new UploadService().getClass())) {
                Toast.makeText(NotificationA.this, context.getString(R.string.video_already_in_progress), Toast.LENGTH_SHORT).show();
            } else {
                boolean isOpenGLSupported = Functions.isOpenGLVersionSupported(context, 0x00030001);
                if (isOpenGLSupported) {
                    Intent intent = new Intent(NotificationA.this, VideoRecoderA.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                } else {
                    Toast.makeText(context, context.getString(R.string.your_device_opengl_verison_is_not_compatible_to_use_this_feature), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void openInboxF() {
        Intent intent=new Intent(NotificationA.this,InboxA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    // open the broad cast live user streaming on notification receive
    private void openLivedUser() {
        Intent intent=new Intent(NotificationA.this, LiveUsersA.class);
        startActivity(intent);
       overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void openWatchVideo(NotificationModel item) {
        Intent intent = new Intent(NotificationA.this, WatchVideosA.class);
        intent.putExtra("video_id", item.video_id);
        intent.putExtra("position", 0);
        intent.putExtra("pageCount", 0);
        intent.putExtra("userId",Functions.getSharedPreference(NotificationA.this).getString(Variables.U_ID,""));
        intent.putExtra("whereFrom","IdVideo");
        startActivity(intent);
    }

    private void openWatchVideoWithComment(NotificationModel item) {
        Intent intent = new Intent(context, WatchVideosA.class);
        intent.putExtra("video_id", item.video_id);
        intent.putExtra("position", 0);
        intent.putExtra("pageCount", 0);
        intent.putExtra("userId",Functions.getSharedPreference(context).getString(Variables.U_ID,""));
        intent.putExtra("whereFrom","IdVideo");
        if (item.type.equals("video_comment"))
        {
            intent.putExtra("video_comment",true);
        }
        startActivity(intent);
    }



    // open the profile of the user which notification we have receive
    public void openProfile(NotificationModel item) {
        if (Functions.getSharedPreference(context).getString(Variables.U_ID, "0").equals(item.user_id)) {

            TabLayout.Tab profile = MainMenuActivity.tabLayout.getTabAt(4);
            profile.select();

        } else {

            Intent intent=new Intent(NotificationA.this, ProfileA.class);
            intent.putExtra("user_id", item.user_id);
            intent.putExtra("user_name", item.username);
            intent.putExtra("user_pic", item.profile_pic);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        }

    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(NotificationA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(context,context.getString(R.string.we_need_storage_camera_recording_permission_for_make_new_video));
                    }
                    else
                    if (allPermissionClear)
                    {
                        uploadNewVideo();
                    }

                }
            });


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPermissionResult!=null)
        {
            mPermissionResult.unregister();
        }
        if (mReceiver != null) {
         unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }



    private class NewVideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            pageCount = 0;
            callStoryApi();
        }
    }


}
