package com.qboxus.tictic.activitesfragments.livestreaming.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.activitesfragments.livestreaming.adapter.MultiCastStatAdapter;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.LocalStatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.RemoteStatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.StatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.StatsManager;
import com.qboxus.tictic.activitesfragments.livestreaming.ui.VideoGridContainer;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.TicTic;
import com.qboxus.tictic.simpleclasses.Variables;
import com.qboxus.tictic.simpleclasses.VerticalViewPager;

import java.util.ArrayList;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class MultiViewLiveA extends RtcBaseMultiviewA implements View.OnClickListener {

    SwipeRefreshLayout swiperefresh;
    VerticalViewPager viewpager;
    MultiCastStatAdapter pagerSatetAdapter;
    ArrayList<LiveUserModel> dataList = new ArrayList<>();
    RelativeLayout tabNoUser;
    public VideoGridContainer mVideoGridContainer;
    public VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    DatabaseReference rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE, Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(), false);
        setContentView(R.layout.activity_multi_view_live);

        InitControl();
        ActionControl();
    }

    private void ActionControl() {
        swiperefresh.setProgressViewOffset(false, 0, 200);
        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRelated();
            }
        });
    }

    private void refreshRelated() {
        swiperefresh.setRefreshing(true);
        swiperefresh.setEnabled(true);
        dataList.clear();
        callStreamerList();
    }

    private void callStreamerList() {

        rootref.child("LiveStreamingUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swiperefresh.setRefreshing(false);
                if (snapshot.exists())
                {
                    ArrayList<LiveUserModel> tempList = new ArrayList<>();
                    for (DataSnapshot postData:snapshot.getChildren())
                    {
                        LiveUserModel model = postData.getValue(LiveUserModel.class);
                        if(model.getOnlineType()!=null && model.getOnlineType().equals("multicast"))
                        {
                            tempList.add(model);
                        }
                    }
                    if (dataList.isEmpty())
                    {
                        setTabs();
                    }
                    dataList.addAll(tempList);
                    pagerSatetAdapter.refreshStateSet(false);
                    pagerSatetAdapter.notifyDataSetChanged();
                    if (!(swiperefresh.isEnabled()))
                    {swiperefresh.setEnabled(false);}

                    if (dataList.isEmpty())
                    {
                        tabNoUser.setVisibility(View.VISIBLE);
                        viewpager.setVisibility(View.GONE);
                    }
                    else
                    {
                        tabNoUser.setVisibility(View.GONE);
                        viewpager.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (dataList.isEmpty())
                {
                    tabNoUser.setVisibility(View.VISIBLE);
                    viewpager.setVisibility(View.GONE);
                }
                else
                {
                    tabNoUser.setVisibility(View.GONE);
                    viewpager.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public String userId, userName, userPicture;
    public int userRole;
    public String onlineType,description,secureCode,bookingId;


    private void InitControl() {
        Intent bundle = getIntent();
        if (bundle != null) {
            userId = bundle.getStringExtra("user_id");
            userName = bundle.getStringExtra("user_name");
            userPicture = bundle.getStringExtra("user_picture");
            userRole = bundle.getIntExtra("user_role", Constants.CLIENT_ROLE_AUDIENCE);
            onlineType=bundle.getStringExtra("onlineType");
            description=bundle.getStringExtra("description");
            secureCode=bundle.getStringExtra("secureCode");
            if (onlineType.equals("oneTwoOne"))
            {
                bookingId=getIntent().getStringExtra("bookingId");
            }
        }

        rootref= FirebaseDatabase.getInstance().getReference();
        swiperefresh=findViewById(R.id.swiperefresh);
        viewpager=findViewById(R.id.viewpager);
        tabNoUser=findViewById(R.id.tabNoUser);
        setTabs();
        getPreviousList();
    }

    private void getPreviousList() {
        ArrayList<LiveUserModel> tempList = new ArrayList<>();
        tempList= (ArrayList<LiveUserModel>) getIntent().getSerializableExtra("dataList");

        dataList.addAll(tempList);
        pagerSatetAdapter.refreshStateSet(false);
        pagerSatetAdapter.notifyDataSetChanged();
        if (!(swiperefresh.isEnabled()))
        {swiperefresh.setEnabled(false);}

        viewpager.setCurrentItem(getIntent().getIntExtra("position",0),true);
    }


    public void setTabs() {
        pagerSatetAdapter = new MultiCastStatAdapter(getSupportFragmentManager(),dataList,MultiViewLiveA.this);
        viewpager.setAdapter(pagerSatetAdapter);
        viewpager.setOffscreenPageLimit(1);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


     @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Functions.printLog(com.qboxus.tictic.Constants.tag, "onFirstRemoteVideoDecoded");
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        Functions.printLog(com.qboxus.tictic.Constants.tag, "renderRemoteUser "+uid);
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

        if (!(getChannelName().equals("")) && (getChannelName()!=null))
        {
        }

        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);

    }

    // check the network quality
    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {

        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }


    public void switchCamera()
    {
        rtcEngine().switchCamera();
    }

    public void muteLocalAudioStream(boolean isAudioActivated)
    {
        rtcEngine().muteLocalAudioStream(isAudioActivated);
    }

    public void setBeautyEffectOptions(boolean isbeautyActivated)
    {
        rtcEngine().setBeautyEffectOptions(isbeautyActivated,
                com.qboxus.tictic.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);
    }

    public void stopBroadcast(int role)
    {
        rtcEngine().setClientRole(role);
        removeRtcVideo(0, true);
        statsManager().clearAllData();
    }

    public SurfaceView startBroadcast(String userId,int role)
    {
        TicTic ticTic = (TicTic)getApplication();
        ticTic.engineConfig().setChannelName(userId);
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        return  prepareRtcVideo(0, true);
    }

    public VideoEncoderConfiguration.VideoDimensions getconfigDimenIndex()
    {
        return com.qboxus.tictic.activitesfragments.livestreaming.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }

    public StatsManager setStatsManager()
    {
        return statsManager();
    }

    public void setClientRole(int userRole)
    {
        rtcEngine().setClientRole(userRole);
    }



    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }



    @Override
    public void onClick(View v) {

    }


    @Override
    public void onBackPressed() {
        finish();

    }
}