package com.qboxus.tictic.activitesfragments.spaces.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.voicecallmodule.openacall.VoiceStreamingNonUiChat;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.TicTic;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.HashMap;


public class RoomStreamService extends Service {

    public static VoiceStreamingNonUiChat streamingInstance;
    DatabaseReference reference;

    String roomId,userId;
    @Override
    public void onCreate() {
        super.onCreate();
        reference= FirebaseDatabase.getInstance().getReference();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction()!=null && intent.getAction().equals("start"))
        {

            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            roomId = intent.getStringExtra("roomId");
            userId = intent.getStringExtra("userId");


            showForgroundService(title, message);
            startRoomStreaming(roomId, userId);
        }
        else
        if(intent.getAction()!=null && intent.getAction().equals("stop"))
        {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }


    private void startRoomStreaming(String roomId,String userId) {
        streamingInstance=new VoiceStreamingNonUiChat((TicTic) getApplication());
        streamingInstance.setChannelNameAndUid(""+roomId,""+userId);
        streamingInstance.startStream(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {

            }
        });

        onlineUser();

    }



    private void showForgroundService(String title, String message) {

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

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setOnlyAlertOnce(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(101, notification);

    }

    @Override
    public void onDestroy() {
        Functions.printLog(Constants.tag,"RoomStreamService:onDestroy");
        if (streamingInstance!=null)
        {
            streamingInstance.quitCall();
        }
        offlineUser();
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onlineUser(){
        HashMap<String,Object> updateMice=new HashMap<>();
        updateMice.put("online","1");
        reference.child(Variables.roomKey).child(roomId).
                child(Variables.roomUsers)
                .child(userId).updateChildren(updateMice);
    }

    public void offlineUser(){
        reference.child(Variables.roomKey).child(roomId).
                child(Variables.roomUsers).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            HashMap<String,Object> updateMice=new HashMap<>();
                            updateMice.put("online","0");
                            reference.child(Variables.roomKey).child(roomId).
                                    child(Variables.roomUsers)
                                    .child(userId).updateChildren(updateMice);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}