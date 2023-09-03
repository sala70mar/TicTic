package com.qboxus.tictic.activitesfragments.videorecording;

import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.video.VideoSize;
import com.qboxus.tictic.activitesfragments.argear.AppConfig;
import com.qboxus.tictic.activitesfragments.argear.BeautyFragment;
import com.qboxus.tictic.activitesfragments.argear.BulgeFragment;
import com.qboxus.tictic.activitesfragments.argear.GLView;
import com.qboxus.tictic.activitesfragments.argear.StickerFragment;
import com.qboxus.tictic.activitesfragments.argear.api.ContentsResponse;
import com.qboxus.tictic.activitesfragments.argear.camera.ReferenceCamera;
import com.qboxus.tictic.activitesfragments.argear.camera.ReferenceCamera1;
import com.qboxus.tictic.activitesfragments.argear.camera.ReferenceCamera2;
import com.qboxus.tictic.activitesfragments.argear.data.BeautyItemData;
import com.qboxus.tictic.activitesfragments.argear.model.ItemModel;
import com.qboxus.tictic.activitesfragments.argear.network.DownloadAsyncResponse;
import com.qboxus.tictic.activitesfragments.argear.network.DownloadAsyncTask;
import com.qboxus.tictic.activitesfragments.argear.rendering.CameraTexture;
import com.qboxus.tictic.activitesfragments.argear.rendering.ScreenRenderer;
import com.qboxus.tictic.activitesfragments.argear.util.FileDeleteAsyncTask;
import com.qboxus.tictic.activitesfragments.argear.util.PreferenceUtil;
import com.qboxus.tictic.activitesfragments.argear.viewmodel.ContentsViewModel;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.coremedia.iso.boxes.Container;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.interfaces.ProgressBarListener;
import com.qboxus.tictic.simpleclasses.FFMPEGFunctions;
import com.qboxus.tictic.simpleclasses.SegmentedProgressBar;
import com.seerslab.argear.exceptions.InvalidContentsException;
import com.seerslab.argear.exceptions.NetworkException;
import com.seerslab.argear.exceptions.SignedUrlGenerationException;
import com.seerslab.argear.session.ARGAuth;
import com.seerslab.argear.session.ARGContents;
import com.seerslab.argear.session.ARGFrame;
import com.seerslab.argear.session.ARGMedia;
import com.seerslab.argear.session.ARGSession;
import com.seerslab.argear.session.config.ARGCameraConfig;
import com.seerslab.argear.session.config.ARGConfig;
import com.seerslab.argear.session.config.ARGInferenceConfig;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoRecoderDuetA extends AppCompatLocaleActivity implements View.OnClickListener {


    int number = 0;
    ArrayList<String> videopaths = new ArrayList<>();

    ImageButton recordImage;
    ImageButton doneBtn;
    boolean isRecording = false;
    boolean isFlashOn = false;

    LinearLayout tabFlash,tabRotateCam,tabTimer,tabOrientation,tabFeature,tabFunny,tabFilter;
    ImageView ivFlash,ivOrientation;
    TextView tvOrientation;
    SegmentedProgressBar videoProgress;
    LinearLayout cameraOptions;
    ImageView  cutVideoBtn;
    int secPassed = 0;
    long timeInMilis = 0;
    int speedTabPosition=2;
    TextView countdownTimerTxt;
    boolean isRecordingTimerEnable;
    int recordingTime = 3;
    Context context;
    HomeModel item;

    boolean duetOrientation = false;
    LinearLayout tabLayoutOrientation;



    CameraManager mCameraManager;
    String mCameraId;
    private ReferenceCamera mCamera;
    private GLView mGlView;
    private ScreenRenderer mScreenRenderer;
    private CameraTexture mCameraTexture;
    private ARGFrame.Ratio mScreenRatio = ARGFrame.Ratio.RATIO_1_1;
    private String mItemDownloadPath;
    private ItemModel mCurrentStickeritem = null;
    private BeautyItemData mBeautyItemData;
    private boolean mHasTrigger = false;
    private boolean mUseARGSessionDestroy = false;
    private int mDeviceWidth = 0;
    private int mDeviceHeight = 0;
    private int mGLViewWidth = 0;
    private int mGLViewHeight = 0;
    private Toast mTriggerToast = null;
    private ARGSession mARGSession;
    private ARGMedia mARGMedia;
    private ContentsViewModel mContentsViewModel;
    FrameLayout cameraLayout;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(VideoRecoderDuetA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        hideNavigation();
        setContentView(R.layout.activity_video_recoder_duet);
        context=VideoRecoderDuetA.this;
        clearCacheFiles();
        initNewControls();
        cameraOptions = findViewById(R.id.camera_options);
        ivOrientation=findViewById(R.id.ivOrientation);
        tvOrientation=findViewById(R.id.tvOrientation);
        tabOrientation=findViewById(R.id.tabOrientation);
        tabOrientation.setOnClickListener(this);
        tabLayoutOrientation = findViewById(R.id.layout_orientation);
        tabFeature=findViewById(R.id.tabFeature);
        tabFeature.setOnClickListener(this);
        tabFunny=findViewById(R.id.tabFunny);
        tabFunny.setOnClickListener(this);
        tabFilter=findViewById(R.id.tabFilter);
        tabFilter.setOnClickListener(this);

        recordImage = findViewById(R.id.record_image);

        cutVideoBtn = findViewById(R.id.cut_video_btn);
        cutVideoBtn.setVisibility(View.GONE);
        cutVideoBtn.setOnClickListener(this);

        doneBtn = findViewById(R.id.done);
        doneBtn.setEnabled(false);
        doneBtn.setOnClickListener(this);


        tabRotateCam = findViewById(R.id.tabRotateCam);
        tabRotateCam.setOnClickListener(this);
        ivFlash=findViewById(R.id.ivFlash);
        tabFlash = findViewById(R.id.tabFlash);
        tabFlash.setOnClickListener(this);
        tabTimer=findViewById(R.id.tabTimer);
        tabTimer.setOnClickListener(this);

        findViewById(R.id.goBack).setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra("data")) {
            item = (HomeModel) intent.getSerializableExtra("data");
        }



        recordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStopRecording();
            }
        });
        countdownTimerTxt = findViewById(R.id.countdown_timer_txt);

        initializePlayer(Functions.getAppFolder(VideoRecoderDuetA.this) + item.video_id + ".mp4");
        initVideoProgress();


    }


    ExoPlayer exoplayer;
    StyledPlayerView playerView;
    // initlize the player for play video
    private void initializePlayer(String videoAttachment) {
        if(exoplayer==null && item!=null){
            try {

                Constants.RECORDING_DURATION = (int)Functions.getFileDuration(context,Uri.fromFile(new File(videoAttachment)));

                exoplayer =new ExoPlayer.Builder(context).
                        setTrackSelector(new DefaultTrackSelector(context)).
                        setLoadControl(Functions.getExoControler()).
                        build();
                exoplayer.setMediaItem(MediaItem.fromUri(Uri.fromFile(new File(videoAttachment))));
                exoplayer.prepare();
                exoplayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build();
                exoplayer.setAudioAttributes(audioAttributes, true);
                exoplayer.setPlayWhenReady(false);
                exoplayer.addListener(new Player.Listener() {
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
                });
                VideoRecoderDuetA.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerView = findViewById(R.id.playerview);
                        if(exoplayer!=null) {
                            playerView.setPlayer(exoplayer);
                        }
                        playerView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true;
                            }
                        });
                    }
                });

            }
            catch (Exception e)
            {
                Log.d(Constants.tag,"Exception : "+e);
            }

        }

    }


    private void initVideoProgress() {
        videoProgress = findViewById(R.id.video_progress);
        videoProgress.setDividerColor(Color.WHITE);
        videoProgress.setDividerEnabled(true);
        videoProgress.setDividerWidth(4);
        videoProgress.setShader(new int[]{Color.CYAN, Color.CYAN, Color.CYAN});
        setupVideoProgress();
    }

    // initialize the video progress for video recording percentage
    public void setupVideoProgress() {
        videoProgress.enableAutoProgressView(Constants.RECORDING_DURATION);
        secPassed = 0;
        videoProgress.SetListener(new ProgressBarListener() {
            @Override
            public void timeinMill(long mills) {
                timeInMilis = mills;
                secPassed = (int) (mills / 1000);
                if (secPassed > (Constants.RECORDING_DURATION / 1000) - 1) {
                    startOrStopRecording();
                }

                if (isRecordingTimerEnable && secPassed >= recordingTime) {
                    isRecordingTimerEnable = false;
                    startOrStopRecording();
                }

            }
        });

    }

    // if the Recording is stop then it we start the recording
    // and if the mobile is recording the video then it will stop the recording
    public void startOrStopRecording() {

        if (!isRecording && secPassed < (Constants.RECORDING_DURATION / 1000)- 1)
        {
            number = number + 1;

            isRecording = true;

            File file = new File(Functions.getAppFolder(this)+Variables.videoChunk+ (number) + ".mp4");
            videopaths.add(Functions.getAppFolder(this)+Variables.videoChunk+ (number) + ".mp4");

            startRecording(file.getAbsolutePath());

            if (exoplayer != null) {
                exoplayer.setPlayWhenReady(true);
            }

            doneBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_not_done));
            doneBtn.setEnabled(false);
            videoProgress.resume();
            recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_recoding_yes));
            cutVideoBtn.setVisibility(View.GONE);

            cameraOptions.setVisibility(View.GONE);
            tabRotateCam.setVisibility(View.GONE);

        }
        else
        if (isRecording)
        {
            isRecording = false;
            videoProgress.pause();
            videoProgress.addDivider();

            try {
                if (exoplayer != null) {
                    exoplayer.setPlayWhenReady(false);
                }
            }
            catch (Exception e)
            {
                Functions.printLog(Constants.tag,"Exception: "+e);
            }
            try {
                stopRecording();
            }catch (Exception e)
            {
                Log.d(Constants.tag,"Stop cameraView: "+e);
            }


            checkDoneBtnEnable();
            cutVideoBtn.setVisibility(View.VISIBLE);

            recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_recoding_no));

            cameraOptions.setVisibility(View.VISIBLE);
            tabRotateCam.setVisibility(View.VISIBLE);


            Log.d(Constants.tag,"Camera Facing: "+mCamera.isCameraFacingFront());
            applySpeedFunctionality();

        }
        else
        if (secPassed > (Constants.RECORDING_DURATION / 1000)) {
            Functions.showAlert(VideoRecoderDuetA.this, VideoRecoderDuetA.this.getString(R.string.alert), VideoRecoderDuetA.this.getString(R.string.video_only_can_be_a)+" " + (int) Constants.RECORDING_DURATION / 1000 + " S");
        }

    }


    public void applySpeedFunctionality(){
        String intputPath=Functions.getAppFolder(this)+(Variables.videoChunk+number) + ".mp4";
        int second=5;
        try {
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            retriever.setDataSource(intputPath);
            String duration=retriever.extractMetadata(METADATA_KEY_DURATION);
            second= Integer.valueOf(duration)/1000;
        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: "+e);
        }
        int frameRate=Integer.valueOf(Functions.getTrimVideoFrameRate(new File(""+intputPath).getAbsolutePath()));

//        progress synced with two filter
        Functions.showDeterminentLoader(VideoRecoderDuetA.this,false,false,true,context.getString(R.string.rendering_));

        int finalSecond = second;
        FFMPEGFunctions.INSTANCE.videoSpeedProcess(VideoRecoderDuetA.this,intputPath,
                speedTabPosition
                ,frameRate
                ,Functions.getSettingsPreference(VideoRecoderDuetA.this).getString(Variables.VideoCompression,"2000")
                , new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getString("action").equals("success"))
                        {
                            Functions.cancelDeterminentLoader();
                            int index=(videopaths.size()-1);
                            videopaths.remove(index);
                            Log.d(Constants.tag,"index:"+index+" path:"+intputPath);
                            videopaths.add(index,intputPath);
                        }
                        else
                        if (bundle.getString("action").equals("failed"))
                        {
                            Functions.cancelDeterminentLoader();
                            Toast.makeText(context, context.getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                        else
                        if (bundle.getString("action").equals("cancel"))
                        {
                            Functions.cancelDeterminentLoader();
                            Toast.makeText(context, context.getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                        else
                        if (bundle.getString("action").equals("process"))
                        {
                            String message=bundle.getString("message");
                            try {
                                int progressPercentage=Functions.CalculateFFMPEGTimeToPercentage(message, finalSecond);
                                Functions.showLoadingProgress(progressPercentage);
                            }
                            catch (Exception e){}

                        }
                    }
                });

    }


    private void startRecording(String path) {
        if (mCamera == null) {
            return;
        }

        int bitrate = 10 * 1000 * 1000;

        ARGMedia.Ratio ratio;
        if (mScreenRatio == ARGFrame.Ratio.RATIO_FULL) {
            ratio = ARGMedia.Ratio.RATIO_16_9;
        } else if (mScreenRatio == ARGFrame.Ratio.RATIO_4_3) {
            ratio = ARGMedia.Ratio.RATIO_4_3;
        } else {
            ratio = ARGMedia.Ratio.RATIO_1_1;
        }

        int [] previewSize = mCamera.getPreviewSize();


        mARGMedia.initRecorder(path,
                previewSize[0],
                previewSize[1], bitrate,
                false,
                false,
                false,
                ratio);
        mARGMedia.startRecording();

    }



    public void checkDoneBtnEnable() {
        Log.d(Constants.tag,"secPassed: "+secPassed);
        Log.d(Constants.tag,"MIN_TIME_RECORDING: "+(Constants.MIN_TIME_RECORDING / 1000));
        if (secPassed > (Constants.MIN_TIME_RECORDING / 1000)) {
            doneBtn.setImageDrawable(ContextCompat.getDrawable(VideoRecoderDuetA.this,R.drawable.ic_done_red));
            doneBtn.setEnabled(true);
        } else {
            doneBtn.setImageDrawable(ContextCompat.getDrawable(VideoRecoderDuetA.this,R.drawable.ic_not_done));
            doneBtn.setEnabled(false);
        }
    }


    // this will combine all the videos parts in one  fullvideo
    private void combineAllVideos() {
        if (!(videopaths.size()>0))
        {
            return;
        }
        Functions.showDeterminentLoader(VideoRecoderDuetA.this, false, false,false,context.getString(R.string.mixing_));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> video_list = new ArrayList<>();
                for (int i = 0; i < videopaths.size(); i++) {

                    File file = new File(videopaths.get(i));
                    if (file.exists()) {
                        try {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(VideoRecoderDuetA.this, Uri.fromFile(file));
                            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                            boolean isVideo = "yes".equals(hasVideo);

                            if (isVideo && file.length() > 3000) {
                                Functions.printLog("resp", videopaths.get(i));
                                video_list.add(videopaths.get(i));
                            }
                        } catch (Exception e) {
                            Functions.printLog(Constants.tag, e.toString());
                        }
                    }
                }

                try {
                    Movie[] inMovies = new Movie[video_list.size()];
                    for (int i = 0; i < video_list.size(); i++) {

                        inMovies[i] = MovieCreator.build(video_list.get(i));
                    }
                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }
                    Movie result = new Movie();
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }
                    Container out = new DefaultMp4Builder().build(result);
                    String outputFilePath = Functions.getAppFolder(VideoRecoderDuetA.this)+Variables.output_filter_file;
                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.cancelDeterminentLoader();
                            openPostActivity();

                        }
                    });

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: combineVideo: "+e);
                }
            }
        }).start();
    }


    public void removeLastSection(String deleteFilePath) {

        try {

            File file = new File(deleteFilePath);
            if (file.exists()) {

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(context, Uri.fromFile(file));
                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                timeInMillisec= (long) calculateExectChunkTime(videopaths,timeInMilis,timeInMillisec);
                boolean isVideo = "yes".equals(hasVideo);
                if (isVideo) {
                    timeInMilis = timeInMilis - timeInMillisec;
                    videoProgress.removeDivider();
                    videopaths.remove(videopaths.size() - 1);
                    videoProgress.updateProgress(timeInMilis);
                    videoProgress.back_countdown(timeInMillisec);
                    if (exoplayer != null) {
                        int audio_backtime = (int) (exoplayer.getCurrentPosition() - timeInMillisec);
                        if (audio_backtime < 0)
                            audio_backtime = 0;

                        exoplayer.seekTo(audio_backtime);
                    }

                    secPassed = (int) (timeInMilis / 1000);

                    checkDoneBtnEnable();

                }

                Functions.clearFilesCacheBeforeOperation(file);
            }

            if (videopaths.isEmpty()) {
                cutVideoBtn.setVisibility(View.GONE);
                tabRotateCam.setVisibility(View.VISIBLE);

                exoplayer=null;
                initializePlayer(Functions.getAppFolder(VideoRecoderDuetA.this) + item.video_id + ".mp4");

            }

        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"removeLastSection: "+e);
        }
    }

    private long calculateExectChunkTime(ArrayList<String> videopaths, long totalTime,long chunkTime) {
        for (String path: videopaths)
        {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.fromFile(new File(path)));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            totalTime=(totalTime-Long.parseLong(time));
        }
        long adjustedTime=totalTime/videopaths.size();
        adjustedTime=adjustedTime+chunkTime;
        return adjustedTime;
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tabRotateCam:
            {
                mARGSession.pause();
                mCamera.changeCameraFacing();
                mARGSession.resume();
            }
            break;

            case R.id.done:
            {
                combineAllVideos();
            }
            break;
            case R.id.tabOrientation:
                if (duetOrientation) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) findViewById(R.id.layout_orientation).getLayoutParams();
                    layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
                    tabLayoutOrientation.setLayoutParams(layoutParams);
                    tabLayoutOrientation.setOrientation(LinearLayout.VERTICAL);


                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    ivOrientation.animate().rotation(0f).setDuration(500).start();
                    tvOrientation.setText(context.getString(R.string.horizontal));

                    duetOrientation = false;
                } else {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) findViewById(R.id.layout_orientation).getLayoutParams();
                    layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
                    tabLayoutOrientation.setLayoutParams(layoutParams);
                    tabLayoutOrientation.setOrientation(LinearLayout.HORIZONTAL);


                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    ivOrientation.animate().rotation(90f).setDuration(500).start();
                    tvOrientation.setText(context.getString(R.string.vertical));
                    duetOrientation = true;
                }
                break;

            case R.id.cut_video_btn:

                Functions.showAlert(VideoRecoderDuetA.this, "", getString(R.string.descard_the_last_clip_), getString(R.string.delete).toUpperCase(), getString(R.string.cancel_).toUpperCase(), new Callback() {
                    @Override
                    public void onResponce(String resp) {
                        if (resp.equalsIgnoreCase("yes")) {
                            if (videopaths.size() > 0) {
                                removeLastSection(videopaths.get(videopaths.size() - 1));
                            }
                        }
                    }
                });
                break;
            case R.id.tabFeature:
            {
                openFeatureDialogue();
            }
            break;
            case R.id.tabFunny:
            {
                openFunnyDialogue();
            }
            break;
            case R.id.tabFilter:
            {
                openFilterDialogue();
            }
            break;

            case R.id.tabFlash:
            {
                if (isFlashOn) {
                    try {
                        mCameraManager.setTorchMode(mCameraId, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                    isFlashOn = false;
                    ivFlash.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_flash_on));
                } else {

                    try {
                        mCameraManager.setTorchMode(mCameraId, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                    isFlashOn = true;
                    ivFlash.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_flash_off));
                }
            }
            break;

            case R.id.goBack:
                onBackPressed();
                break;

            case R.id.tabTimer:
            {
                if (secPassed + 1 < Constants.RECORDING_DURATION / 1000) {
                    RecordingTimeRangF recordingTimeRang_f = new RecordingTimeRangF(new FragmentCallBack() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if (bundle != null) {
                                isRecordingTimerEnable = true;
                                recordingTime = bundle.getInt("end_time");
                                countdownTimerTxt.setText("3");
                                countdownTimerTxt.setVisibility(View.VISIBLE);
                                recordImage.setClickable(false);
                                final Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                new CountDownTimer(4000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                        countdownTimerTxt.setText("" + (millisUntilFinished / 1000));
                                        countdownTimerTxt.setAnimation(scaleAnimation);

                                    }

                                    @Override
                                    public void onFinish() {
                                        recordImage.setClickable(true);
                                        countdownTimerTxt.setVisibility(View.GONE);
                                        startOrStopRecording();
                                    }
                                }.start();

                            }
                        }
                    });
                    Bundle bundle = new Bundle();
                    if (secPassed < (Constants.RECORDING_DURATION / 1000) - 3)
                        bundle.putInt("end_time", (secPassed + 3));
                    else
                        bundle.putInt("end_time", (secPassed + 1));

                    bundle.putInt("total_time", (Constants.RECORDING_DURATION / 1000));
                    recordingTimeRang_f.setArguments(bundle);
                    recordingTimeRang_f.show(getSupportFragmentManager(), "");
                }
            }
            break;
        }

    }


    private void openFunnyDialogue() {
        BulgeFragment fragment = new BulgeFragment();
        fragment.show(getSupportFragmentManager(), "BulgeFragment");
    }

    private void openFeatureDialogue() {
        BeautyFragment fragment = new BeautyFragment();
        Bundle args = new Bundle();
        args.putSerializable(BeautyFragment.BEAUTY_PARAM1, mScreenRatio);
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), "BeautyFragment");
    }

    private void openFilterDialogue() {
        StickerFragment fragment = new StickerFragment();
        fragment.show(getSupportFragmentManager(), "StickerFragment");
    }


    private void initNewControls() {
        mContentsViewModel = new ViewModelProvider(this).get(ContentsViewModel.class);
        mContentsViewModel.getContents().observe(this, new Observer<ContentsResponse>() {
            @Override
            public void onChanged(ContentsResponse contentsResponse) {
                if (contentsResponse == null) return;
                setLastUpdateAt(context, contentsResponse.lastUpdatedAt);
            }
        });
        mBeautyItemData = new BeautyItemData();

        Point realSize = new Point();
        Display display= ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getRealSize(realSize);
        mDeviceWidth = realSize.x;
        mDeviceHeight = realSize.y;
        mGLViewWidth = realSize.x;
        mGLViewHeight = realSize.y;
        mItemDownloadPath = getFilesDir().getAbsolutePath();

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        progressBar=findViewById(R.id.progressBar);
        tabFlash = findViewById(R.id.tabFlash);
        tabFlash.setOnClickListener(this);
        ivFlash=findViewById(R.id.ivFlash);

        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
            boolean isFlashAvailable = getApplicationContext().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if(!isFlashAvailable)
                tabFlash.setVisibility(View.GONE);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
            Functions.printLog(Constants.tag,e.toString());
        }


    }




    protected void onresume() {
        super.onResume();

        if (mARGSession == null) {


            ARGConfig config
                    = new ARGConfig(Constants.API_URL, Constants.API_KEY_ARGEAR, Constants.SECRET_KEY, Constants.AUTH_KEY);
            Set<ARGInferenceConfig.Feature> inferenceConfig = EnumSet.of(ARGInferenceConfig.Feature.FACE_HIGH_TRACKING);

            mARGSession = new ARGSession(VideoRecoderDuetA.this, config, inferenceConfig);
            mARGMedia = new ARGMedia(mARGSession);

            mScreenRenderer = new ScreenRenderer();
            mCameraTexture = new CameraTexture();


            setBeauty(mBeautyItemData.getBeautyValues());
            initGLView();
            initCamera();

        }

        mCamera.startCamera();
        mARGSession.resume();

        setGLViewSize(mCamera.getPreviewSize());
    }

    private void setGLViewSize(int [] cameraPreviewSize) {
        int previewWidth = cameraPreviewSize[1];
        int previewHeight = cameraPreviewSize[0];

        if (mScreenRatio == ARGFrame.Ratio.RATIO_FULL) {
            mGLViewHeight = mDeviceHeight;
            mGLViewWidth = (int) ((float) mDeviceHeight * previewWidth / previewHeight );
        } else {
            mGLViewWidth = mDeviceWidth;
            mGLViewHeight = (int) ((float) mDeviceWidth * previewHeight / previewWidth);
        }

        if (mGlView != null
                && (mGLViewWidth != mGlView.getViewWidth()
                || mGLViewHeight != mGlView.getViewHeight())) {
            cameraLayout.removeView(mGlView);

            mGlView.getHolder().setFixedSize(mGLViewWidth, mGLViewHeight);
            cameraLayout.addView(mGlView);
        }
    }

    private void initGLView() {
        cameraLayout = findViewById(R.id.camera_layout);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mGlView = new GLView(this, glViewListener);
        mGlView.setZOrderMediaOverlay(false);

        cameraLayout.addView(mGlView, params);
    }

    private void initCamera() {
        if (AppConfig.USE_CAMERA_API == 1) {
            mCamera = new ReferenceCamera1(VideoRecoderDuetA.this, cameraListener);
        } else {
            mCamera = new ReferenceCamera2(this, cameraListener, getWindowManager().getDefaultDisplay().getRotation());
        }
    }


    GLView.GLViewListener glViewListener = new GLView.GLViewListener() {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mScreenRenderer.create(gl, config);
            mCameraTexture.createCameraTexture();
        }

        @Override
        public void onDrawFrame(GL10 gl, int width, int height) {
            if (mCameraTexture.getSurfaceTexture() == null) {
                return;
            }

            if (mCamera != null) {
                mCamera.setCameraTexture(mCameraTexture.getTextureId(), mCameraTexture.getSurfaceTexture());
            }

            ARGFrame frame = mARGSession.drawFrame(gl, mScreenRatio, width, height);
            mScreenRenderer.draw(frame, width, height);

            if (mHasTrigger) updateTriggerStatus(frame.getItemTriggerFlag());

            if (mARGMedia != null) {
                if (mARGMedia.isRecording()) mARGMedia.updateFrame(frame.getTextureId());
            }

            if(mUseARGSessionDestroy)
                mARGSession.destroy();
        }
    };

    ReferenceCamera.CameraListener cameraListener = new ReferenceCamera.CameraListener() {
        @Override
        public void setConfig(int previewWidth, int previewHeight, float verticalFov, float horizontalFov, int orientation, boolean isFrontFacing, float fps) {
            mARGSession.setCameraConfig(new ARGCameraConfig(previewWidth,
                    previewHeight,
                    verticalFov,
                    horizontalFov,
                    orientation,
                    isFrontFacing,
                    fps));
        }


        @Override
        public void feedRawData(byte[] data) {
            mARGSession.feedRawData(data);
        }
        // endregion

        // region - for camera api 2
        @Override
        public void feedRawData(Image data) {
            mARGSession.feedRawData(data);
        }
        // endregion
    };


    public void updateTriggerStatus(final int triggerstatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mCurrentStickeritem != null && mHasTrigger) {
                    String strTrigger = null;
                    if ((triggerstatus & 1) != 0) {
                        strTrigger = "Open your mouth.";
                    } else if ((triggerstatus & 2) != 0) {
                        strTrigger = "Move your head side to side.";
                    } else if ((triggerstatus & 8) != 0) {
                        strTrigger = "Blink your eyes.";
                    } else {
                        if (mTriggerToast != null) {
                            mTriggerToast.cancel();
                            mTriggerToast = null;
                        }
                    }

                    if (strTrigger != null) {
                        mTriggerToast = Toast.makeText(context, strTrigger, Toast.LENGTH_SHORT);
                        mTriggerToast.setGravity(Gravity.CENTER, 0, 0);
                        mTriggerToast.show();
                        mHasTrigger = false;
                    }
                }
            }
        });
    }

    public void setFilter(ItemModel item) {

        String filePath = mItemDownloadPath + "/" + item.uuid;
        if (getLastUpdateAt(context) > getFilterUpdateAt(context, item.uuid)) {
            new FileDeleteAsyncTask(new File(filePath), new FileDeleteAsyncTask.OnAsyncFileDeleteListener() {
                @Override
                public void processFinish(Object result) {
                    Functions.printLog(Constants.tag,"file delete success!");

                    setFilterUpdateAt(context, item.uuid, getLastUpdateAt(context));
                    requestSignedUrl(item, filePath, false);
                }
            }).execute();
        } else {
            if (new File(filePath).exists()) {
                setItem(ARGContents.Type.FilterItem, filePath, item);
            } else {
                requestSignedUrl(item, filePath, false);
            }
        }
    }

    public void setItem(ARGContents.Type type, String path, ItemModel itemModel) {

        mCurrentStickeritem = null;
        mHasTrigger = false;

        mARGSession.contents().setItem(type, path, itemModel.uuid, new ARGContents.Callback() {
            @Override
            public void onSuccess() {
                if (type == ARGContents.Type.ARGItem) {
                    mCurrentStickeritem = itemModel;
                    mHasTrigger = itemModel.hasTrigger;
                }
            }

            @Override
            public void onError(Throwable e) {
                mCurrentStickeritem = null;
                mHasTrigger = false;
                if (e instanceof InvalidContentsException) {
                    Functions.printLog(Constants.tag,"InvalidContentsException");
                }
            }
        });
    }

    private void requestSignedUrl(ItemModel item, String path, final boolean isArItem) {
        progressBar.setVisibility(View.VISIBLE);
        mARGSession.auth().requestSignedUrl(item.zipFileUrl, item.title, item.type, new ARGAuth.Callback() {
            @Override
            public void onSuccess(String url) {
                requestDownload(path, url, item, isArItem);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof SignedUrlGenerationException) {

                    Functions.printLog(Constants.tag,"SignedUrlGenerationException !! ");
                } else if (e instanceof NetworkException) {
                    Functions.printLog(Constants.tag,"NetworkException !!");
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void requestDownload(String targetPath, String url, ItemModel item, boolean isSticker) {
        new DownloadAsyncTask(targetPath, url, new DownloadAsyncResponse() {
            @Override
            public void processFinish(boolean result) {
                progressBar.setVisibility(View.INVISIBLE);
                if (result) {
                    if (isSticker) {
                        setItem(ARGContents.Type.ARGItem, targetPath, item);
                    } else {
                        setItem(ARGContents.Type.FilterItem, targetPath, item);
                    }
                    Functions.printLog(Constants.tag,"download success!");
                } else {
                    Functions.printLog(Constants.tag, "download failed!");
                }
            }
        }).execute();
    }

    public void setSticker(ItemModel item) {
        String filePath = mItemDownloadPath + "/" + item.uuid;
        if (getLastUpdateAt(context) > getStickerUpdateAt(context, item.uuid)) {
            new FileDeleteAsyncTask(new File(filePath), new FileDeleteAsyncTask.OnAsyncFileDeleteListener() {
                @Override
                public void processFinish(Object result) {
                    Functions.printLog(Constants.tag,"file delete success!");

                    setStickerUpdateAt(context, item.uuid, getLastUpdateAt(context));
                    requestSignedUrl(item, filePath, true);
                }
            }).execute();
        } else {
            if (new File(filePath).exists()) {
                setItem(ARGContents.Type.ARGItem, filePath, item);
            } else {
                requestSignedUrl(item, filePath, true);
            }
        }
    }


    public void setMeasureSurfaceView(View view) {
        if (view.getParent() instanceof FrameLayout) {
            view.setLayoutParams(new FrameLayout.LayoutParams(mGLViewWidth, mGLViewHeight));
        }

        else if(view.getParent() instanceof RelativeLayout) {
            view.setLayoutParams(new RelativeLayout.LayoutParams(mGLViewWidth, mGLViewHeight));
        }

        if ((mScreenRatio == ARGFrame.Ratio.RATIO_FULL) && (mGLViewWidth > mDeviceWidth)) {
            view.setX((mDeviceWidth - mGLViewWidth) / 2);
        } else {
            view.setX(0);
        }
    }

    public void setBulgeFunType(int type) {
        ARGContents.BulgeType bulgeType = ARGContents.BulgeType.NONE;
        switch (type) {
            case 1:
                bulgeType = ARGContents.BulgeType.FUN1;
                break;
            case 2:
                bulgeType = ARGContents.BulgeType.FUN2;
                break;
            case 3:
                bulgeType = ARGContents.BulgeType.FUN3;
                break;
            case 4:
                bulgeType = ARGContents.BulgeType.FUN4;
                break;
            case 5:
                bulgeType = ARGContents.BulgeType.FUN5;
                break;
            case 6:
                bulgeType = ARGContents.BulgeType.FUN6;
                break;
        }
        mARGSession.contents().setBulge(bulgeType);
    }


    public int getGLViewWidth() {
        return mGLViewWidth;
    }

    public int getGLViewHeight() {
        return mGLViewHeight;
    }

    public void setBeauty(float[] params) {
        mARGSession.contents().setBeauty(params);
    }

    private long getStickerUpdateAt(Context context, String itemId) {
        return PreferenceUtil.getLongValue(context, AppConfig.USER_PREF_NAME_STICKER, itemId);
    }

    private void setStickerUpdateAt(Context context, String itemId, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME_STICKER, itemId, updateAt);
    }

    private long getLastUpdateAt(Context context) {
        return PreferenceUtil.getLongValue(context, AppConfig.USER_PREF_NAME, "ContentLastUpdateAt");
    }
    private long getFilterUpdateAt(Context context, String itemId) {
        return PreferenceUtil.getLongValue(context, AppConfig.USER_PREF_NAME_FILTER, itemId);
    }
    private void setFilterUpdateAt(Context context, String itemId, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME_FILTER, itemId, updateAt);
    }

    private void setLastUpdateAt(Context context, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME, "ContentLastUpdateAt", updateAt);
    }

    public BeautyItemData getBeautyItemData() {
        return mBeautyItemData;
    }

    public void clearStickers() {
        mCurrentStickeritem = null;
        mHasTrigger = false;

        mARGSession.contents().clear(ARGContents.Type.ARGItem);
    }

    public void clearBulge() {
        mARGSession.contents().clear(ARGContents.Type.Bulge);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onresume();
    }

    @Override
    protected void onDestroy() {
        releaseResources();
        ondestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mARGSession != null) {
            mCamera.stopCamera();
            mARGSession.pause();
        }
    }

    protected void ondestroy() {
        if (mARGSession != null) {
            mCamera.destroy();
            mUseARGSessionDestroy = true;
        }
    }

    private void stopRecording() {
        mARGMedia.stopRecording();
    }

    public void releaseResources() {
        try {
            if (exoplayer != null) {
                exoplayer.setPlayWhenReady(false);
            }
            stopRecording();
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.are_you_sure_if_you_back))
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        releaseResources();

                        finish();
                        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

                    }
                }).show();


    }



    public void openPostActivity() {
        String duet = "";
        if (duetOrientation)
            duet = "h";
        else
            duet = "v";
        Variables.isCompressionApplyOnStart=false;
        Intent intent = new Intent(this, PostVideoA.class);
        intent.putExtra("duet_video_id", item.video_id);
        intent.putExtra("duet_orientation", duet);
        intent.putExtra("duet_video_username", item.username);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }


    private void clearCacheFiles() {
        removeAllFilesIntoDir(Functions.getAppFolder(context)+Variables.APP_HIDED_FOLDER);
        removeAllFilesIntoDir(Functions.getAppFolder(context)+Variables.APP_STORY_EDITED_FOLDER);
        removeAllFilesIntoDir(Functions.getAppFolder(context)+Variables.APP_OUTPUT_FOLDER);
    }

    private void removeAllFilesIntoDir(String dirPath) {
        Log.d("Files__", "DirPath: " + dirPath);
        File directory = new File(dirPath);
        if (directory.exists())
        {
            File[] files = directory.listFiles();
            Log.d("Files__", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                Log.d("Files__", "FileName:" + files[i].getAbsolutePath());
                Functions.clearFilesCacheBeforeOperation(files[i]);
            }
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
