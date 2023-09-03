package com.qboxus.tictic.activitesfragments.livestreaming.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.activitesfragments.EditTextSheetF;
import com.qboxus.tictic.activitesfragments.livestreaming.adapter.LiveCommentsAdapter;
import com.qboxus.tictic.activitesfragments.livestreaming.adapter.LiveUserViewAdapter;
import com.qboxus.tictic.activitesfragments.livestreaming.fragments.InviteContactsToStreamF;
import com.qboxus.tictic.activitesfragments.livestreaming.model.CameraRequestModel;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveCoinsModel;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveCommentModel;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.LocalStatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.RemoteStatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.StatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.ui.VideoGridContainer;
import com.qboxus.tictic.activitesfragments.sendgift.StickerModel;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.StreamJoinModel;
import com.qboxus.tictic.models.StreamShowHeartModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.models.UsersModel;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.OnSwipeTouchListener;
import com.qboxus.tictic.simpleclasses.Variables;
import com.qboxus.tictic.simpleclasses.streaminglikes.HeartView;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.paperdb.Paper;


public class MulticastStreamerA extends RtcBaseActivity implements View.OnClickListener {

    private VideoGridContainer mVideoGridContainer;
    private VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    DatabaseReference rootref;
    SimpleDraweeView ivGiftCount;
    String userId, userName, userPicture;
    int userRole;
    TextView tvGiftCount;

    View animationCapture;
    LinearLayout tabGiftCount;
    String onlineType,description,secureCode,streamingId;
    int joinStreamPrice;
    boolean dualStreaming;
    LiveUserModel streamerLiveModel;

    ViewFlipper viewflliper,innerViewflliper;
    RelativeLayout viewOne,viewTwo,viewThree,viewFour;
    HeartView streamLikeView;
    Context context;
    boolean isFirstTimeFlip=true;
    View tabMainView;
    public RelativeLayout tabCoinSender;

    public TextView tvCoinCount,tvSender;
    public SimpleDraweeView ivSender;

