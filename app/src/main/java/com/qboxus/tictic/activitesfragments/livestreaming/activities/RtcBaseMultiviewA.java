package com.qboxus.tictic.activitesfragments.livestreaming.activities;


import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.qboxus.tictic.activitesfragments.livestreaming.Constants;
import com.qboxus.tictic.activitesfragments.livestreaming.rtc.EventHandler;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public abstract class RtcBaseMultiviewA extends BaseActivity implements EventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refreshStreamingConnection(String channelName) {
        config().setChannelName(channelName);
        registerRtcEventHandler(this);
        configVideo();
        joinChannel();
    }

    public void removeStreamingConnection()
    {
        removeRtcEventHandler(this);
        rtcEngine().leaveChannel();
    }

    public String getChannelName()
    {
      return ""+config().getChannelName();
    }

    private void configVideo() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        );
        configuration.mirrorMode = Constants.VIDEO_MIRROR_MODES[config().getMirrorEncodeIndex()];
        rtcEngine().setVideoEncoderConfiguration(configuration);
    }

    private void joinChannel() {
        rtcEngine().joinChannel(null, config().getChannelName(), "", 0);

    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine().setupLocalVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            0,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorLocalIndex()]
                    )
            );
        } else {
            rtcEngine().setupRemoteVideo(
                    new VideoCanvas(
                            surface,
                            VideoCanvas.RENDER_MODE_HIDDEN,
                            uid,
                            Constants.VIDEO_MIRROR_MODES[config().getMirrorRemoteIndex()]
                    )
            );
        }
        return surface;
    }

    protected void removeRtcVideo(int uid, boolean local) {
        if (local) {
            Log.d(com.qboxus.tictic.Constants.tag,"local True: ");
            rtcEngine().setupLocalVideo(null);
        } else {
            Log.d(com.qboxus.tictic.Constants.tag,"local false: ");
            rtcEngine().setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeRtcEventHandler(this);
        rtcEngine().leaveChannel();
    }


}
