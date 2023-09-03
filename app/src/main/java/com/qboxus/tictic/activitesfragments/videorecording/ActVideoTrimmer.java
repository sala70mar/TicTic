package com.qboxus.tictic.activitesfragments.videorecording;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.gson.Gson;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.FFMPEGFunctions;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.qboxus.tictic.trimmodule.CustomProgressView;
import com.qboxus.tictic.trimmodule.FileUtils;
import com.qboxus.tictic.trimmodule.TrimVideo;
import com.qboxus.tictic.trimmodule.TrimVideoOptions;
import com.qboxus.tictic.trimmodule.TrimmerUtils;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executors;

public class ActVideoTrimmer  extends AppCompatLocaleActivity implements View.OnClickListener{


    private ImageView imagePlayPause,btnNext,btnBack;
    ProgressBar compressionProgress;
    private SimpleDraweeView[] imageViews;
    private long totalDuration;
    private Uri uri;
    private String uriRealPath;
    private TextView txtStartDuration, txtEndDuration;
    private CrystalRangeSeekbar seekbar;
    private long lastMinValue = 0;
    private long lastMaxValue = 0;
    private MenuItem menuDone;
    private CrystalSeekbar seekbarController;
    private boolean isValidVideo = true, isVideoEnded;
    private Handler seekHandler;
    private Bundle bundle;
    private ProgressBar progressBar;
    private TrimVideoOptions trimVideoOptions;

