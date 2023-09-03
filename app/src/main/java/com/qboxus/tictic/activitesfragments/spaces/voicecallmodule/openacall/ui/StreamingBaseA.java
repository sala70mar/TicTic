package com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewConfigurationCompat;


import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.CurrentUserSettings;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.EngineConfig;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.MyEngineEventHandler;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.model.WorkerThread;
import com.qboxus.tictic.simpleclasses.TicTic;

import io.agora.rtc.RtcEngine;

public abstract class StreamingBaseA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                initUIandEvent();
            }
        });
    }

    protected abstract void initUIandEvent();

    protected abstract void deInitUIandEvent();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // permission take from here

        ((TicTic) getApplication()).initWorkerThread();
    }


    @Override
    protected void onDestroy() {
        deInitUIandEvent();
        super.onDestroy();
    }


    protected RtcEngine rtcEngine() {
        return ((TicTic) getApplication()).getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return ((TicTic) getApplication()).getWorkerThread();
    }

    protected final EngineConfig config() {
        return ((TicTic) getApplication()).getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return ((TicTic) getApplication()).getWorkerThread().eventHandler();
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected CurrentUserSettings vSettings() {
        return TicTic.mAudioSettings;
    }

    protected int virtualKeyHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics);
        } else {
            display.getMetrics(metrics);
        }

        int fullHeight = metrics.heightPixels;

        display.getMetrics(metrics);

        return fullHeight - metrics.heightPixels;
    }

}