    RecyclerView liveUserViewRecyclerView;
    TextView tvNoViewData;
    LiveUserViewAdapter liveUserViewAdapter;
    TextView liveUserCount, tvCurrentJoin;
    ImageView ivVideoRequest,ivMuteJoinInvitation,ivVerified;
    TextView tvUserName,tvOtherUserLikes;
    SimpleDraweeView ivMainProfile;
    LinearLayout tabShareStream,tabMenu,tabEffects;
    public TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_multicast_streamer);

        context=MulticastStreamerA.this;
        tvGiftCount=findViewById(R.id.tvGiftCount);
        rootref = FirebaseDatabase.getInstance().getReference();

        Intent bundle = getIntent();
        if (bundle != null) {
            userId = bundle.getStringExtra("userId");
            userName = bundle.getStringExtra("userName");
            userPicture = bundle.getStringExtra("userPicture");
            userRole = bundle.getIntExtra("userRole", Constants.CLIENT_ROLE_BROADCASTER);
            onlineType=bundle.getStringExtra("onlineType");
            description=bundle.getStringExtra("description");
            secureCode=bundle.getStringExtra("secureCode");
            dualStreaming=bundle.getBooleanExtra("dualStreaming",false);
            joinStreamPrice=bundle.getIntExtra("joinStreamPrice",0);
            streamingId=bundle.getStringExtra("streamingId");
        }

        InitControl();
        ActionControl();


        if (userRole == Constants.CLIENT_ROLE_BROADCASTER) {
            rootref.child("LiveStreamingUsers").child(streamingId).keepSynced(true);
            rootref.child("LiveStreamingUsers").child(streamingId).onDisconnect().removeValue();

            addFirebaseNode();
            sendLiveNotification();
            broadcasterlistenerNode();
            addStreamInternetConnection();
            addNodeCameraRequest();
        }
        ListenerCoinNode();
        addLikeStream();
        ListenerJoinNode();
        ListCommentData();

        setUpScreenData();



        addLiveStreamingShareMessage("shareStream");
    }

    CountDownTimer shareStreamTimer;
    private void shareStreamOnRepeatedly() {
        shareStreamTimer=new CountDownTimer(10*60*1000,60*60*1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                addLiveStreamingShareMessage("shareStream");
            }
        };
        shareStreamTimer.start();
    }


    // send the comment to the live user
    public void addLiveStreamingShareMessage(String type) {
        final String key = rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(this).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(this).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(this).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment("");
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").child(key).setValue(commentItem)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete())
                {
                    shareStreamOnRepeatedly();
                }
            }
        });
    }


    private void setUpScreenData() {
        String verified=Functions.getSharedPreference(context).getString(Variables.U_WALLET,"0");
        if (verified.equals("1"))
        {
            ivVerified.setVisibility(View.VISIBLE);
        }
        else
        {
            ivVerified.setVisibility(View.GONE);
        }
        tvCoinCount.setText(""+Functions.getSharedPreference(context).getString(Variables.U_WALLET,"0"));
        tvUserName.setText(""+userName);
        ivMainProfile.setController(Functions.frescoImageLoad(userPicture,ivMainProfile,false));

    }



    private void InitControl() {
        tvOtherUserLikes=findViewById(R.id.tvOtherUserLikes);
        tvUserName = findViewById(R.id.tvMainUserName);
        ivVerified=findViewById(R.id.ivVerified);
        tabMenu =findViewById(R.id.tabMenu);
        tabEffects=findViewById(R.id.tabEffects);
        tvCurrentJoin=findViewById(R.id.tvCurrentJoin);
        liveUserCount=findViewById(R.id.liveUserCount);
        tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setOnClickListener(this);
        ivSender=findViewById(R.id.ivSender);
        tvCoinCount=findViewById(R.id.tvCoinCount);
        tvSender=findViewById(R.id.tvSender);
        tabCoinSender=findViewById(R.id.tabCoinSender);
        ivGiftCount=findViewById(R.id.ivGiftCount);
        tabGiftCount=findViewById(R.id.tabGiftCount);
        animationCapture=findViewById(R.id.animationCapture);
        tabMainView=findViewById(R.id.tabMainView);
        streamLikeView=findViewById(R.id.streamLikeView);
        streamLikeView.setOnClickListener(this);
        viewflliper=findViewById(R.id.viewflliper);
        innerViewflliper=findViewById(R.id.innerViewflliper);
        viewOne=findViewById(R.id.viewOne);
        viewTwo=findViewById(R.id.viewTwo);
        viewThree=findViewById(R.id.viewThree);
        viewFour=findViewById(R.id.viewFour);
        tabShareStream=findViewById(R.id.tabShareStream);
        ivMainProfile=findViewById(R.id.ivMainProfile);
        ivVideoRequest=findViewById(R.id.ivVideoRequest);
        ivVideoRequest.setOnClickListener(this);
        ivMuteJoinInvitation=findViewById(R.id.ivMuteJoinInvitation);
        ivMuteJoinInvitation.setOnClickListener(this);
        tvNoViewData=findViewById(R.id.tvNoViewData);
        liveUserViewRecyclerView=findViewById(R.id.liveUserViewRecyclerView);
        initCommentAdapter();
        setUpJoinRecycler();
        initUI();
        initData();
    }


    private void ActionControl() {
        findViewById(R.id.cross_btn).setOnClickListener(this);

        final Animation inAnim = AnimationUtils.loadAnimation(context, R.anim.in_from_right);
        final Animation outAnim = AnimationUtils.loadAnimation(context, R.anim.out_to_left);
        final Animation inPrevAnim = AnimationUtils.loadAnimation(context, R.anim.in_from_left);
        final Animation outPrevAnim = AnimationUtils.loadAnimation(context, R.anim.out_to_right);


        tabMainView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {

                viewflliper.setInAnimation(inPrevAnim);
                viewflliper.setOutAnimation(outPrevAnim);
                innerViewflliper.setInAnimation(inPrevAnim);
                innerViewflliper.setOutAnimation(outPrevAnim);
                Log.d(com.qboxus.tictic.Constants.tag,"start");

                if (viewTwo==viewflliper.getCurrentView())
                {
                    if (viewFour==innerViewflliper.getCurrentView())
                    {
                        innerViewflliper.showPrevious();
                    }
                    else
                    {
                        viewflliper.showPrevious();
                    }
                }
                else
                {
                    viewflliper.showPrevious();
                }

            }
            public void onSwipeLeft() {
                viewflliper.setInAnimation(inAnim);
                viewflliper.setOutAnimation(outAnim);
                innerViewflliper.setInAnimation(inAnim);
                innerViewflliper.setOutAnimation(outAnim);
                Log.d(com.qboxus.tictic.Constants.tag,"end");
                if (viewTwo==viewflliper.getCurrentView())
                {
                    if (viewThree==innerViewflliper.getCurrentView())
                    {
                        innerViewflliper.showNext();
                    }

                }
                else
                {
                    viewflliper.showNext();
                }
            }
            public void onSwipeBottom() {

            }
            public void onDoubleClick() {

            }
            public void onSingleClick(){

            }
        });

        if (isFirstTimeFlip)
        {
            isFirstTimeFlip=false;
            if (viewOne==viewflliper.getCurrentView())
            {
                viewflliper.showNext();
            }
        }

        tabEffects.setOnClickListener(this);
        tabMenu.setOnClickListener(this);
        tabShareStream.setOnClickListener(this);


    }



    // initailze the adapter
    ArrayList<LiveCommentModel> dataList=new ArrayList<>();
    RecyclerView recyclerView;
    LiveCommentsAdapter adapter;
    public void initCommentAdapter() {
        dataList.clear();
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

        adapter = new LiveCommentsAdapter(context, dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                LiveCommentModel itemUpdate=dataList.get(pos);
                if (itemUpdate.getType().equals("shareStream"))
                {
                    inviteFriendsForStream();
                }
                if (itemUpdate.getType().equals("selfInviteForStream"))
                {
                    if(streamerLiveModel.getDuetConnectedUserId()!=null && !(TextUtils.isEmpty(streamerLiveModel.getDuetConnectedUserId())))
                    {
                        Toast.makeText(context, context.getString(R.string.user_already_connect_to_streaming), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showCameraRequest(itemUpdate.getUserId());
                    }

                }
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void showCameraRequest(String requestedUserId) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.camera_request_broadcast_view);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

        RelativeLayout tabAccept = alertDialog.findViewById(R.id.tabAccept);
        RelativeLayout tabReject = alertDialog.findViewById(R.id.tabReject);
        ImageView closeBtn = alertDialog.findViewById(R.id.closeBtn);



        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                HashMap<String,Object> duetConnectedUserMap=new HashMap<>();
                duetConnectedUserMap.put("duetConnectedUserId","");
                rootref.child("LiveStreamingUsers").child(streamingId)
                        .updateChildren(duetConnectedUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (task.isComplete())
                                {
                                    sendCameraRequest("0",requestedUserId);
                                }
                            }
                        });
                    }
                });

            }
        });

        tabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                HashMap<String,Object> duetConnectedUserMap=new HashMap<>();
                duetConnectedUserMap.put("duetConnectedUserId",requestedUserId);
                rootref.child("LiveStreamingUsers").child(streamingId)
                        .updateChildren(duetConnectedUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (task.isComplete())
                                {
                                    sendCameraRequest("2",requestedUserId);
                                }
                            }
                        });
                    }
                });
            }
        });
        tabReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                HashMap<String,Object> duetConnectedUserMap=new HashMap<>();
                duetConnectedUserMap.put("duetConnectedUserId","");
                rootref.child("LiveStreamingUsers").child(streamingId)
                        .updateChildren(duetConnectedUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (task.isComplete())
                                {
                                    sendCameraRequest("0",requestedUserId);
                                }
                            }
                        });
                    }
                });

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void sendCameraRequest(String type,String requestedUserId) {
        CameraRequestModel model=new CameraRequestModel();
        model.setRequestState(type);
        rootref.child("LiveStreamingUsers").child(streamingId)
                .child("CameraRequest")
                .child(requestedUserId)
                .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete())
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type.equals("2"))
                            {
                                Toast.makeText(context, context.getString(R.string.camera_request_accepted), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context, context.getString(R.string.camera_request_sended), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }


    ChildEventListener commentChildListener;
    Calendar current_cal;
    public void ListCommentData() {
        current_cal = Calendar.getInstance();
        if(commentChildListener ==null)
        {
            commentChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    LiveCommentModel model = dataSnapshot.getValue(LiveCommentModel.class);
                    dataList.add(model);

                    if (Functions.checkTimeDiffernce(current_cal,model.getCommentTime()))
                    {
                        if(model.getType().equalsIgnoreCase("gift"))
                        {
                           MulticastStreamerA.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ShowGiftAnimation(model);
                                }
                            });
                        }
                    }


                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(dataList.size() - 1);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").addChildEventListener(commentChildListener);
        }
    }

    public void removeCommentListener() {
        if (rootref!=null && commentChildListener != null)
        {
            rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").removeEventListener(commentChildListener);
            commentChildListener =null;
        }
    }


    private void setUpJoinRecycler() {
        GridLayoutManager layoutManager=new GridLayoutManager(context,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        liveUserViewRecyclerView.setLayoutManager(layoutManager);
        liveUserViewAdapter=new LiveUserViewAdapter(context,jointUserList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {

            }
        });
        liveUserViewRecyclerView.setAdapter(liveUserViewAdapter);
    }


    ArrayList<StreamJoinModel> jointUserList=new ArrayList<>();
    ValueEventListener joinValueEventListener;
    private void ListenerJoinNode() {
        if (joinValueEventListener==null)
        {
            joinValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    jointUserList.clear();
                    if (dataSnapshot.exists())
                    {
                        for(DataSnapshot joinSnapsot:dataSnapshot.getChildren())
                        {
                            Log.d(com.qboxus.tictic.Constants.tag,"Data JSON : "+joinSnapsot.getValue().toString());
                            if (!(TextUtils.isEmpty(joinSnapsot.getValue().toString())))
                            {
                                StreamJoinModel model=joinSnapsot.getValue(StreamJoinModel.class);
                                jointUserList.add(model);
                            }

                        }
                        liveUserViewAdapter.notifyDataSetChanged();
                        liveUserCount.setText(Functions.getSuffix(""+jointUserList.size()));

                    }
                    else
                    {
                        liveUserViewAdapter.notifyDataSetChanged();
                        liveUserCount.setText(Functions.getSuffix(""+jointUserList.size()));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    liveUserViewAdapter.notifyDataSetChanged();
                    liveUserCount.setText(Functions.getSuffix(""+jointUserList.size()));
                }
            };
            rootref.child("LiveStreamingUsers").child(streamingId).child("JoinStream")
                    .addValueEventListener(joinValueEventListener);
        }
    }

    public void removeJoinListener() {
        if (rootref!=null && joinValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(streamingId).child("JoinStream").removeEventListener(joinValueEventListener);
            joinValueEventListener=null;
        }
    }


    ChildEventListener likeValueEventListener;
    int heartCounter=0;
    private void addLikeStream() {
        if(likeValueEventListener==null)
        {

            likeValueEventListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        StreamShowHeartModel likeData=snapshot.getValue(StreamShowHeartModel.class);
                       MulticastStreamerA.this.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               heartCounter=heartCounter+1;
                               tvOtherUserLikes.setText(Functions.getSuffix(""+heartCounter)+" "+context.getString(R.string.likes));
                               heartsShow();
                           }
                       });
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            rootref.child("LiveStreamingUsers").child(streamingId).child("LikesStream").addChildEventListener(likeValueEventListener);

        }
    }





    ValueEventListener coinValueEventListener;
    ArrayList<LiveCoinsModel> senderCoinsList=new ArrayList<>();
    private void ListenerCoinNode() {
        coinValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                senderCoinsList.clear();
                if (dataSnapshot.exists())
                {
                    for(DataSnapshot joinSnapsot:dataSnapshot.getChildren())
                    {
                        if (!(TextUtils.isEmpty(joinSnapsot.getValue().toString())))
                        {
                            LiveCoinsModel model=joinSnapsot.getValue(LiveCoinsModel.class);
                            senderCoinsList.add(model);
                        }
                    }
                    double maxCoins=0;
                    LiveCoinsModel highCoinSender = null;
                    if (senderCoinsList.size()>0)
                    {
                        tabCoinSender.setVisibility(View.VISIBLE);
                        maxCoins=Double.valueOf(senderCoinsList.get(0).getSendedCoins());
                        highCoinSender=senderCoinsList.get(0);
                    }
                    else
                    {
                        tabCoinSender.setVisibility(View.GONE);
                    }
                    for (LiveCoinsModel item:senderCoinsList)
                    {
                        if (Double.valueOf(item.getSendedCoins())>maxCoins)
                        {
                            maxCoins=Double.valueOf(item.getSendedCoins());
                            highCoinSender=item;
                        }
                    }
                    if (highCoinSender!=null)
                    {
                        tvSender.setText(highCoinSender.getUserName());
                        ivSender.setController(Functions.frescoImageLoad(highCoinSender.getUserPic(),ivSender,false));
                    }


                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootref.child("LiveStreamingUsers").child(streamingId).child("CoinsStream").addValueEventListener(coinValueEventListener);
    }







    public void removeCoinListener() {
        if (rootref != null && coinValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(streamingId).child("CoinsStream").removeEventListener(coinValueEventListener);
        }
    }



    // initialize the views of activity
    private void initUI() {
        boolean isBroadcaster = (userRole == Constants.CLIENT_ROLE_BROADCASTER);


        isAudioActivated=!isBroadcaster;isVideoActivated=!isBroadcaster;
        isbeautyActivated=true;
        rtcEngine().setBeautyEffectOptions(isbeautyActivated,
                com.qboxus.tictic.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);

        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(statsManager());

        rtcEngine().setClientRole(userRole);
        if (isBroadcaster) startBroadcast();
    }


    private void initData() {
        mVideoDimension = com.qboxus.tictic.activitesfragments.livestreaming.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }



    private void startBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
    }



    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setWidth(mVideoDimension.width);
        data.setHeight(mVideoDimension.height);
        data.setFramerate(stats.sentFrameRate);
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

        if (!(streamingId.equals("")) && (streamingId!=null))
        {
            Paper.book("MyLiveStreaming").write(streamingId,""+Functions.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
        }

        if (!statsManager().isEnabled()) return;
        LocalStatsData data = (LocalStatsData) statsManager().getStatsData(0);
        if (data == null) return;

        data.setLastMileDelay(stats.lastmileDelay);
        data.setVideoSendBitrate(stats.txVideoKBitRate);
        data.setVideoRecvBitrate(stats.rxVideoKBitRate);
        data.setAudioSendBitrate(stats.txAudioKBitRate);
        data.setAudioRecvBitrate(stats.rxAudioKBitRate);
        data.setCpuApp(stats.cpuAppUsage);
        data.setCpuTotal(stats.cpuAppUsage);
        data.setSendLoss(stats.txPacketLossRate);
        data.setRecvLoss(stats.rxPacketLossRate);
    }

    // check the network quality
    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userRole == Constants.CLIENT_ROLE_BROADCASTER) {
            broadcastRemoveListener();
            removeNode();
            removeNodeCameraRequest();
            removeStreamInternetConnection();
        }
        removeCoinListener();
        removeLikeStream();
        removeJoinListener();
        removeCommentListener();
        if (shareStreamTimer!=null)
        {
            shareStreamTimer.cancel();
        }

    }

    public void removeLikeStream() {
        if (rootref!=null && likeValueEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(streamingId).child("LikesStream").removeEventListener(likeValueEventListener);
            likeValueEventListener =null;
        }


    }

    public void addFirebaseNode() {

        LiveUserModel model=new LiveUserModel();
        model.setStreamingId(""+streamingId);
        model.setUserId(""+userId);
        model.setUserName(""+userName);
        model.setUserPicture(""+userPicture);
        model.setOnlineType(""+onlineType);
        model.setDescription(""+description);
        model.setSecureCode(""+secureCode);
        model.setDualStreaming(dualStreaming);
        if (dualStreaming)
        {
            model.setStreamJoinAllow(true);
        }
        else
        {
            model.setStreamJoinAllow(false);
        }
        model.setJoinStreamPrice(""+joinStreamPrice);
        model.setUserCoins(""+Functions.getSharedPreference(MulticastStreamerA.this).getString(Variables.U_WALLET,"0"));
        model.setIsVerified(""+Functions.getSharedPreference(MulticastStreamerA.this).getString(Variables.IS_VERIFIED,"0"));
        model.setDuetConnectedUserId("");

        rootref.child("LiveStreamingUsers").child(streamingId).setValue(model);
    }

    // when user goes to offline then change the value status on firebase
    public void removeNode() {
        rootref.child("LiveStreamingUsers").child(streamingId).removeValue();
    }


    // check the current live user status eighter user is live or not when users goes offline this callback will hit
    ValueEventListener broadcastValueEventListener;


    public void broadcasterlistenerNode() {

        broadcastValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    streamerLiveModel = dataSnapshot.getValue(LiveUserModel.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            tvCoinCount.setText(""+streamerLiveModel.getUserCoins());

                            if (streamerLiveModel.isDualStreaming())
                            {
                                ivMuteJoinInvitation.setVisibility(View.VISIBLE);
                                if (streamerLiveModel.isStreamJoinAllow())
                                {
                                    ivMuteJoinInvitation.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_join_streaming_request));
                                }
                                else
                                {
                                    ivMuteJoinInvitation.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_join_streaming_request_mute));
                                }

                                if (streamerLiveModel.getDuetConnectedUserId()==null || streamerLiveModel.getDuetConnectedUserId().equals(""))
                                {
                                    ivVideoRequest.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                ivMuteJoinInvitation.setVisibility(View.GONE);
                            }

                            Functions.getSharedPreference(MulticastStreamerA.this).edit()
                                    .putString(Variables.U_WALLET, ""+streamerLiveModel.getUserCoins()).commit();
                        }
                    });
                }
                else
                {
                    MulticastStreamerA.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, context.getString(R.string.your_live_channel_is_close), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootref.child("LiveStreamingUsers").child(streamingId).addValueEventListener(broadcastValueEventListener);
    }

    public void broadcastRemoveListener() {
        if (rootref != null && broadcastValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(streamingId).removeEventListener(broadcastValueEventListener);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.tabShareStream:
            {
                inviteFriendsForStream();
            }
            break;
            case R.id.tabMenu:
            {
                ShowDailogForJoinBroadcast();
            }
            break;
            case R.id.tabEffects:
            {
                performBeautify();
            }
            break;
            case R.id.cross_btn:
            {
                onBackPressed();
            }
            break;
            case R.id.tvMessage:
            {
                sendComment();
            }
            break;
            case R.id.ivVideoRequest:
            {
                if (streamerLiveModel.getDuetConnectedUserId()!=null
                        && !(TextUtils.isEmpty(streamerLiveModel.getDuetConnectedUserId())))
                {
                    showCameraRequest(streamerLiveModel.getDuetConnectedUserId());
                }
                else
                {
                    Toast.makeText(context, context.getString(R.string.no_user_connected), Toast.LENGTH_SHORT).show();
                }

            }
            break;
            case R.id.ivMuteJoinInvitation:
            {
                updateJoinInvitationStatus();
            }
            break;

        }
    }

    private void performBeautify() {
        isbeautyActivated=!isbeautyActivated;
        rtcEngine().setBeautyEffectOptions(isbeautyActivated,
                com.qboxus.tictic.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);
    }

    private void updateJoinInvitationStatus() {
        HashMap<String,Object> mapData=new HashMap<>();
        if(streamerLiveModel.isStreamJoinAllow())
        {
            mapData.put("streamJoinAllow",false);
        }
        else
        {
            mapData.put("streamJoinAllow",true);
        }

        Functions.showLoader(MulticastStreamerA.this,false,false);
        rootref.child("LiveStreamingUsers").child(streamerLiveModel.getStreamingId()).updateChildren(mapData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (task.isComplete())
                        {
                            Functions.cancelLoader();
                        }
                    }
                });
            }
        });
    }


    private void inviteFriendsForStream() {
        InviteContactsToStreamF f = new InviteContactsToStreamF(streamingId,"multiple",new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {

                }
            }
        });
        f.show(getSupportFragmentManager(), "InviteContactsToStreamF");
    }

    ArrayList<UsersModel> taggedUserList = new ArrayList<>();
    private void sendComment() {
        EditTextSheetF fragment = new EditTextSheetF("OwnComment",taggedUserList, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    if (bundle.getString("action").equals("sendComment"))
                    {
                        taggedUserList= (ArrayList<UsersModel>) bundle.getSerializable("taggedUserList");
                        String message=bundle.getString("message");
                        tvMessage.setText(message);
                        addMessages("comment");
                    }
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("replyStr","");
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "EditTextSheetF");
    }


    public void heartsShow()
    {
        streamLikeView.addHeart(new Random().nextInt(5));
    }


    boolean isAudioActivated=true,isVideoActivated=true,isbeautyActivated=true;
    public void ShowDailogForJoinBroadcast() {
        final Dialog alertDialog = new Dialog(MulticastStreamerA.this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.live_join_broadcast_view);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(MulticastStreamerA.this,R.drawable.d_round_white_background));

        ImageView swith_camera_btn=alertDialog.findViewById(R.id.swith_camera_btn);
        ImageView live_btn_mute_audio=alertDialog.findViewById(R.id.live_btn_mute_audio);
        ImageView live_btn_beautification=alertDialog.findViewById(R.id.live_btn_beautification);
        ImageView live_btn_mute_video=alertDialog.findViewById(R.id.live_btn_mute_video);
        RelativeLayout tab_cancel = alertDialog.findViewById(R.id.tab_cancel);
        ImageView closeBtn = alertDialog.findViewById(R.id.closeBtn);
        LinearLayout tabClient=alertDialog.findViewById(R.id.tabClient);
        LinearLayout tabSwitch=alertDialog.findViewById(R.id.tabSwitch);

        if (!(onlineType.equals("oneTwoOne")))
        {
            if (userRole != Constants.CLIENT_ROLE_BROADCASTER)
            {
                tabClient.setVisibility(View.GONE);
                tabSwitch.setVisibility(View.GONE);
            }
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        live_btn_mute_audio.setActivated(!isAudioActivated);
        live_btn_mute_video.setActivated(!isVideoActivated);
        live_btn_beautification.setActivated(!isbeautyActivated);

        rtcEngine().setBeautyEffectOptions(live_btn_mute_video.isActivated(),
                com.qboxus.tictic.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);

        tab_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });
        swith_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                rtcEngine().switchCamera();
            }
        });
        live_btn_mute_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isAudioActivated=live_btn_mute_video.isActivated();
                if (!isAudioActivated) return;
                rtcEngine().muteLocalAudioStream(isAudioActivated);
                view.setActivated(!isAudioActivated);
            }
        });
        live_btn_beautification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isbeautyActivated=view.isActivated();
                view.setActivated(!isbeautyActivated);
                performBeautify();
            }
        });
        live_btn_mute_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isVideoActivated=view.isActivated();
                if (isVideoActivated) {
                    stopBroadcast();
                } else {
                    startBroadcast();
                }
                view.setActivated(!isVideoActivated);
            }
        });
        alertDialog.show();
    }


    ValueEventListener connectCheckListener;
    DatabaseReference connectedRef;
    private Timer timer = new Timer();
    private final long DELAY = 20000;
    private void addStreamInternetConnection() {
        if (connectCheckListener==null)
        {
            connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

            connectCheckListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        Log.d(com.qboxus.tictic.Constants.tag, "connected");
                        timer.cancel();
                    } else {
                        Log.d(com.qboxus.tictic.Constants.tag, "not connected");
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                onBackPressed();
                                            }
                                        });
                                    }
                                },
                                DELAY
                        );

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(com.qboxus.tictic.Constants.tag, "Listener was cancelled");
                }
            };
            connectedRef.addValueEventListener(connectCheckListener);
        }

    }

    public void removeStreamInternetConnection() {
        if (connectedRef != null && connectCheckListener != null) {
            connectedRef.removeEventListener(connectCheckListener);
        }
    }



    SimpleDraweeView ivGiftProfile,ivGiftItem;
    LinearLayout tabGiftTitle;
    RelativeLayout tabGiftMain;
    View animationGiftCapture,animationResetAnimation;
    TextView tvGiftTitle,tvGiftCountTitle,tvSendGiftCount;
    public void ShowGiftAnimation(LiveCommentModel item) {
        ivGiftProfile=findViewById(R.id.ivGiftProfile);
        tabGiftTitle=findViewById(R.id.tabGiftTitle);
        tabGiftMain=findViewById(R.id.tabGiftMain);
        animationResetAnimation=findViewById(R.id.animationResetAnimation);
        tvGiftTitle=findViewById(R.id.tvGiftTitle);
        tvGiftCountTitle=findViewById(R.id.tvGiftCountTitle);
        ivGiftItem=findViewById(R.id.ivGiftItem);
        tvSendGiftCount=findViewById(R.id.tvSendGiftCount);
        animationGiftCapture=findViewById(R.id.animationGiftCapture);

        String[] str=item.getComment().split("=====");

        Uri imageUri = Uri.parse(str[2]);

        ivGiftProfile.setController(Functions.frescoImageLoad(item.getUserPicture(),ivGiftProfile,false));

        ivGiftItem.setController(Functions.frescoImageLoad(""+imageUri,ivGiftItem,false));
        tvGiftTitle.setText(item.getUserName());
        tvGiftCountTitle.setText(getString(R.string.gave_you_a)+" "+str[1]);
        tvSendGiftCount.setText("X "+str[0]);

        tabGiftMain.animate().alpha(1).translationX(animationGiftCapture.getX()).setDuration(3000).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        tabGiftMain.animate().translationY(animationCapture.getY()).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tabGiftMain.clearAnimation();
                                tabGiftMain.animate().alpha(0).translationY(animationResetAnimation.getY()).translationX(animationResetAnimation.getX()).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        tabGiftMain.clearAnimation();
                                    }
                                }).start();
                            }
                        }).start();
                    }


                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        PlayGiftSound();
                    }
                }).start();
    }

    MediaPlayer player;
    Handler handler;
    private void PlayGiftSound() {
        handler=new Handler(Looper.getMainLooper());
        player = MediaPlayer.create(getApplicationContext(), R.raw.gift_tone);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setVolume(100,100);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        handler.postDelayed(runnable,2000);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            onTuneStop();
        }
    };

    public void onTuneStop() {
        if(player!=null && player.isPlaying()){
            player.stop();
        }
        if (handler!=null)
        {
            handler.removeCallbacks(runnable);
        }
    }


    // send the comment to the live user
    public void addMessages(String type) {

        final String key = rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(this).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(this).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(this).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment(""+ tvMessage.getText().toString());
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").child(key).setValue(commentItem);

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }



    // send the comment to the live user
    public void addLikeComment(String type) {

        final String key = rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(this).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(this).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(this).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment(my_name+" "+getString(R.string.like_this_stream));
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").child(key).setValue(commentItem);

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }


    // send the comment to the live user
    public void addGiftComment(String type, String count, StickerModel model) {

        final String key = rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(this).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(this).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(this).getString(Variables.U_PIC, "");

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        LiveCommentModel commentItem=new LiveCommentModel();
        commentItem.setKey(key);
        commentItem.setUserId(my_id);
        commentItem.setUserName(my_name);
        commentItem.setUserPicture(my_image);
        commentItem.setComment(count+"====="+model.name+"====="+model.getImage());
        commentItem.setType(type);
        commentItem.setCommentTime(formattedDate);
        rootref.child("LiveStreamingUsers").child(streamingId).child("Chat").child(key).setValue(commentItem);
        LiveCoinsModel coinsModel=new LiveCoinsModel();
        coinsModel.setUserId(my_id);
        coinsModel.setUserName(my_name);
        coinsModel.setUserPic(my_image);
        coinsModel.setSendedCoins(""+(Double.valueOf(count)*Double.valueOf(model.coins)));
        rootref.child("LiveStreamingUsers").child(streamingId).child("CoinsStream").child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    LiveCoinsModel preModel=snapshot.getValue(LiveCoinsModel.class);
                    double totalCoins=Double.valueOf(preModel.getSendedCoins());
                    totalCoins=totalCoins+((Double.valueOf(count)*Double.valueOf(model.coins)));

                    HashMap<String,Object> updateMap=new HashMap<>();
                    updateMap.put("sendedCoins",""+totalCoins);

                    rootref.child("LiveStreamingUsers").child(streamingId).child("CoinsStream").child(my_id).updateChildren(updateMap);
                }
                else
                {
                    rootref.child("LiveStreamingUsers").child(streamingId).child("CoinsStream").child(my_id).setValue(coinsModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }




    @Override
    public void onBackPressed() {
        finish();
    }

    // send notification to all of it follower when user live
    public void sendLiveNotification() {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", Functions.getSharedPreference(this).getString(Variables.U_ID, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyRequest.JsonPostRequest(MulticastStreamerA.this, ApiLinks.sendLiveStreamPushNotfication,params, Functions.getHeaders(context),new Callback() {
            @Override
            public void onResponce(String resp) {
            }
        });
    }

    private void streamExist() {

        finish();
    }


    ValueEventListener  cameraRequestEventListener;
    private void addNodeCameraRequest() {
        if(cameraRequestEventListener==null)
        {
            cameraRequestEventListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {

                               if (streamerLiveModel.getDuetConnectedUserId()!=null && !(TextUtils.isEmpty(streamerLiveModel.getDuetConnectedUserId())))
                               {

                                   CameraRequestModel model=snapshot.child(streamerLiveModel.getDuetConnectedUserId()).getValue(CameraRequestModel.class);
                                   if (model.getRequestState().equals("1"))
                                   {
                                       ivVideoRequest.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_camera_request_r));
                                       ivVideoRequest.setVisibility(View.VISIBLE);

                                       if (streamerLiveModel.getDuetConnectedUserId()!=null
                                               && !(TextUtils.isEmpty(streamerLiveModel.getDuetConnectedUserId())))
                                       {
                                           showCameraRequest(streamerLiveModel.getDuetConnectedUserId());
                                       }
                                       else
                                       {
                                           Toast.makeText(context, context.getString(R.string.no_user_connected), Toast.LENGTH_SHORT).show();
                                       }
                                   }
                                   else
                                   if (model.getRequestState().equals("2"))
                                   {
                                       ivVideoRequest.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_camera_request_a));
                                       ivVideoRequest.setVisibility(View.VISIBLE);
                                   }
                                   else
                                   {
                                       ivVideoRequest.setVisibility(View.GONE);
                                   }

                               }

                           }
                       });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            rootref.child("LiveStreamingUsers").child(streamingId).child("CameraRequest").addValueEventListener(cameraRequestEventListener);

        }
    }

    private void removeNodeCameraRequest() {
        if (rootref!=null && cameraRequestEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(streamingId).child("CameraRequest").removeEventListener(cameraRequestEventListener);
            cameraRequestEventListener =null;
        }
    }

}
