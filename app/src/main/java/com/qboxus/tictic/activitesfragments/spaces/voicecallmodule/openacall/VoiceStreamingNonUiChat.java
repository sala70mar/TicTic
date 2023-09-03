package com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall;

import android.util.Log;


import com.qboxus.tictic.Constants;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.AGEventHandler;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.ConstantApp;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.TicTic;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class VoiceStreamingNonUiChat extends VoiceStreamingNonUiBase implements AGEventHandler {

    private volatile boolean mAudioMuted = true;
    private volatile int mAudioRouting = -1;

    TicTic application;
    String channelName,userId;
    boolean isCallStart=false;

    public VoiceStreamingNonUiChat(TicTic application) {
        super(application);
        this.application=application;
    }


    public String getChannelName() {
        return channelName;
    }

    public String getUid() {
        return userId;
    }

    public void setChannelNameAndUid(String channelName, String userId) {
        this.channelName = channelName;
        this.userId=userId;
        Functions.printLog(Constants.tag,"channelName:"+this.channelName+" UserID:"+this.userId);
        config().mUid=Integer.valueOf(userId);
    }

    public void startStream(FragmentCallBack voiceControler)
    {
        initConfiguration();
    }

    protected void initConfiguration() {
        isCallStart=true;
        event().addEventHandler(this);

        worker().joinChannel(channelName, config().mUid);
        Log.d(Constants.tag,"Connected Channel ID: "+channelName);
        //volum control
//        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    protected void removeConfiguration() {
        isCallStart=false;
        doLeaveChannel();
        event().removeEventHandler(this);
    }


    public void onEnableSpeakerSwitch() {
        Functions.printLog(Constants.tag,"onSwitchSpeakerClicked "+ mAudioMuted + " " + mAudioRouting);
        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.setEnableSpeakerphone(true);
    }
    public void onDisableSpeakerSwitch() {
        Functions.printLog(Constants.tag,"onSwitchSpeakerClicked "+ mAudioMuted + " " + mAudioRouting);
        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.setEnableSpeakerphone(false);
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
    }

    public void quitCall() {
        Functions.printLog(Constants.tag,"quitCall ");
        removeConfiguration();
    }

    public void muteVoiceCall() {
        Functions.printLog(Constants.tag,"muteVoiceCall");
        mAudioMuted=true;

        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.muteLocalAudioStream(mAudioMuted);
        if(mAudioRouting==0){
            onDisableSpeakerSwitch();
        }
        else{
            onEnableSpeakerSwitch();
        }

    }

    public void enableVoiceCall(){
        Functions.printLog(Constants.tag,"enableVoiceCall");
        mAudioMuted=false;


        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.muteLocalAudioStream(mAudioMuted);
        if(mAudioRouting==0){
            onDisableSpeakerSwitch();
        }
        else{
            onEnableSpeakerSwitch();
        }
    }





    public boolean ismAudioMuted() {
        return mAudioMuted;
    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        String msg = "onJoinChannelSuccess " + channel + "=>  UserId:" + (uid) + " => " + elapsed;
        Functions.printLog(Constants.tag,msg);
        rtcEngine().muteLocalAudioStream(mAudioMuted);
    }



    @Override
    public void onUserOffline(int uid, int reason) {
        String msg = "onUserOffline " + (uid) + " " + reason;
        Functions.printLog(Constants.tag,msg);

    }

    @Override
    public void onExtraCallback(final int type, final Object... data) {

        if (isCallStart)
        {
            doHandleExtraCallback(type, data);
        }

    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;

        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_AUDIO_MUTED: {
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];

                Functions.printLog(Constants.tag,"mute: " + (peerUid & 0xFFFFFFFFL) + " " + muted);
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_QUALITY: {
                peerUid = (Integer) data[0];
                int quality = (int) data[1];
                short delay = (short) data[2];
                short lost = (short) data[3];


//                setupLocalCallback("speaker",""+peerUid);
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS: {
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];


                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_APP_ERROR: {
                int subType = (int) data[0];
                if (subType == ConstantApp.AppError.NO_NETWORK_CONNECTION) {
                    Functions.printLog(Constants.tag,"msgNoNetworkConnection " + subType);
                }

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];
                Functions.printLog(Constants.tag,error + " " + description);
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED: {
                notifyHeadsetPlugged((int) data[0]);
                break;
            }
        }
    }

    public void notifyHeadsetPlugged(final int routing) {
        Functions.printLog(Constants.tag,"notifyHeadsetPlugged " + routing);
        mAudioRouting = routing;
        if(mAudioRouting==0){
            onDisableSpeakerSwitch();
        }
        else{
            onEnableSpeakerSwitch();
        }

    }



}
