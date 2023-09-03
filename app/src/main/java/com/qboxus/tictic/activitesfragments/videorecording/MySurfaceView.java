package com.qboxus.tictic.activitesfragments.videorecording;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.Functions;


public class MySurfaceView extends SurfaceView {

    public RelativeLayout relativeLayout;

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);


        // Create a RelativeLayout
        relativeLayout = new RelativeLayout(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Functions.printLog(Constants.tag,"onDraw(Canvas canvas)");
    }
}
