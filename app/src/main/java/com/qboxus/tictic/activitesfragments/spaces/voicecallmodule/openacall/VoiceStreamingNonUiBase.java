package com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall;


import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.CurrentUserSettings;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.EngineConfig;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.MyEngineEventHandler;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.WorkerThread;
import com.qboxus.tictic.simpleclasses.TicTic;

import io.agora.rtc.RtcEngine;

public class VoiceStreamingNonUiBase {

    TicTic application;

    public VoiceStreamingNonUiBase(TicTic application) {
        this.application=application;
        application.initWorkerThread();
    }


    protected RtcEngine rtcEngine() {
        return application.getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return application.getWorkerThread();
    }

    protected final EngineConfig config() {
        return application.getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return application.getWorkerThread().eventHandler();
    }

    protected CurrentUserSettings vSettings() {
        return TicTic.mAudioSettings;
    }

}
