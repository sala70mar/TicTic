package com.qboxus.tictic.services;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.qboxus.tictic.activitesfragments.HomeF;
import com.qboxus.tictic.activitesfragments.WatchVideosA;
import com.qboxus.tictic.apiclasses.FileUploader;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.models.UploadVideoModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qboxus on 6/7/2018.
 */


// this the background service which will upload the video into database
public class UploadService extends Service {


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }

    boolean mAllowRebind;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }


    String draft_file, duet_video_id;
    String videopath;
    String description;
    String privacy_type;
    String allow_comment, allow_duet;
    String hashtags_json, users_json;
    SharedPreferences sharedPreferences;

    public UploadService() {
        super();
    }


    @Override
    public void onCreate() {
        sharedPreferences = Functions.getSharedPreference(this);
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        // get the all the selected date for send to server during the post video

        if (intent != null && intent.getAction().equals("startservice")) {
            showNotification();

            videopath = intent.getStringExtra("uri");
            draft_file = intent.getStringExtra("draft_file");
            duet_video_id = intent.getStringExtra("duet_video_id");
            description = intent.getStringExtra("desc");
            privacy_type = intent.getStringExtra("privacy_type");
            allow_comment = intent.getStringExtra("allow_comment");
            allow_duet = intent.getStringExtra("allow_duet");
            hashtags_json = intent.getStringExtra("hashtags_json");
            users_json = intent.getStringExtra("mention_users_json");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    UploadVideoModel uploadModel=new UploadVideoModel();
                    uploadModel.setUserId(sharedPreferences.getString(Variables.U_ID, "0"));
                    uploadModel.setSoundId(Variables.selectedSoundId);
                    uploadModel.setDescription(description);
                    uploadModel.setPrivacyPolicy(privacy_type);
                    uploadModel.setAllowComments(allow_comment);
                    uploadModel.setAllowDuet(allow_duet);
                    uploadModel.setHashtagsJson(hashtags_json);
                    uploadModel.setUsersJson(users_json);
                    if (duet_video_id != null) {
                        uploadModel.setVideoId(duet_video_id);
                        uploadModel.setDuet("" + intent.getStringExtra("duet_orientation"));
                    } else {
                        uploadModel.setVideoId("0");
                    }

                    try {
                        if (intent.hasExtra("videoType"))
                        {
                            if (intent.getStringExtra("videoType")!=null && !(intent.getStringExtra("videoType").equals("null")) &&
                                    intent.getStringExtra("videoType").equals("Story"))
                            {
                                uploadModel.setVideoType("1");
                            }
                            else
                            {
                                uploadModel.setVideoType("0");
                            }
                        }
                        else
                        {
                            uploadModel.setVideoType("0");
                        }

                    }catch (Exception e){
                        Log.d(Constants.tag,"Exception: "+e);
                        uploadModel.setVideoType("0");
                    }

                    FileUploader fileUploader = new FileUploader(new File(videopath),getApplicationContext(),uploadModel);
                    fileUploader.SetCallBack(new FileUploader.FileUploaderCallback() {
                        @Override
                        public void onError() {
                            //send error broadcast
                            Functions.printLog(Constants.tag, "Error");
                            stopForeground(true);
                            stopSelf();

                            sendBroadByName("uploadVideo");
                            sendBroadByName("newVideo");
                        }

                        @Override
                        public void onFinish(String responses) {

                            Functions.printLog(Constants.tag, responses);

                            try {
                                JSONObject jsonObject = new JSONObject(responses);
                                int code = jsonObject.optInt("code",0);
                                if (code==200) {

                                    Variables.reloadMyVideos = true;
                                    Variables.reloadMyVideosInner = true;
                                    deleteDraftFile();
                                    Functions.showToast(UploadService.this, UploadService.this.getString(R.string.your_video_is_uploaded_successfully));

                                }

                            } catch (Exception e) {
                                Functions.printLog(Constants.tag, "Exception: "+e);
                            }

                            stopForeground(true);
                            stopSelf();

                            sendBroadByName("uploadVideo");
                            sendBroadByName("newVideo");
                            //send finish broadcast
                        }

                        @Override
                        public void onProgressUpdate(int currentpercent, int totalpercent,String msg) {
                            //send progress broadcast
                            if (currentpercent>0)
                            {
                                Bundle bundle=new Bundle();
                                bundle.putBoolean("isShow",true);
                                bundle.putInt("currentpercent",currentpercent);
                                bundle.putInt("totalpercent",totalpercent);
                                if (HomeF.uploadingCallback!=null)
                                {
                                    HomeF.uploadingCallback.onResponce(bundle);
                                }
                                if (WatchVideosA.uploadingCallback!=null)
                                {
                                    WatchVideosA.uploadingCallback.onResponce(bundle);
                                }
                            }
                        }
                    });


                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", sharedPreferences.getString(Variables.U_ID, "0"));
                    map.put("sound_id", Variables.selectedSoundId);
                    map.put("description", description);
                    map.put("privacy_type", privacy_type);
                    map.put("allow_comments", allow_comment);
                    map.put("allow_duet", allow_duet);
                    map.put("hashtags_json", hashtags_json);
                    map.put("users_json", users_json);
                    map.put("videoType",""+uploadModel.getVideoType());


                    if (duet_video_id != null) {
                        map.put("video_id", duet_video_id);
                        map.put("duet", "" + intent.getStringExtra("duet_orientation"));
                    } else {
                        map.put("video_id", "0");
                    }

                    Functions.printLog(Constants.tag, map.toString());


                }
            }).start();


        } else if (intent != null && intent.getAction().equals("stopservice")) {
            stopForeground(true);
            stopSelf();
        }


        return Service.START_STICKY;
    }





    private void sendBroadByName(String action) {
        Intent intent= new Intent(action);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }


    // this will show the sticky notification during uploading video
    @SuppressLint("InlinedApi")
    private void showNotification() {

        Intent notificationIntent = new Intent(this, MainMenuActivity.class);


        PendingIntent pendingIntent=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        final String CHANNEL_ID = "default";
        final String CHANNEL_NAME = "Default";

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        androidx.core.app.NotificationCompat.Builder builder = (androidx.core.app.NotificationCompat.Builder) new androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle(UploadService.this.getString(R.string.uploading_video))
                .setContentText(UploadService.this.getString(R.string.please_wait_video_is_uploading))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        android.R.drawable.stat_sys_upload))
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(101, notification);

    }


    // delete the video from draft after post video
    public void deleteDraftFile() {

        try {
            if (draft_file != null) {
                File file = new File(draft_file);
                file.delete();
            }
        } catch (Exception e) {
            Functions.printLog(Constants.tag, e.toString());
        }


    }


}