    private long currentDuration, lastClickedTime;
    Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            try {
                currentDuration = videoPlayer.getCurrentPosition() / 1000;
                if (!videoPlayer.getPlayWhenReady())
                    return;
                if (currentDuration <= lastMaxValue)
                    seekbarController.setMinStartValue((int) currentDuration).apply();
                else
                    videoPlayer.setPlayWhenReady(false);
            } finally {
                seekHandler.postDelayed(updateSeekbar, 1000);
            }
        }
    };

    private String outputPath;
    private int trimType;
    private long fixedGap, minGap, minFromGap, maxToGap;
    private boolean hidePlayerSeek;
    private CustomProgressView progressView;
    int recordingDuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(ActVideoTrimmer.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        hideNavigation();
        setContentView(R.layout.activity_act_video_trimmer);

        bundle = getIntent().getExtras();
        recordingDuration=bundle.getInt("recordingDuration",0);
        Gson gson = new Gson();
        String videoOption = bundle.getString(TrimVideo.TRIM_VIDEO_OPTION);
        trimVideoOptions = gson.fromJson(videoOption, TrimVideoOptions.class);
        progressView = new CustomProgressView(this);
        compressionProgress=findViewById(R.id.compressionProgress);
        btnNext=findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
    }


    private void compressionApplyOnVideo(String videoPath) {
        Log.d(Constants.tag,"InputPath: "+videoPath);
        int frameRate=Integer.valueOf(Functions.getTrimVideoFrameRate(new File(""+videoPath).getAbsolutePath()));
        updateCommpressionProgress(true);
        FFMPEGFunctions.INSTANCE.compressVideoHighToLowProcess(ActVideoTrimmer.this,
                new File(""+videoPath)
                ,frameRate
                ,Functions.getSettingsPreference(ActVideoTrimmer.this).getString(Variables.VideoCompression,"2000")
                , new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getString("action").equals("success"))
                        {
                            updateCommpressionProgress(false);
                            uriRealPath=""+bundle.getString("path");
                            uri = Uri.parse(uriRealPath);
                            Log.d(Constants.tag,"OutputPath: "+uriRealPath);
                            Functions.printLog(Constants.tag,"Compressing Done");
                            setDataInView();
                        }
                        else
                        if (bundle.getString("action").equals("failed"))
                        {
                            updateCommpressionProgress(false);
                            Toast.makeText(ActVideoTrimmer.this, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                        else
                        if (bundle.getString("action").equals("cancel"))
                        {
                            updateCommpressionProgress(false);
                            Toast.makeText(ActVideoTrimmer.this, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateCommpressionProgress(boolean isProgress) {
        if (isProgress)
        {
            ivThumbnail.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            compressionProgress.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);
            btnNext.setClickable(false);
        }
        else
        {
            ivThumbnail.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            compressionProgress.setVisibility(View.GONE);
            btnNext.setEnabled(true);
            btnNext.setClickable(true);
        }
    }




    private StyledPlayerView  playerView;
    SimpleDraweeView ivThumbnail;
    private ExoPlayer videoPlayer;
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ivThumbnail=findViewById(R.id.ivThumbnail);
        playerView = findViewById(R.id.player_view_lib);
        imagePlayPause = findViewById(R.id.image_play_pause);
        seekbar = findViewById(R.id.range_seek_bar);
        txtStartDuration = findViewById(R.id.txt_start_duration);
        txtEndDuration = findViewById(R.id.txt_end_duration);
        seekbarController = findViewById(R.id.seekbar_controller);

        progressBar = findViewById(R.id.progress_circular);
        SimpleDraweeView imageOne = findViewById(R.id.image_one);
        SimpleDraweeView imageTwo = findViewById(R.id.image_two);
        SimpleDraweeView imageThree = findViewById(R.id.image_three);
        SimpleDraweeView imageFour = findViewById(R.id.image_four);
        SimpleDraweeView imageFive = findViewById(R.id.image_five);
        SimpleDraweeView imageSix = findViewById(R.id.image_six);
        SimpleDraweeView imageSeven = findViewById(R.id.image_seven);
        SimpleDraweeView imageEight = findViewById(R.id.image_eight);
        imageViews = new SimpleDraweeView[]{imageOne, imageTwo, imageThree,
                imageFour, imageFive, imageSix, imageSeven, imageEight};
        seekHandler = new Handler(Looper.getMainLooper());
       try {
           uri = Uri.parse(bundle.getString(TrimVideo.TRIM_VIDEO_URI));
           uriRealPath= FileUtils.getRealPath(ActVideoTrimmer.this,uri);
           uri = Uri.parse(uriRealPath);
       }catch (Exception e)
       {
           Log.d(Constants.tag,"Exception: "+e);
       }
        setDataInView();
    }


    private void setDataInView() {
        try {
            Runnable fileUriRunnable = () -> {

                runOnUiThread(() -> {
                    Log.d(Constants.tag,"Real uri : "+uri);
                    progressBar.setVisibility(View.GONE);
                    totalDuration = TrimmerUtils.getDuration(ActVideoTrimmer.this, uri);
                    imagePlayPause.setOnClickListener(v ->
                            onVideoClicked());
                    Objects.requireNonNull(playerView.getVideoSurfaceView()).setOnClickListener(v ->
                            onVideoClicked());
                    initTrimData();
                    buildMediaSource(uri);
                    loadThumbnails();
                    setUpSeekBar();
                });
            };
            Executors.newSingleThreadExecutor().execute(fileUriRunnable);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }

    private void initTrimData() {
        try {
            assert trimVideoOptions != null;
            trimType = TrimmerUtils.getTrimType(trimVideoOptions.trimType);
            hidePlayerSeek = trimVideoOptions.hideSeekBar;
            fixedGap = trimVideoOptions.fixedDuration;
            fixedGap = fixedGap != 0 ? fixedGap : totalDuration;
            minGap = trimVideoOptions.minDuration;
            minGap = minGap != 0 ? minGap : totalDuration;
            if (trimType == 3) {
                minFromGap = trimVideoOptions.minToMax[0];
                maxToGap = trimVideoOptions.minToMax[1];
                minFromGap = minFromGap != 0 ? minFromGap : totalDuration;
                maxToGap = maxToGap != 0 ? maxToGap : totalDuration;
            }
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }

    private void onVideoClicked() {
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue);
                videoPlayer.setPlayWhenReady(true);
                return;
            }
            if ((currentDuration - lastMaxValue) > 0)
                seekTo(lastMinValue);
            videoPlayer.setPlayWhenReady(!videoPlayer.getPlayWhenReady());
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }

    private void seekTo(long sec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(sec * 1000);
    }

    private void buildMediaSource(Uri mUri) {
        try {

            videoPlayer = new ExoPlayer.Builder(ActVideoTrimmer.this).
                    setTrackSelector(new DefaultTrackSelector(ActVideoTrimmer.this))
                    .build();

            try {
                MediaItem mediaItem = MediaItem.fromUri(mUri);
                videoPlayer.setMediaItem(mediaItem);
                videoPlayer.prepare();
                videoPlayer.setPlayWhenReady(true);

            }catch (Exception e)
            {
                Log.d(Constants.tag,"Exception: getExoPlayerInit "+e);
            }




            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build();
            videoPlayer.setAudioAttributes(audioAttributes, true);

            videoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                    imagePlayPause.setVisibility(playWhenReady ? View.GONE :
                            View.VISIBLE);
                }

                @Override
                public void onVideoSizeChanged(VideoSize videoSize) {
                    Player.Listener.super.onVideoSizeChanged(videoSize);
                    if (videoSize.width>videoSize.height)
                    {
                        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                    }
                    else
                    {
                        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    }
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state) {
                        case Player.STATE_ENDED:
                            Log.d(Constants.tag,"onPlayerStateChanged: Video ended.");
                            imagePlayPause.setVisibility(View.VISIBLE);
                            isVideoEnded = true;
                            break;
                        case Player.STATE_READY:
                            isVideoEnded = false;
                            startProgress();
                            Log.d(Constants.tag,"onPlayerStateChanged: Ready to play.");
                            break;
                        default:
                            break;
                        case Player.STATE_BUFFERING:
                            Log.d(Constants.tag,"onPlayerStateChanged: STATE_BUFFERING.");
                            break;
                        case Player.STATE_IDLE:
                            Log.d(Constants.tag,"onPlayerStateChanged: STATE_IDLE.");
                            break;
                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    try {
                        ivThumbnail.setController(Functions.frescoImageLoad(Uri.parse(bundle.getString(TrimVideo.TRIM_VIDEO_URI)),false));
                        Variables.isCompressionApplyOnStart=true;
                        if (Variables.isCompressionApplyOnStart)
                        {
                            compressionApplyOnVideo(uriRealPath);
                        }
                    }catch (Exception e)
                    {
                        Log.d(Constants.tag,"Exception: "+e);
                    }
                    finally {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Functions.showToastOnTop(ActVideoTrimmer.this,null,getString(R.string.hold_on_it_take_few_seconds));
                            }
                        },5000);
                    }
                    Log.d(Constants.tag,"Player Error: "+error.getMessage());
                }
            });

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }

        ActVideoTrimmer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                playerView.setPlayer(videoPlayer);

            }
        });
    }

    /*
     *  loading thumbnails
     * */
    private void loadThumbnails() {
        try {
            long diff = totalDuration / 8;
            int sec = 1;
            for (SimpleDraweeView img : imageViews) {
                img.setController(Functions.frescoImageLoad(Uri.parse(bundle.getString(TrimVideo.TRIM_VIDEO_URI)),false));
                if (sec < totalDuration)
                    sec++;
            }
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }

    private void setUpSeekBar() {
        seekbar.setVisibility(View.VISIBLE);
        txtStartDuration.setVisibility(View.VISIBLE);
        txtEndDuration.setVisibility(View.VISIBLE);

        seekbarController.setMaxValue(totalDuration).apply();
        seekbar.setMaxValue(totalDuration).apply();
        seekbar.setMaxStartValue((float) totalDuration).apply();
        if (trimType == 1) {
            seekbar.setFixGap(fixedGap).apply();
            lastMaxValue = totalDuration;
        } else if (trimType == 2) {
            seekbar.setMaxStartValue((float) minGap);
            seekbar.setGap(minGap).apply();
            lastMaxValue = totalDuration;
        } else if (trimType == 3) {
            seekbar.setMaxStartValue((float) maxToGap);
            seekbar.setGap(minFromGap).apply();
            lastMaxValue = maxToGap;
        } else {
            seekbar.setGap(2).apply();
            lastMaxValue = totalDuration;
        }
        if (hidePlayerSeek)
            seekbarController.setVisibility(View.GONE);

        seekbar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            if (!hidePlayerSeek)
                seekbarController.setVisibility(View.VISIBLE);
        });

        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            long minVal = (long) minValue;
            long maxVal = (long) maxValue;
            if (lastMinValue != minVal) {
                seekTo((long) minValue);
                if (!hidePlayerSeek)
                    seekbarController.setVisibility(View.INVISIBLE);
            }
            lastMinValue = minVal;
            lastMaxValue = maxVal;
            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal));
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal));
            if (trimType == 3)
                setDoneColor(minVal, maxVal);
        });

        seekbarController.setOnSeekbarFinalValueListener(value -> {
            long value1 = (long) value;
            if (value1 < lastMaxValue && value1 > lastMinValue) {
                seekTo(value1);
                return;
            }
            if (value1 > lastMaxValue)
                seekbarController.setMinStartValue((int) lastMaxValue).apply();
            else if (value1 < lastMinValue) {
                seekbarController.setMinStartValue((int) lastMinValue).apply();
                if (videoPlayer.getPlayWhenReady())
                    seekTo(lastMinValue);
            }
        });
    }

    /**
     * will be called whenever seekBar range changes
     * it checks max duration is exceed or not.
     * and disabling and enabling done menuItem
     *
     * @param minVal left thumb value of seekBar
     * @param maxVal right thumb value of seekBar
     */
    private void setDoneColor(long minVal, long maxVal) {
        try {
            if (menuDone == null)
                return;
            //changed value is less than maxDuration
            if ((maxVal - minVal) <= maxToGap) {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.white)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = true;
            } else {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.white)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = false;
            }
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
            super.onDestroy();
        if (videoPlayer != null)
            videoPlayer.release();
        if (progressView != null && progressView.isShowing())
            progressView.dismiss();
        stopRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuDone = menu.findItem(R.id.action_done);
        return super.onPrepareOptionsMenu(menu);
    }



    private void trimVideo() {
        if (isValidVideo) {
            //not exceed given maxDuration if has given
            outputPath = Functions.getAppFolder(ActVideoTrimmer.this)+Variables.gallery_trimed_video;
            videoPlayer.setPlayWhenReady(false);

            Log.d(Constants.tag,"startTimeString: "+Functions.getTimeWithAdditionalSecond("HH:mm:ss", (int) lastMinValue)+
                    " endTimeString: "+Functions.getTimeWithAdditionalSecond("HH:mm:ss", (int) lastMaxValue));

            Log.d(Constants.tag,"recordingDuration: "+recordingDuration);
            Log.d(Constants.tag,"Max:Second Allow: "+(lastMaxValue-lastMinValue));
            int allowRecordingDuration= (int) (lastMaxValue-lastMinValue);
            int allowDuration=recordingDuration/1000;
            if (allowRecordingDuration<=allowDuration)
            {

                int frameRate=Integer.valueOf(Functions.getTrimVideoFrameRate(new File(""+uri).getAbsolutePath()));
                Functions.showDeterminentLoader(ActVideoTrimmer.this,false,false,true,ActVideoTrimmer.this.getString(R.string.trimming_));
                FFMPEGFunctions.INSTANCE.trimVideoProcess(new File(""+uri),
                        outputPath, Functions.getTimeWithAdditionalSecond("HH:mm:ss", (int) lastMinValue)
                        , Functions.getTimeWithAdditionalSecond("HH:mm:ss", (int) lastMaxValue)
                        ,frameRate
                        ,Functions.getSettingsPreference(ActVideoTrimmer.this).getString(Variables.VideoCompression,"2000")
                        , new FragmentCallBack() {
                            @Override
                            public void onResponce(Bundle bundle) {
                                if (bundle.getString("action").equals("success"))
                                {
                                    Functions.cancelDeterminentLoader();
                                    Intent intent = new Intent();
                                    intent.putExtra(Variables.gallery_trimed_video, outputPath);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                                else
                                if (bundle.getString("action").equals("failed"))
                                {
                                    Functions.cancelDeterminentLoader();
                                    Toast.makeText(ActVideoTrimmer.this, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                                }
                                else
                                if (bundle.getString("action").equals("cancel"))
                                {
                                    Functions.cancelDeterminentLoader();
                                    Toast.makeText(ActVideoTrimmer.this, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();

                                }
                                else
                                if (bundle.getString("action").equals("process"))
                                {
                                    String message=bundle.getString("message");
                                    try {
                                        int progressPercentage=Functions.CalculateFFMPEGTimeToPercentage(message,allowRecordingDuration);
                                        Functions.showLoadingProgress(progressPercentage);
                                    }
                                    catch (Exception e){
                                        Log.d(Constants.tag,"Exception: "+e);
                                    }

                                }
                            }
                        });

            }
            else
            {
                Toast.makeText(ActVideoTrimmer.this, getText(R.string.your_are_only_allow_maximum)+" "+allowDuration+" "+
                        getText(R.string.second_video), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(this, getString(R.string.txt_smaller) + " " + TrimmerUtils.getLimitedTimeFormatted(maxToGap), Toast.LENGTH_SHORT).show();
    }





    void startProgress() {
        updateSeekbar.run();
    }

    void stopRepeatingTask() {
        seekHandler.removeCallbacks(updateSeekbar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnBack:
            {
                ActVideoTrimmer.super.onBackPressed();
            }
                break;
            case R.id.btnNext:
            {
                //prevent multiple clicks
                if (SystemClock.elapsedRealtime() - lastClickedTime < 800)
                    return ;
                lastClickedTime = SystemClock.elapsedRealtime();
                trimVideo();
            }
                break;
        }
    }

    // this will hide the bottom mobile navigation controll
    public void hideNavigation() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

    }
}
