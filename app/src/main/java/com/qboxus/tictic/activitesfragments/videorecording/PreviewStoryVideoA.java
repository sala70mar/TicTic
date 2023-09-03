package com.qboxus.tictic.activitesfragments.videorecording;

import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.qboxus.tictic.activitesfragments.storyeditors.ShapeBSFragment;
import com.qboxus.tictic.activitesfragments.storyeditors.StoryStickerArtF;
import com.qboxus.tictic.activitesfragments.storyeditors.TextEditorDialogFragment;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.interfaces.GenrateBitmapCallback;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.models.TextEditorModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.services.UploadService;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.DebounceClickHandler;
import com.qboxus.tictic.simpleclasses.FFMPEGFunctions;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import com.google.android.exoplayer2.video.VideoSize;
import com.simform.videooperations.Common;
import org.json.JSONArray;
import java.io.File;
import java.io.FileOutputStream;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;


public class PreviewStoryVideoA extends AppCompatLocaleActivity implements Player.Listener {


    String isSoundSelected;
    File videoFile;
    String videoType="";
    String draftFile, duetVideoId, duetOrientation;

    SimpleDraweeView ivUserPic;
    Context context;
    LinearLayout tabSound, tabRedo, tabUndo;
    TextView tvSound;
    PhotoEditorView photoEditorView;
    PhotoEditor mPhotoEditor;
    LinearLayout tabPublishStory,tabDraw,tabEraser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE, Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(), false);
        hideNavigation();
        setContentView(R.layout.activity_preview_story_video);
        context = PreviewStoryVideoA.this;
        tabPublishStory = findViewById(R.id.tabPublishStory);
        tabDraw=findViewById(R.id.tabDraw);
        tabEraser=findViewById(R.id.tabEraser);
        photoEditorView = findViewById(R.id.photoEditorView);
        tvSound = findViewById(R.id.tvSound);
        tabSound = findViewById(R.id.tabSound);
        tabUndo = findViewById(R.id.tabUndo);
        tabRedo = findViewById(R.id.tabRedo);
        initEditor();

        Intent intent = getIntent();
        if (intent != null) {
            String fromWhere = intent.getStringExtra("fromWhere");
            if (fromWhere != null && fromWhere.equals("video_recording")) {
                isSoundSelected = intent.getStringExtra("isSoundSelected");
                draftFile = intent.getStringExtra("draft_file");
                videoType = ""+intent.getStringExtra("videoType");
            }
            else {
                draftFile = intent.getStringExtra("draft_file");
            }
        }
        ivUserPic = findViewById(R.id.ivUserPic);
        String picUrl = Functions.getSharedPreference(PreviewStoryVideoA.this).getString(Variables.U_PIC, "null");
        ivUserPic.setController(Functions.frescoImageLoad(picUrl, R.drawable.ic_user_icon, ivUserPic, false));

        videoFile = new File(Functions.getAppFolder(this) + Variables.outputfile2);


        Log.d(Constants.tag,"videoFile: "+videoFile);
        startPlayerConfiguration();

        tabUndo.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.undo();
            }
        }));
        tabRedo.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.redo();
            }
        }));
        findViewById(R.id.goBack).setOnClickListener(new DebounceClickHandler(v -> {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }));

        findViewById(R.id.tabText).setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKotlinTextEditor();
            }
        }));

        findViewById(R.id.tabSticker).setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStickerAddedSheet();
            }
        }));

        findViewById(R.id.tabNext).setOnClickListener(new DebounceClickHandler(v -> {
            saveEditedImage(false);

        }));

        tabPublishStory.setOnClickListener(new DebounceClickHandler(v -> {
            videoType="Story";
            saveEditedImage(true);
        }));
        tabEraser.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.brushEraser();
            }
        }));
        tabDraw.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.setBrushDrawingMode(true);
                openKotlinDrawShapeEditor();
            }
        }));
    }



    private void saveEditedImage(boolean isPublish) {
        String fileName = System.currentTimeMillis() + ".png";
        File dirPath=new File(Functions.getAppFolder(PreviewStoryVideoA.this)+Variables.APP_STORY_EDITED_FOLDER);
        File filePath=new File(dirPath,fileName);
        Functions.makeDirectryAndRefresh(PreviewStoryVideoA.this,dirPath.getAbsolutePath(),fileName);

        PreviewStoryVideoA.this.runOnUiThread(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                SaveSettings saveSettings=new SaveSettings.Builder().build();
                mPhotoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        PreviewStoryVideoA.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Variables.videoWidth, Variables.videoHeight, true);
                                    FileOutputStream out = new FileOutputStream(filePath.getAbsolutePath());
                                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.close();
                                }
                                catch (Exception e)
                                {
                                    Log.d(Constants.tag,"Exception scaledBitmap: "+e);
                                }
                                finally {
                                    merageEditing(filePath.getAbsolutePath(),isPublish);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@Nullable Exception e) {
                        Log.d(Constants.tag,"Exception BitmapEditor: "+e);
                    }
                });
            }
        });
    }

    private void openKotlinDrawShapeEditor() {
        ShapeBuilder mShapeBuilder =new ShapeBuilder();
        mPhotoEditor.setShape(mShapeBuilder);
        ShapeBSFragment fragment = new ShapeBSFragment();
        fragment.setPropertiesChangeListener(new ShapeBSFragment.Properties() {
            @Override
            public void onColorChanged(int colorCode) {
                mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode));
            }

            @Override
            public void onOpacityChanged(int opacity) {
                mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity));
            }

            @Override
            public void onShapeSizeChanged(int shapeSize) {
                mPhotoEditor.setShape(mShapeBuilder.withShapeSize(Float.valueOf(""+shapeSize)));
            }

            @Override
            public void onShapePicked(@Nullable ShapeType shapeType) {
                mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType));
            }
        });
        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "TextEditorDialogFragment");
    }

    private void openKotlinTextEditor() {
        TextEditorDialogFragment fragment = new TextEditorDialogFragment(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                TextEditorModel model=(TextEditorModel) bundle.getSerializable("data");


                TextStyleBuilder styleBuilder=new TextStyleBuilder();
                styleBuilder.withTextColor(model.colorCode);
                Typeface typeface = ResourcesCompat.getFont(context, model.selectedFont.font);
                styleBuilder.withTextFont(typeface);
                if(model.direction==0)
                    styleBuilder.withGravity(Gravity.START);
                if(model.direction==1)
                    styleBuilder.withGravity(Gravity.CENTER);
                if(model.direction==2)
                    styleBuilder.withGravity(Gravity.END);
                mPhotoEditor.addText(model.text,styleBuilder);
            }
        });
        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "TextEditorDialogFragment");
    }

    private void openKotlinTextEditor(View rootView,String inputText, int colorCode) {
        TextEditorDialogFragment fragment = new TextEditorDialogFragment(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                TextEditorModel model=(TextEditorModel) bundle.getSerializable("data");


                TextStyleBuilder styleBuilder=new TextStyleBuilder();
                styleBuilder.withTextColor(model.colorCode);
                Typeface typeface = ResourcesCompat.getFont(context, model.selectedFont.font);
                styleBuilder.withTextFont(typeface);
                if(model.direction==0)
                    styleBuilder.withGravity(Gravity.START);
                if(model.direction==1)
                    styleBuilder.withGravity(Gravity.CENTER);
                if(model.direction==2)
                    styleBuilder.withGravity(Gravity.END);
                mPhotoEditor.editText(rootView,inputText,styleBuilder);
            }
        });
        TextEditorModel model=new TextEditorModel();
        model.text=inputText;
        model.colorCode=colorCode;
        Bundle bundle=new Bundle();
        bundle.putSerializable("data",model);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "TextEditorDialogFragment");
    }

    private void initEditor() {
        photoEditorView.getSource().setBackground(ContextCompat.getDrawable(PreviewStoryVideoA.this,R.drawable.transprent_editor));
        mPhotoEditor =new PhotoEditor.Builder(PreviewStoryVideoA.this,photoEditorView)
                .setPinchTextScalable(true)
                .build();
        mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(@Nullable View view, @Nullable String inputText, int colorCode) {
                openKotlinTextEditor(view,inputText,colorCode);
            }

            @Override
            public void onAddViewListener(@Nullable ViewType viewType, int i) {

            }

            @Override
            public void onRemoveViewListener(@Nullable ViewType viewType, int i) {

            }

            @Override
            public void onStartViewChangeListener(@Nullable ViewType viewType) {

            }

            @Override
            public void onStopViewChangeListener(@Nullable ViewType viewType) {

            }

            @Override
            public void onTouchSourceImage(@Nullable MotionEvent motionEvent) {

            }
        });
    }

    private void setupScreenData() {
        if (isSoundSelected != null && isSoundSelected.equals("yes"))
        {
            tabSound.setVisibility(View.VISIBLE);
            tvSound.setText(""+getIntent().getStringExtra("soundName"));
            preparedAudio();
        }
        else
        {
            tabSound.setVisibility(View.GONE);
        }
    }





    // this will play the sound with the video when we select the audio
    MediaPlayer audio;
    public void preparedAudio() {
        videoPlayer.setVolume(0);

        File file = new File(Functions.getAppFolder(this)+Variables.APP_HIDED_FOLDER + Variables.SelectedAudio_AAC);
        if (file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(Functions.getAppFolder(this)+Variables.APP_HIDED_FOLDER + Variables.SelectedAudio_AAC);
                audio.prepare();
                audio.setLooping(true);

                videoPlayer.setPlayWhenReady(true);
                audio.start();

            } catch (Exception e) {
                e.printStackTrace();
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


    private void startPlayerConfiguration() {
        setPlayer(videoFile.getAbsolutePath());
        setupScreenData();
    }




    private void openStickerAddedSheet() {
        StoryStickerArtF fragment = new StoryStickerArtF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow"))
                {
                    if (bundle.getString("type").equals("sticker"))
                    {
                        String url=bundle.getString("data");
                        addBitmapImage(url);
                    }
                    else
                    if (bundle.getString("type").equals("emoji"))
                    {
                        String emojiCode=bundle.getString("data");
                        mPhotoEditor.addEmoji(emojiCode);
                    }

                }
            }
        });
        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "StoryStickerArtF");
    }

    private void addBitmapImage(String url) {
        Functions.UrlToBitmapGenrator(url, new GenrateBitmapCallback() {
            @Override
            public void onResult(Bitmap bitmap) {
                PreviewStoryVideoA.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoEditor.addImage(bitmap);
                    }
                });
            }
        });
    }


    private void merageEditing(String path,boolean isPublish) {
//        clean new file before going to next step
        Functions.clearFilesCacheBeforeOperation(new File(Functions.getAppFolder(PreviewStoryVideoA.this)+Variables.output_filter_file));

        Functions.showDeterminentLoader(PreviewStoryVideoA.this,false,false,true,PreviewStoryVideoA.this.getString(R.string.rendering_));
        String outputPath = Common.INSTANCE.getFilePath(PreviewStoryVideoA.this, Common.VIDEO);

        int frameRate=Integer.valueOf(Functions.getTrimVideoFrameRate(new File(""+videoFile).getAbsolutePath()));

        FFMPEGFunctions.INSTANCE.addImageProcess(path,
                videoFile, outputPath
                ,frameRate
                ,Functions.getSettingsPreference(PreviewStoryVideoA.this).getString(Variables.VideoCompression,"2000")
                , new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getString("action").equals("success"))
                        {
                            Functions.cancelDeterminentLoader();
                            Functions.clearFilesCacheBeforeOperation(new File(path));
                            videoFile=new File(bundle.getString("path"));

                            if (isPublish)
                            {
                                try {
                                    Functions.copyFile(videoFile, new File(Functions.getAppFolder(PreviewStoryVideoA.this)+Variables.output_filter_file));

                                } catch (Exception e) {
                                    Functions.printLog(Constants.tag, ""+e);
                                }
                                moveToPublish();
                            }
                            else
                            {
                                try {
                                    Functions.copyFile(videoFile, new File(Functions.getAppFolder(PreviewStoryVideoA.this)+Variables.outputfile2));

                                } catch (Exception e) {
                                    Functions.printLog(Constants.tag, ""+e);
                                }
                                moveToNext();
                            }
                            startPlayerConfiguration();
                        }
                        else
                        if (bundle.getString("action").equals("failed"))
                        {
                            Functions.cancelDeterminentLoader();
                            Toast.makeText(PreviewStoryVideoA.this, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                        else
                        if (bundle.getString("action").equals("cancel"))
                        {
                            Functions.cancelDeterminentLoader();
                            Toast.makeText(PreviewStoryVideoA.this, getText(R.string.invalid_video_format), Toast.LENGTH_SHORT).show();
                        }
                        else
                        if (bundle.getString("action").equals("process"))
                        {
                            String message=bundle.getString("message");
                            try {
                                int progressPercentage=Functions.CalculateFFMPEGTimeToPercentage(message,videoDuration);
                                Functions.showLoadingProgress(progressPercentage);
                            }
                            catch (Exception e){}

                        }
                    }
                });
    }


    // this will call when swipe for another video and
    // this function will set the player to the current video
    ExoPlayer videoPlayer;
    StyledPlayerView  playerView;
    int videoDuration=5;
    public void setPlayer(String path) {
        videoPlayer =new ExoPlayer.Builder(context).
                setTrackSelector(new DefaultTrackSelector(context)).
                setLoadControl(Functions.getExoControler()).
                build();
        Uri videoURI = Uri.parse(path);
        MediaItem mediaItem = MediaItem.fromUri(videoURI);
        videoPlayer.setMediaItem(mediaItem);
        videoPlayer.prepare();
        videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        videoPlayer.addListener(PreviewStoryVideoA.this);
        try {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build();
            videoPlayer.setAudioAttributes(audioAttributes, true);
        }catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: getExoPlayerInit "+e);
        }

        PreviewStoryVideoA.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerView = findViewById(R.id.playerview);
                playerView.setPlayer(videoPlayer);
                playerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                videoPlayer.setPlayWhenReady(true);
            }
        });
    }

    private int getVideoDurationSeconds(ExoPlayer player)
    {
        int timeMs=(int) player.getDuration();
        int totalSeconds = timeMs / 1000;
        return totalSeconds;
    }


    private void moveToPublish() {
        makeParamAccordingToStory();
        onStop();
        startService();
    }


    JSONArray hashTag, friendsTag;
    String privcyType="Public";
    public void makeParamAccordingToStory() {
        hashTag = new JSONArray();
        friendsTag = new JSONArray();
        privcyType="Public";
    }

    // this will start the service for uploading the video into database
    public void startService() {
        String videoPath = Functions.getAppFolder(this)+Variables.output_filter_file;
        UploadService mService = new UploadService();
        if (!Functions.isMyServiceRunning(this, mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("startservice");
            mServiceIntent.putExtra("draft_file", draftFile);
            mServiceIntent.putExtra("duet_video_id", duetVideoId);
            mServiceIntent.putExtra("uri", "" + videoPath);
            mServiceIntent.putExtra("desc", "");
            mServiceIntent.putExtra("privacy_type", privcyType);
            mServiceIntent.putExtra("hashtags_json", hashTag.toString());
            mServiceIntent.putExtra("mention_users_json", friendsTag.toString());
            mServiceIntent.putExtra("duet_orientation", duetOrientation);
            mServiceIntent.putExtra("allow_duet", "0");
            mServiceIntent.putExtra("allow_comment", "false");
            mServiceIntent.putExtra("videoType", videoType);


            startService(mServiceIntent);


            PreviewStoryVideoA.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendBroadByName("uploadVideo");
                    startActivity(new Intent(PreviewStoryVideoA.this, MainMenuActivity.class));
                }
            });


        } else {
            Toast.makeText(PreviewStoryVideoA.this, getString(R.string.please_wait_video_uploading_is_already_in_progress), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendBroadByName(String action) {
        Intent intent= new Intent(action);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }


    private void moveToNext() {
        Intent intent = new Intent(this, PreviewVideoA.class);
        intent.putExtra("fromWhere", ""+getIntent().getStringExtra("fromWhere"));
        intent.putExtra("isSoundSelected", ""+getIntent().getStringExtra("isSoundSelected"));
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    // play the video again on resume
    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(true);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            if (videoPlayer != null) {
                videoPlayer.setPlayWhenReady(false);
            }
            if (audio != null) {
                audio.pause();
            }
        } catch (Exception e) {

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null) {
            videoPlayer.release();
        }

        if (audio != null) {
            audio.pause();
            audio.release();
        }
    }


    // handle that will be call on player state change


    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_ENDED) {

            videoPlayer.seekTo(0);
            videoPlayer.setPlayWhenReady(true);

            if (audio != null) {
                audio.seekTo(0);
                audio.start();
            }

        }
        if (playbackState == Player.STATE_READY)
        {
            videoDuration= getVideoDurationSeconds(videoPlayer);
            Log.d(Constants.tag,"videoDuration: "+videoDuration);
        }

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



    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }



}
