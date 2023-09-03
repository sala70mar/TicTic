package com.qboxus.tictic.activitesfragments.videorecording;

import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coremedia.iso.boxes.Container;
import com.google.android.material.tabs.TabLayout;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
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
import com.qboxus.tictic.activitesfragments.profile.settings.QrCodeProfileA;
import com.qboxus.tictic.activitesfragments.soundlists.SoundListMainA;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.adapters.PhotoUploadAdapter;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.interfaces.ProgressBarListener;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.FFMPEGFunctions;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.SegmentedProgressBar;
import com.qboxus.tictic.simpleclasses.Variables;
import com.qboxus.tictic.trimmodule.TrimType;
import com.qboxus.tictic.trimmodule.TrimVideo;
import com.qboxus.tictic.trimmodule.TrimmerUtils;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoRecoderA  extends AppCompatLocaleActivity implements View.OnClickListener {


    int number = 0;

    ArrayList<String> videopaths = new ArrayList<>();

    ImageButton recordImage;
    ImageButton doneBtn;
    boolean isRecording = false;
    boolean isFlashOn = false;
    String isSelected;
    ImageView ivFlash;
    LinearLayout tabFlash,tabRotateCam;
    SegmentedProgressBar videoProgress;
    LinearLayout cameraOptions,photoSlideOptions;
    RecyclerView photosRecyclerview;
    PhotoUploadAdapter photoUploadAdapter;
    ArrayList<String> uploadPhotoPath=new ArrayList<>();
    ImageView cutVideoBtn;
    protected TabLayout speedSelectionTab;
    int speedTabPosition=2;
    boolean isSpeedMode =true;
    Context context;

    TextView addSoundTxt;

    int secPassed = 0;
    long timeInMilis = 0;

    TextView countdownTimerTxt;
    boolean isRecordingTimerEnable;
    int recordingTime = 3;

    TextView shortVideoTimeTxt, longVideoTimeTxt;
    TextView tvUploadStory, tvUploadVideo,tvUploadPhoto;
    String videoType="Video";
    int timerSelectedDuration = 30*1000;
    RelativeLayout tabVideoLength;
    LinearLayout tabSpeed,tabTimer,tabFeature,tabFunny,tabFilter;
    ProgressBar progressBar;


    CameraManager mCameraManager;
    String mCameraId;
    private ReferenceCamera mCamera;
    private GLView mGlView;
    private ScreenRenderer mScreenRenderer;
    private CameraTexture mCameraTexture;
    private ARGFrame.Ratio mScreenRatio = ARGFrame.Ratio.RATIO_FULL;
    private String mItemDownloadPath;
    private boolean mIsShooting = false;
    private boolean mFilterVignette = false;
    private boolean mFilterBlur = false;
    private int mFilterLevel = 100;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        hideNavigation();
        setContentView(R.layout.activity_video_recoder);
        context=VideoRecoderA.this;

        initNewControls();

        Variables.selectedSoundId = "null";
        Constants.RECORDING_DURATION = 15*1000;
        clearCacheFiles();
        photoSlideOptions=findViewById(R.id.photoSlideOptions);
        cameraOptions = findViewById(R.id.cameraOptions);
        recordImage = findViewById(R.id.record_image);
        speedSelectionTab = (TabLayout) findViewById(R.id.speedSelectionTab);
        setupPhotoSlideAdapter();
        setupSpeedTab();
        findViewById(R.id.upload_layout).setOnClickListener(this);

        cutVideoBtn = findViewById(R.id.cut_video_btn);
        cutVideoBtn.setVisibility(View.GONE);
        cutVideoBtn.setOnClickListener(this);

        doneBtn = findViewById(R.id.done);
        doneBtn.setEnabled(false);
        doneBtn.setOnClickListener(this);

        tabVideoLength=findViewById(R.id.tabVideoLength);
        shortVideoTimeTxt = findViewById(R.id.short_video_time_txt);
        longVideoTimeTxt = findViewById(R.id.long_video_time_txt);
        shortVideoTimeTxt.setOnClickListener(this);
        longVideoTimeTxt.setOnClickListener(this);


        tvUploadStory = findViewById(R.id.tvUploadStory);
        tvUploadVideo = findViewById(R.id.tvUploadVideo);
        tvUploadPhoto=findViewById(R.id.tvUploadPhoto);
        tvUploadStory.setOnClickListener(this);
        tvUploadVideo.setOnClickListener(this);
        tvUploadPhoto.setOnClickListener(this);


        tabRotateCam = findViewById(R.id.tabRotateCam);
        tabRotateCam.setOnClickListener(this);

        findViewById(R.id.goBack).setOnClickListener(this);

        addSoundTxt = findViewById(R.id.add_sound_txt);
        addSoundTxt.setOnClickListener(this);

        tabTimer=findViewById(R.id.tabTimer);
        tabTimer.setOnClickListener(this);
        tabSpeed=findViewById(R.id.tabSpeed);
        tabSpeed.setOnClickListener(this);
        tabFeature=findViewById(R.id.tabFeature);
        tabFeature.setOnClickListener(this);
        tabFunny=findViewById(R.id.tabFunny);
        tabFunny.setOnClickListener(this);
        tabFilter=findViewById(R.id.tabFilter);
        tabFilter.setOnClickListener(this);



        Intent intent = getIntent();
        if (intent.hasExtra("sound_name")) {
            addSoundTxt.setText(intent.getStringExtra("sound_name"));
            Variables.selectedSoundId = intent.getStringExtra("sound_id");
            isSelected = intent.getStringExtra("isSelected");
            tabVideoLength.setVisibility(View.INVISIBLE);
            findViewById(R.id.tabVideoTypeSelection).setVisibility(View.INVISIBLE);
            preparedAudio();
        }


        recordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStopRecording();
            }
        });
        countdownTimerTxt = findViewById(R.id.countdown_timer_txt);

        initVideoProgress();
    }

    private void setupPhotoSlideAdapter() {
        photosRecyclerview=findViewById(R.id.photosRecyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        photosRecyclerview.setLayoutManager(layoutManager);
        ItemTouchHelper itemDecor = new ItemTouchHelper((ItemTouchHelper.Callback)(new ItemTouchHelper.SimpleCallback(3, 0) {
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                String fromItem=uploadPhotoPath.get(fromPos);
                String toItem=uploadPhotoPath.get(toPos);
                uploadPhotoPath.set(fromPos,toItem);
                uploadPhotoPath.set(toPos,fromItem);
                photoUploadAdapter.notifyItemMoved(fromPos, toPos);
                return true;
            }
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        }));
        itemDecor.attachToRecyclerView(photosRecyclerview);
        photoUploadAdapter=new PhotoUploadAdapter(uploadPhotoPath, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                String itemUpdated=uploadPhotoPath.get(pos);
                if (view.getId()==R.id.ivDeletePhoto)
                {
                    uploadPhotoPath.remove(itemUpdated);
                    photoUploadAdapter.notifyDataSetChanged();
                    updatePhotoUploadStatus();
                }
            }
        });
        photosRecyclerview.setAdapter(photoUploadAdapter);
    }

    private void clearCacheFiles() {
        removeAllFilesIntoDir(Functions.getAppFolder(context)+Variables.APP_HIDED_FOLDER);
        removeAllFilesIntoDir(Functions.getAppFolder(context)+Variables.APP_STORY_EDITED_FOLDER);
        removeAllFilesIntoDir(Functions.getAppFolder(context)+Variables.APP_OUTPUT_FOLDER);
    }

    private void initNewControls() {
        mContentsViewModel = new ViewModelProvider(this).get(ContentsViewModel.class);
        mContentsViewModel.getContents().observe(this, new Observer<ContentsResponse>() {
            @Override
            public void onChanged(ContentsResponse contentsResponse) {
                if (contentsResponse == null) return;
                setLastUpdateAt(VideoRecoderA.this, contentsResponse.lastUpdatedAt);
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

    private void setLastUpdateAt(Context context, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME, "ContentLastUpdateAt", updateAt);
    }

    private void initVideoProgress() {
        videoProgress = findViewById(R.id.video_progress);
        videoProgress.setDividerColor(Color.WHITE);
        videoProgress.setDividerEnabled(true);
        videoProgress.setDividerWidth(4);
        videoProgress.setShader(new int[]{Color.CYAN, Color.CYAN, Color.CYAN});
        setupVideoProgress();
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

    private void setupSpeedTab() {
        speedSelectionTab.addTab(speedSelectionTab.newTab().setText(context.getString(R.string.speed_scale_one)));
        speedSelectionTab.addTab(speedSelectionTab.newTab().setText(context.getString(R.string.speed_scale_two)));
        speedSelectionTab.addTab(speedSelectionTab.newTab().setText(context.getString(R.string.speed_scale_three)));
        speedSelectionTab.addTab(speedSelectionTab.newTab().setText(context.getString(R.string.speed_scale_four)));
        speedSelectionTab.addTab(speedSelectionTab.newTab().setText(context.getString(R.string.speed_scale_five)));
        speedSelectionTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                TextView title = v.findViewById(R.id.text);
                title.setTextColor(ContextCompat.getColor(context,R.color.blackColor));
                title.setBackground(ContextCompat.getDrawable(context,R.drawable.ractengle_less_round_solid_white));
                tab.setCustomView(v);
                speedTabPosition=tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                TextView title = v.findViewById(R.id.text);
                title.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                title.setBackground(ContextCompat.getDrawable(context,R.drawable.ractengle_transprent));
                tab.setCustomView(v);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setupTabIcons();
    }

    // Bottom tabs when we open an activity
    private void setupTabIcons() {
        speedSelectionTab.getTabAt(0).setCustomView(getCustomTabView(context.getString(R.string.speed_scale_one),ContextCompat.getColor(context,R.color.graycolor2),R.drawable.ractengle_transprent));
        speedSelectionTab.getTabAt(1).setCustomView(getCustomTabView(context.getString(R.string.speed_scale_two),ContextCompat.getColor(context,R.color.graycolor2),R.drawable.ractengle_transprent));
        speedSelectionTab.getTabAt(2).setCustomView(getCustomTabView(context.getString(R.string.speed_scale_three),ContextCompat.getColor(context,R.color.graycolor2),R.drawable.ractengle_transprent));
        speedSelectionTab.getTabAt(3).setCustomView(getCustomTabView(context.getString(R.string.speed_scale_four),ContextCompat.getColor(context,R.color.graycolor2),R.drawable.ractengle_transprent));
        speedSelectionTab.getTabAt(4).setCustomView(getCustomTabView(context.getString(R.string.speed_scale_five),ContextCompat.getColor(context,R.color.graycolor2),R.drawable.ractengle_transprent));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                speedSelectionTab.getTabAt(speedTabPosition).select();
            }
        },1000);
    }

    private View getCustomTabView(String title, int color, int background) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_speed_tablayout, null);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(title);
        textView.setTextColor(color);
        textView.setBackground(ContextCompat.getDrawable(context,background));
        return view;
    }

    // start trimming activity
    ActivityResultLauncher<Intent> takeOrSelectVideoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (TrimmerUtils.getDuration(VideoRecoderA.this,data.getData())<Constants.MIN_TRIM_TIME){
                            Toast.makeText(context,getString(R.string.video_must_be_larger_then_second),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (data.getData() != null) {
                            openTrimActivity(String.valueOf(data.getData()));
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> videoTrimResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData(),Variables.gallery_trimed_video));

                        String filepath = String.valueOf(uri);

                        changeVideoSize(filepath, Functions.getAppFolder(context)+Variables.gallery_resize_video);

                    } else
                        Log.d(Constants.tag,"videoTrimResultLauncher data is null");
                }
            });




    // initialize the video progress for video recording percentage
    public void setupVideoProgress() {
        videoProgress.enableAutoProgressView(Constants.RECORDING_DURATION);
        secPassed = 0;
        videoProgress.SetListener(new ProgressBarListener() {
            @Override
            public void timeinMill(long mills) {
                Log.d("timeinMill","timeinMill: "+mills);
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

        if (videoType.equals("Photo"))
        {
            mIsShooting = true;
        }
        else
        {
            if (!isRecording && secPassed < (Constants.RECORDING_DURATION / 1000) - 1)
            {
                number = number + 1;

                isRecording = true;

                File file = new File(Functions.getAppFolder(this)+Variables.videoChunk+ (number) + ".mp4");
                videopaths.add(Functions.getAppFolder(this)+Variables.videoChunk+ (number) + ".mp4");

                startRecording(file.getAbsolutePath());

                if (audio != null) {
                    audio.start();
                }

                doneBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_not_done));
                doneBtn.setEnabled(false);
                videoProgress.resume();
                recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_recoding_yes));
                cutVideoBtn.setVisibility(View.GONE);

                findViewById(R.id.tabVideoTypeSelection).setVisibility(View.INVISIBLE);
                tabVideoLength.setVisibility(View.INVISIBLE);
                findViewById(R.id.upload_layout).setEnabled(false);
                cameraOptions.setVisibility(View.GONE);
                photoSlideOptions.setVisibility(View.GONE);
                speedSelectionTab.setVisibility(View.GONE);
                addSoundTxt.setClickable(false);
                tabRotateCam.setVisibility(View.GONE);

            }
            else
            if (isRecording)
            {
                isRecording = false;
                videoProgress.pause();
                videoProgress.addDivider();

                try {
                    if (audio != null) {
                        if (audio.isPlaying())
                        {
                            audio.pause();
                        }
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

                findViewById(R.id.upload_layout).setEnabled(true);
                if (videoType.equals("Video"))
                {
                    recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_recoding_no));
                }
                else
                if (videoType.equals("Photo"))
                {
                    photoSlideOptions.setVisibility(View.VISIBLE);
                    recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_capture_photo));
                }
                else
                {
                    recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_recoding_story_no));
                }
                if (isSpeedMode)
                {
                    speedSelectionTab.setVisibility(View.VISIBLE);
                }
                cameraOptions.setVisibility(View.VISIBLE);
                tabRotateCam.setVisibility(View.VISIBLE);


                Log.d(Constants.tag,"Camera Facing: "+mCamera.isCameraFacingFront());
                applySpeedFunctionality();

            }
            else
            if (secPassed > (Constants.RECORDING_DURATION / 1000)) {
                Functions.showAlert(VideoRecoderA.this, VideoRecoderA.this.getString(R.string.alert), VideoRecoderA.this.getString(R.string.video_only_can_be_a)+" " + (int) Constants.RECORDING_DURATION / 1000 + " S");
            }

        }

    }


    private void takePictureOnGlThread(int textureId) {
        mIsShooting = false;
        ARGMedia.Ratio ratio;
        if (mScreenRatio == ARGFrame.Ratio.RATIO_FULL) {
            ratio = ARGMedia.Ratio.RATIO_16_9;
        } else if (mScreenRatio == ARGFrame.Ratio.RATIO_4_3) {
            ratio = ARGMedia.Ratio.RATIO_4_3;
        } else {
            ratio = ARGMedia.Ratio.RATIO_1_1;
        }
        String fileName = System.currentTimeMillis() + ".png";
        File dirPath=new File(Functions.getAppFolder(context)+Variables.APP_STORY_EDITED_FOLDER);
        File filePath=new File(dirPath,fileName);
        Functions.makeDirectryAndRefresh(context,dirPath.getAbsolutePath(),fileName);

        mARGMedia.takePicture(textureId, filePath.getAbsolutePath(), ratio);

        VideoRecoderA.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (filePath != null && !TextUtils.isEmpty(""+filePath)) {

                    if (uploadPhotoPath.size()<Constants.MAX_PICS_ALLOWED_FOR_VIDEO)
                    {
                        uploadPhotoPath.add(filePath.getAbsolutePath());
                        photoUploadAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        String message=Constants.MAX_PICS_ALLOWED_FOR_VIDEO+" "+context.getString(R.string.pics_allow_only);
                        Functions.showToastOnTop(VideoRecoderA.this,null,message);
                    }
                    updatePhotoUploadStatus();
                }
                else
                {
                    Functions.showToastOnTop(VideoRecoderA.this,null,context.getString(R.string.invalid_photo_format));
                }
            }
        });
    }

    private void updatePhotoUploadStatus() {
        if (uploadPhotoPath.size()>0)
        {
            doneBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_done_red));
            doneBtn.setEnabled(true);
        }
        else
        {
            doneBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_not_done));
            doneBtn.setEnabled(false);
        }
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


    private void makeFiveSecVideo(ArrayList<String> photoPaths) {
//        clean new file before going to next step
        Functions.clearFilesCacheBeforeOperation(new File(Functions.getAppFolder(context)+Variables.outputfile2));
        Functions.showDeterminentLoader(VideoRecoderA.this,false,false,true,context.getString(R.string.rendering_));

        FFMPEGFunctions.INSTANCE.createImageVideo(VideoRecoderA.this,photoPaths
                ,Functions.getSettingsPreference(VideoRecoderA.this).getString(Variables.VideoCompression,"2000")
                , new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getString("action").equals("success"))
                        {
                            Functions.cancelDeterminentLoader();
                            Log.d(Constants.tag,"pathpath: "+bundle.getString("path"));
                            try {
                                Functions.copyFile(new File(""+bundle.getString("path")), new File(Functions.getAppFolder(context)+Variables.outputfile2));
                            } catch (Exception e) {
                                Functions.printLog(Constants.tag, ""+e);
                            }
                            Functions.clearFilesCacheBeforeOperation(new File(bundle.getString("path")));
                            goToPreviewActivity();

                        }
                        else
                        if (bundle.getString("action").equals("failed"))
                        {
                            Functions.cancelDeterminentLoader();
                            Toast.makeText(context, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                        else
                        if (bundle.getString("action").equals("cancel"))
                        {
                            Functions.cancelDeterminentLoader();
                            Toast.makeText(context, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                        else
                        if (bundle.getString("action").equals("process"))
                        {
                            String message=bundle.getString("message");
                            try {
                                int progressPercentage=Functions.CalculateFFMPEGTimeToPercentage(message,Constants.MAX_TIME_FOR_VIDEO_PICS);
                                Functions.showLoadingProgress(progressPercentage);
                            }
                            catch (Exception e){
                                Functions.printLog(Constants.tag,"Exception: "+e);
                            }

                        }
                    }
                });
    }


    public void checkDoneBtnEnable() {
        if (secPassed > (Constants.MIN_TIME_RECORDING / 1000)) {
            doneBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_done_red));
            doneBtn.setEnabled(true);
        } else {
            doneBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_not_done));
            doneBtn.setEnabled(false);
        }
    }

    // this will combine all the videos parts in one  fullvideo
    private void combineAllVideos() {
        if (!(videopaths.size()>0))
        {
            return;
        }
        Functions.showDeterminentLoader(VideoRecoderA.this, false, false,false,context.getString(R.string.mixing_));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> video_list = new ArrayList<>();
                for (int i = 0; i < videopaths.size(); i++) {

                    File file = new File(videopaths.get(i));
                    if (file.exists()) {
                        try {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(VideoRecoderA.this, Uri.fromFile(file));
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
                    String outputFilePath = Functions.getAppFolder(VideoRecoderA.this)+Variables.outputfile2;
                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.cancelDeterminentLoader();
                            goToPreviewActivity();

                        }
                    });

                } catch (Exception e) {
                    Functions.printLog(Constants.tag,"Exception: "+e);
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
                    if (audio != null) {
                        int audio_backtime = (int) (audio.getCurrentPosition() - timeInMillisec);
                        audio.seekTo(audio_backtime);
                    }

                    secPassed = (int) (timeInMilis / 1000);

                    checkDoneBtnEnable();

                }

                Functions.clearFilesCacheBeforeOperation(file);
            }

            if (videopaths.isEmpty()) {
                findViewById(R.id.tabVideoTypeSelection).setVisibility(View.VISIBLE);
                tabVideoLength.setVisibility(View.VISIBLE);
                cutVideoBtn.setVisibility(View.GONE);
                addSoundTxt.setClickable(true);
                tabRotateCam.setVisibility(View.VISIBLE);

//                initlizeVideoProgress();

                if (audio != null) {
                    preparedAudio();
                }

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

            case R.id.upload_layout:
            {
                if (videoType.equals("Photo"))
                {
                    pickPhotoFromGallery();
                }
                else
                {
                    pickVideoFromGallery();
                }
            }
            break;

            case R.id.done:
            {
                if (videoType.equals("Photo"))
                {
                    makeFiveSecVideo(uploadPhotoPath);
                }
                else
                {
                    combineAllVideos();
                }
            }
                break;

            case R.id.cut_video_btn:

                Functions.showAlert(VideoRecoderA.this, "", getString(R.string.descard_the_last_clip_), getString(R.string.delete).toUpperCase(), getString(R.string.cancel_).toUpperCase(), new Callback() {
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

            case R.id.tabFlash:
            {
                if (isFlashOn) {
                    try {
                        mCameraManager.setTorchMode(mCameraId, false);
                    } catch (Exception e) {
                        Functions.printLog(Constants.tag,"Exception: "+e);
                    }

                    isFlashOn = false;
                    ivFlash.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_flash_on));
                } else {

                    try {
                        mCameraManager.setTorchMode(mCameraId, true);
                    } catch (Exception e) {
                        Functions.printLog(Constants.tag,"Exception: "+e);
                    }

                    isFlashOn = true;
                    ivFlash.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_flash_off));
                }
            }
            break;

            case R.id.goBack:
                onBackPressed();
                break;

            case R.id.add_sound_txt:
                Intent intent = new Intent(this, SoundListMainA.class);
                resultCallback.launch(intent);
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;

            case R.id.tabTimer:
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
                break;
            case R.id.tabSpeed:
            {
                if (isSpeedMode)
                {
                    isSpeedMode =false;
                    speedSelectionTab.setVisibility(View.GONE);
                }
                else
                {
                    isSpeedMode =true;
                    speedSelectionTab.setVisibility(View.VISIBLE);
                }
            }
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
            case R.id.short_video_time_txt:
            {
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);
                shortVideoTimeTxt.setLayoutParams(param);

                RelativeLayout.LayoutParams param4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param4.addRule(RelativeLayout.START_OF, R.id.short_video_time_txt);
                longVideoTimeTxt.setLayoutParams(param4);

                shortVideoTimeTxt.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));
                longVideoTimeTxt.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));

                timerSelectedDuration =15*1000;
                Constants.RECORDING_DURATION = 15*1000;

                setupVideoProgress();
            }
            break;


            case R.id.long_video_time_txt:
            {
                RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param2.addRule(RelativeLayout.CENTER_HORIZONTAL);
                longVideoTimeTxt.setLayoutParams(param2);

                RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param3.addRule(RelativeLayout.END_OF, R.id.long_video_time_txt);
                shortVideoTimeTxt.setLayoutParams(param3);

                shortVideoTimeTxt.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                longVideoTimeTxt.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));

                timerSelectedDuration =60*1000;
                Constants.RECORDING_DURATION = 60*1000;

                setupVideoProgress();
            }
            break;


            case R.id.tvUploadPhoto:
            {
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);
                tvUploadPhoto.setLayoutParams(param);

                RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param3.addRule(RelativeLayout.START_OF, R.id.tvUploadPhoto);
                tvUploadVideo.setLayoutParams(param3);

                RelativeLayout.LayoutParams param4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param4.addRule(RelativeLayout.START_OF, R.id.tvUploadVideo);
                tvUploadStory.setLayoutParams(param4);

                tvUploadPhoto.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));
                tvUploadStory.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                tvUploadVideo.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                clearCacheFiles();
                videoType="Photo";

                updateViewsAccordingToType();


                Constants.RECORDING_DURATION=Constants.MAX_TIME_FOR_VIDEO_PICS*1000;
                updatePhotoUploadStatus();
                setupVideoProgress();
            }
            break;

            case R.id.tvUploadVideo:
            {
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);
                tvUploadVideo.setLayoutParams(param);

                RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param3.addRule(RelativeLayout.START_OF, R.id.tvUploadVideo);
                tvUploadStory.setLayoutParams(param3);

                RelativeLayout.LayoutParams param4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param4.addRule(RelativeLayout.END_OF, R.id.tvUploadVideo);
                tvUploadPhoto.setLayoutParams(param4);

                tvUploadVideo.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));
                tvUploadStory.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                tvUploadPhoto.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                clearCacheFiles();
                videoType="Video";

                updateViewsAccordingToType();


                Constants.RECORDING_DURATION = timerSelectedDuration;
                checkDoneBtnEnable();
                setupVideoProgress();
            }
            break;

            case R.id.tvUploadStory:
            {
                RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param2.addRule(RelativeLayout.CENTER_HORIZONTAL);
                tvUploadStory.setLayoutParams(param2);

                RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param3.addRule(RelativeLayout.END_OF, R.id.tvUploadStory);
                tvUploadVideo.setLayoutParams(param3);

                RelativeLayout.LayoutParams param4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                param4.addRule(RelativeLayout.END_OF, R.id.tvUploadVideo);
                tvUploadPhoto.setLayoutParams(param4);

                tvUploadVideo.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                tvUploadPhoto.setTextColor(ContextCompat.getColor(context,R.color.graycolor2));
                tvUploadStory.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));
                clearCacheFiles();
                videoType="Story";

                updateViewsAccordingToType();

                Constants.RECORDING_DURATION = 30*1000;
                checkDoneBtnEnable();
                setupVideoProgress();
            }
            break;


            default:
                return;

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

    private void pickPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultCallbackForGallery.launch(intent);
    }

    ActivityResultLauncher<Intent> resultCallbackForGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("Range")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        String filePath;

                        if (selectedImage.getScheme().equals("content")) {
                            Cursor cursor = context.getContentResolver().query(selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            cursor.close();
                        } else {
                            filePath = selectedImage.getPath();
                        }

//                        Bitmap inputBitmap = BitmapFactory.decodeFile(filePath);
                        Bitmap outputBitmap=Functions.convertImage(filePath);
                        File outputFilepath=Functions.getBitmapToUri(VideoRecoderA.this,outputBitmap,"uploadPhoto"+Functions.getCurrentDate("yyyy-MM-dd HH:mm:ss")+".jpg");
                        filePath=outputFilepath.getAbsolutePath();

                        if (filePath != null && !TextUtils.isEmpty(""+filePath)) {

                            if (uploadPhotoPath.size()<Constants.MAX_PICS_ALLOWED_FOR_VIDEO)
                            {
                                uploadPhotoPath.add(filePath);
                                photoUploadAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                String message=Constants.MAX_PICS_ALLOWED_FOR_VIDEO+" "+context.getString(R.string.pics_allow_only);
                                Functions.showToastOnTop(VideoRecoderA.this,null,message);
                            }
                            updatePhotoUploadStatus();

                        }
                        else
                        {
                            Functions.showToastOnTop(VideoRecoderA.this,null,context.getString(R.string.invalid_photo_format));
                        }

                    }
                }
            });


    private void updateViewsAccordingToType() {
        if (videoType.equals("Video"))
        {
            tabVideoLength.setVisibility(View.VISIBLE);
            speedSelectionTab.setVisibility(View.VISIBLE);
            videoProgress.setVisibility(View.VISIBLE);
            tabSpeed.setVisibility(View.VISIBLE);
            photoSlideOptions.setVisibility(View.GONE);
            recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_recoding_no));
        }
        else
        if (videoType.equals("Photo"))
        {
            tabVideoLength.setVisibility(View.INVISIBLE);
            speedSelectionTab.setVisibility(View.INVISIBLE);
            videoProgress.setVisibility(View.INVISIBLE);
            tabSpeed.setVisibility(View.GONE);
            photoSlideOptions.setVisibility(View.VISIBLE);
            recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_capture_photo));
        }
        else
        {
            tabVideoLength.setVisibility(View.INVISIBLE);
            speedSelectionTab.setVisibility(View.VISIBLE);
            videoProgress.setVisibility(View.VISIBLE);
            tabSpeed.setVisibility(View.VISIBLE);
            photoSlideOptions.setVisibility(View.GONE);
            recordImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_recoding_story_no));
        }
    }


    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            isSelected = data.getStringExtra("isSelected");
                            if (isSelected.equals("yes")) {
                                addSoundTxt.setText(data.getStringExtra("sound_name"));
                                Variables.selectedSoundId = data.getStringExtra("sound_id");
                                preparedAudio();
                            }

                        }
                    }
                }
            });


    // open the intent for get the video from gallery
    public void pickVideoFromGallery() {
        File fileTrim=new File( Functions.getAppFolder(context)+Variables.gallery_trimed_video);
        File fileFilter=new File( Functions.getAppFolder(context)+ Variables.output_filter_file);
        Functions.clearFilesCacheBeforeOperation(fileTrim,fileFilter);

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        takeOrSelectVideoResultLauncher.launch(Intent.createChooser(intent, "Select Video"));
    }



    private void openTrimActivity(String data) {
        Variables.isCompressionApplyOnStart=false;
        TrimVideo.activity(data)
                .setTrimType(TrimType.MIN_MAX_DURATION)
                .setMinToMax(Constants.MIN_TRIM_TIME, (Constants.RECORDING_DURATION/1000))
                .setMinDuration(Constants.MAX_TRIM_TIME)
                .setTitle("")//seconds
                .setMaxTimeCheck(Constants.RECORDING_DURATION)
                .start(this,videoTrimResultLauncher);
    }




    // change the video size
    public void changeVideoSize(String src_path, String destination_path) {

        try {
            Functions.copyFile(new File(src_path),
                    new File(destination_path));

            File file = new File(src_path);
            if (file.exists())
                file.delete();


            if (getIntent().hasExtra("sound_name")) {
                Intent intent = new Intent(context, GallerySelectedVideoA.class);
                intent.putExtra("video_path", Functions.getAppFolder(this)+Variables.gallery_resize_video);
                intent.putExtra("sound_name",getIntent().getStringExtra("sound_name"));
                intent.putExtra("isSelected", "yes");
                intent.putExtra("sound_id", Variables.selectedSoundId);
                intent.putExtra("videoType",videoType);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(context, GallerySelectedVideoA.class);
                intent.putExtra("video_path", Functions.getAppFolder(this)+Variables.gallery_resize_video);
                intent.putExtra("videoType",videoType);
                startActivity(intent);
            }




        } catch (Exception e) {
            e.printStackTrace();
            Functions.printLog(Constants.tag, e.toString());
        }
    }


    // this will play the sound with the video when we select the audio
    MediaPlayer audio;
    public void preparedAudio() {
        File file = new File(Functions.getAppFolder(this)+Variables.APP_HIDED_FOLDER + Variables.SelectedAudio_AAC);
        if (file.exists()) {
            try {
                audio = new MediaPlayer();
                try {
                    audio.setDataSource(Functions.getAppFolder(this)+Variables.APP_HIDED_FOLDER + Variables.SelectedAudio_AAC);
                    audio.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(this, Uri.fromFile(file));
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                final int file_duration = Functions.parseInterger(durationStr);

                if (file_duration < Constants.MAX_RECORDING_DURATION) {
                    Constants.RECORDING_DURATION = file_duration;
                    setupVideoProgress();
                }
            }
            catch (Exception e)
            {
                Log.d(Constants.tag,"Exception : "+e);
                Toast.makeText(this, getString(R.string.you_cannot_create_video_using_this_sound), Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        onresume();
    }



    protected void onresume() {
        super.onResume();

        if (mARGSession == null) {


            ARGConfig config = new ARGConfig(Constants.API_URL, Constants.API_KEY_ARGEAR, Constants.SECRET_KEY, Constants.AUTH_KEY);
            Set<ARGInferenceConfig.Feature> inferenceConfig = EnumSet.of(ARGInferenceConfig.Feature.FACE_HIGH_TRACKING);

            mARGSession = new ARGSession(VideoRecoderA.this, config, inferenceConfig);
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
                && (mGLViewWidth != mGlView.getViewWidth() || mGLViewHeight != mGlView.getViewHeight())) {
            cameraLayout.removeView(mGlView);
            mGlView.getHolder().setFixedSize(mGLViewWidth, mGLViewHeight);
            cameraLayout.addView(mGlView);
        }
    }

    private void initGLView() {
        cameraLayout = findViewById(R.id.camera_layout);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mGlView = new GLView(this, glViewListener);
        mGlView.setZOrderMediaOverlay(true);

        cameraLayout.addView(mGlView, params);
    }

    private void initCamera() {
        if (AppConfig.USE_CAMERA_API == 1) {
            mCamera = new ReferenceCamera1(VideoRecoderA.this, cameraListener);
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
            if (mCameraTexture==null && mCameraTexture.getSurfaceTexture() == null) {
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
                if (mIsShooting) takePictureOnGlThread(frame.getTextureId());
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
                        mTriggerToast = Toast.makeText(VideoRecoderA.this, strTrigger, Toast.LENGTH_SHORT);
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
        if (getLastUpdateAt(VideoRecoderA.this) > getFilterUpdateAt(VideoRecoderA.this, item.uuid)) {
            new FileDeleteAsyncTask(new File(filePath), new FileDeleteAsyncTask.OnAsyncFileDeleteListener() {
                @Override
                public void processFinish(Object result) {
                    Functions.printLog(Constants.tag,"file delete success!");

                    setFilterUpdateAt(VideoRecoderA.this, item.uuid, getLastUpdateAt(VideoRecoderA.this));
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
        if (getLastUpdateAt(VideoRecoderA.this) > getStickerUpdateAt(VideoRecoderA.this, item.uuid)) {
            new FileDeleteAsyncTask(new File(filePath), new FileDeleteAsyncTask.OnAsyncFileDeleteListener() {
                @Override
                public void processFinish(Object result) {
                    Functions.printLog(Constants.tag,"file delete success!");

                    setStickerUpdateAt(VideoRecoderA.this, item.uuid, getLastUpdateAt(VideoRecoderA.this));
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

    public void clearBulge() {
        mARGSession.contents().clear(ARGContents.Type.Bulge);
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

    public BeautyItemData getBeautyItemData() {
        return mBeautyItemData;
    }

    public void setBeauty(float[] params) {
        mARGSession.contents().setBeauty(params);
    }
    public void clearFilter() {
        mARGSession.contents().clear(ARGContents.Type.FilterItem);
    }

    public void setFilterStrength(int strength) {
        if ((mFilterLevel + strength) < 100 && (mFilterLevel + strength) > 0) {
            mFilterLevel += strength;
        }
        mARGSession.contents().setFilterLevel(mFilterLevel);
    }

    public void setVignette() {
        mFilterVignette = !mFilterVignette;
        mARGSession.contents().setFilterOption(ARGContents.FilterOption.VIGNETTING, mFilterVignette);
    }

    public void setBlurVignette() {
        mFilterBlur = !mFilterBlur;
        mARGSession.contents().setFilterOption(ARGContents.FilterOption.BLUR, mFilterBlur);
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

    public int getGLViewWidth() {
        return mGLViewWidth;
    }

    public int getGLViewHeight() {
        return mGLViewHeight;
    }

    public void clearStickers() {
        mCurrentStickeritem = null;
        mHasTrigger = false;

        mARGSession.contents().clear(ARGContents.Type.ARGItem);
    }

    private long getStickerUpdateAt(Context context, String itemId) {
        return PreferenceUtil.getLongValue(context, AppConfig.USER_PREF_NAME_STICKER, itemId);
    }


    private void setStickerUpdateAt(Context context, String itemId, long updateAt) {
        PreferenceUtil.putLongValue(context, AppConfig.USER_PREF_NAME_STICKER, itemId, updateAt);
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

        try {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    VideoRecoderA.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearBulge();
                            clearStickers();
                            clearFilter();
                        }
                    });
                }
            },2000);
        }catch (Exception e)
        {
            Log.d(Constants.tag,"Argear not init");
        }
    }



    public void releaseResources() {
        try {

            if (audio != null) {
                audio.stop();
                audio.reset();
                audio.release();
            }
            stopRecording();
        } catch (Exception e) {

        }
    }

    private void stopRecording() {
        mARGMedia.stopRecording();
    }


    // show a alert before close the activity
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

//        progress synced with two filter
        Functions.showDeterminentLoader(VideoRecoderA.this,false,false,true,context.getString(R.string.rendering_));
        int finalSecond = second;
        int frameRate=Integer.valueOf(Functions.getTrimVideoFrameRate(new File(""+intputPath).getAbsolutePath()));

        FFMPEGFunctions.INSTANCE.videoSpeedProcess(VideoRecoderA.this,intputPath,
                speedTabPosition
                ,frameRate
                ,Functions.getSettingsPreference(VideoRecoderA.this).getString(Variables.VideoCompression,"2000")
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

    public void goToPreviewActivity() {
        if (videoType.equals("Video"))
        {
            Variables.isCompressionApplyOnStart=false;
            Intent intent = new Intent(this, PreviewVideoA.class);
            intent.putExtra("fromWhere", "video_recording");
            intent.putExtra("isSoundSelected", isSelected);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
        else
        {
            Variables.isCompressionApplyOnStart=true;
            Intent intent = new Intent(this, PreviewStoryVideoA.class);
            intent.putExtra("fromWhere", "video_recording");
            intent.putExtra("isSoundSelected", isSelected);
            intent.putExtra("soundName",""+addSoundTxt.getText().toString());
            intent.putExtra("videoType",videoType);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

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
