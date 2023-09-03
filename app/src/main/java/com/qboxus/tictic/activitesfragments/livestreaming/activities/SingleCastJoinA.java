package com.qboxus.tictic.activitesfragments.livestreaming.activities;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
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
import com.qboxus.tictic.activitesfragments.livestreaming.model.CameraRequestModel;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveCoinsModel;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveCommentModel;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.LocalStatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.RemoteStatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.StatsData;
import com.qboxus.tictic.activitesfragments.livestreaming.stats.StatsManager;
import com.qboxus.tictic.activitesfragments.livestreaming.ui.VideoGridContainer;
import com.qboxus.tictic.activitesfragments.sendgift.StickerGiftF;
import com.qboxus.tictic.activitesfragments.sendgift.StickerModel;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.StreamJoinModel;
import com.qboxus.tictic.models.StreamShowHeartModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.models.UserOnlineModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.models.UsersModel;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.OnSwipeTouchListener;
import com.qboxus.tictic.simpleclasses.Variables;
import com.qboxus.tictic.simpleclasses.streaminglikes.HeartView;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class SingleCastJoinA extends RtcBaseActivity implements View.OnClickListener {



    public VideoGridContainer mVideoGridContainer;
    public VideoEncoderConfiguration.VideoDimensions mVideoDimension;
    DatabaseReference rootref;
    Context context;
    ViewFlipper viewflliper,innerViewflliper;
    RelativeLayout viewOne,viewTwo,viewThree,viewFour;
    SimpleDraweeView ivGiftCount;
    TextView tvGiftCount;
    View animationCapture;
    LinearLayout tabGiftCount;
    HeartView streamLikeView;
    boolean isLikeStream=true;
    LiveUserModel streamerLiveModel;
    View tabMainView;
    TextView  tvCurrentJoin;
    public SimpleDraweeView ivSender;
    public TextView tvCoinCount,tvSender,liveUserCount,tvOtherUserLikes;
    public RelativeLayout tabCoinSender;

    RecyclerView liveUserViewRecyclerView;
    TextView tvNoViewData;
    LiveUserViewAdapter liveUserViewAdapter;

    boolean isFirstTimeFlip=true;
    boolean isSendHeart=true;

    String bookingId;
    int userRole=io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE;
    LiveUserModel item;
    SimpleDraweeView ivMainProfile;
    TextView tvMessage,tvMainUserName,btnfollow;
    LinearLayout tabLikeStreaming,tabJoinVideo,tabGift;
    ImageView ivVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE, Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(), false);
        setContentView(R.layout.activity_single_cast_join);

        InitControl();
        ActionControl();
    }

    private void ActionControl() {

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
                Log.d(Constants.tag,"start");

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
                Log.d(Constants.tag,"end");
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

            @Override
            public void onDoubleClick(MotionEvent e) {
                if (isSendHeart)
                {
                    isSendHeart=true;
                    addLikeIntoStream();
                }
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

        tabJoinVideo.setOnClickListener(this);
        tabGift.setOnClickListener(this);




    }

    private void addLikeIntoStream() {
        StreamShowHeartModel likeData=new StreamShowHeartModel();
        likeData.setUserId(""+ Functions.getSharedPreference(context).getString(Variables.U_ID,""));
        likeData.setOtherUserId(""+item.getUserId());
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("LikesStream").push().setValue(likeData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete())
                {
                    isSendHeart=true;
                }
            }
        });
    }

    private void InitControl() {
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null)
        {
            bookingId=getIntent().getStringExtra("bookingId");
            item= (LiveUserModel) getIntent().getSerializableExtra("dataModel");
        }
        else
        {
            finish();
        }

        context=SingleCastJoinA.this;
        rootref = FirebaseDatabase.getInstance().getReference();
        tabMainView=findViewById(R.id.tabMainView);
        tvGiftCount=findViewById(R.id.tvGiftCount);
        ivGiftCount=findViewById(R.id.ivGiftCount);
        tabGiftCount=findViewById(R.id.tabGiftCount);
        animationCapture=findViewById(R.id.animationCapture);
        streamLikeView=findViewById(R.id.streamLikeView);
        tvNoViewData=findViewById(R.id.tvNoViewData);
        liveUserViewRecyclerView=findViewById(R.id.liveUserViewRecyclerView);
        liveUserCount=findViewById(R.id.liveUserCount);
        tvCurrentJoin=findViewById(R.id.tvCurrentJoin);
        tabJoinVideo =findViewById(R.id.tabJoinVideo);
        tabGift =findViewById(R.id.tabGift);
        tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setOnClickListener(this);
        tabLikeStreaming=findViewById(R.id.tabLikeStreaming);
        tabLikeStreaming.setOnClickListener(this);
        tvCoinCount=findViewById(R.id.tvCoinCount);
        tvSender=findViewById(R.id.tvSender);

        tabCoinSender=findViewById(R.id.tabCoinSender);
        ivSender=findViewById(R.id.ivSender);
        tvOtherUserLikes=findViewById(R.id.tvOtherUserLikes);
        ivMainProfile=findViewById(R.id.ivMainProfile);
        findViewById(R.id.cross_btn).setOnClickListener(this);
        tvMainUserName=findViewById(R.id.tvMainUserName);
        ivVerified=findViewById(R.id.ivVerified);
        btnfollow=findViewById(R.id.btnfollow);
        btnfollow.setOnClickListener(this);
        viewflliper=findViewById(R.id.viewflliper);
        innerViewflliper=findViewById(R.id.innerViewflliper);
        viewOne=findViewById(R.id.viewOne);
        viewTwo=findViewById(R.id.viewTwo);
        viewThree=findViewById(R.id.viewThree);
        viewFour=findViewById(R.id.viewFour);

        setUpJoinRecycler();
        initCommentAdapter();


        setUpScreenData();
        lounchStreamerCam();
        callApiForGetAllvideos(item.getUserId(),item.getUserName());
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
                            SingleCastJoinA.this.runOnUiThread(new Runnable() {
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
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").addChildEventListener(commentChildListener);
        }
    }

    public void removeCommentListener() {
        if (rootref!=null && commentChildListener != null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").removeEventListener(commentChildListener);
            commentChildListener =null;
        }
    }


    public void removeLikeStream() {
        if (rootref!=null && likeValueEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("LikesStream").removeEventListener(likeValueEventListener);
            likeValueEventListener =null;
        }


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

            }
        });

        recyclerView.setAdapter(adapter);

    }

    private void setUpScreenData() {
        if (item.getIsVerified().equals("1"))
        {
            ivVerified.setVisibility(View.VISIBLE);
        }
        else
        {
            ivVerified.setVisibility(View.GONE);
        }
        tvCoinCount.setText(""+item.getUserCoins());
        tvMainUserName.setText(""+item.getUserName());
        ivMainProfile.setController(Functions.frescoImageLoad(item.getUserPicture(),ivMainProfile,false));
        if(item.getUserId().equalsIgnoreCase(Variables.sharedPreferences.getString(Variables.U_ID,"")))
        {
            tabGift.setVisibility(View.GONE);
        }
        else {
            tabGift.setVisibility(View.VISIBLE);
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
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("JoinStream")
                    .addValueEventListener(joinValueEventListener);
        }
    }

    public void removeJoinListener() {
        if (rootref!=null && joinValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("JoinStream").removeEventListener(joinValueEventListener);
            joinValueEventListener=null;
        }
    }



    ValueEventListener  coinValueEventListener;
    ArrayList<LiveCoinsModel> senderCoinsList=new ArrayList<>();
    private void ListenerCoinNode() {
        if(coinValueEventListener==null)
        {
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
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").addValueEventListener(coinValueEventListener);

        }
    }

    public void removeCoinListener() {
        if (rootref!=null && coinValueEventListener != null) {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").removeEventListener(coinValueEventListener);
            coinValueEventListener=null;
        }
    }

    private void AddJoinNode() {
        StreamJoinModel model=new StreamJoinModel();
        model.setUserId(Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
        model.setUserName(Functions.getSharedPreference(context).getString(Variables.U_NAME, ""));
        model.setUserPic(Functions.getSharedPreference(context).getString(Variables.U_PIC, ""));

        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("JoinStream")
                .child(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))
                .setValue(model);
    }



    private void startBroadcast() {
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
    }

    private void stopBroadcast() {
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
    }



    // when user goes to offline then change the value status on firebase
    public void removeJoinNode() {
        if(rootref!=null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("JoinStream")
                    .child(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))
                    .removeValue();
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cross_btn:
            {
                onBackPressed();
            }
            break;
            case R.id.tabJoinVideo:
            {
                if (isCameraConnect)
                {
                    ShowDailogForJoinBroadcast();
                }
                else
                {
                    sendCameraRequest();
                }
            }
            break;
            case R.id.tabGift:
            {
                ShowGiftSheet();
            }
            break;
            case R.id.tvMessage:
            {
                sendComment();
            }
                break;
            case R.id.tabLikeStreaming:
            {
                if (isSendHeart)
                {
                    isSendHeart=true;
                    addLikeIntoStream();
                }
            }
            break;
            case R.id.btnfollow:
            {
                if (Functions.checkLoginUser(SingleCastJoinA.this))
                    followUnFollowUser();
            }
            break;
        }
    }


    private void followUnFollowUser() {
        Functions.callApiForFollowUnFollow(SingleCastJoinA.this,
                Functions.getSharedPreference(context).getString(Variables.U_ID, ""),
                item.getUserId(),
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {
                    }

                    @Override
                    public void onSuccess(String responce) {

                        callApiForGetAllvideos(item.getUserId(),item.getUserName());
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });

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

    private void sendCameraRequest() {
        CameraRequestModel model=new CameraRequestModel();
        model.setRequestState("1");
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CameraRequest")
                .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete())
                {
                    Toast.makeText(context, context.getString(R.string.camera_request_sended), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void heartsShow()
    {
        streamLikeView.addHeart(new Random().nextInt(5));
    }

    private void showAllUserLikes() {
        if(!(item.getUserId().equalsIgnoreCase(Variables.sharedPreferences.getString(Variables.U_ID,""))))
        {
            if (isLikeStream)
            {
                addLikeComment("like");
                isLikeStream=false;
            }
        }
    }




    // get the profile details of user
    boolean isRunFirstTime = false;

    private void callApiForGetAllvideos(String userId,String userName) {

        JSONObject parameters = new JSONObject();
        try {

            if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false))
            {
                if (userId!=null && userName!=null)
                {
                    if (userId.equals(Functions.getSharedPreference(context).getString(Variables.U_ID, "")))
                    {
                        parameters.put("user_id", userId);
                    }
                    else
                    {
                        parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
                        parameters.put("other_user_id", userId);
                    }

                }
                else
                {
                    parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
                    parameters.put("username", userName);
                }
            }
            else
            {
                if (userId!=null && userName!=null)
                {
                    parameters.put("user_id", userId);
                }
                else
                {
                    parameters.put("username", userName);
                }
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }


        VolleyRequest.JsonPostRequest(SingleCastJoinA.this, ApiLinks.showUserDetail, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(SingleCastJoinA.this,resp);
                isRunFirstTime = true;
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msg = jsonObject.optJSONObject("msg");

                        UserModel userDetailModel = DataParsing.getUserDataModel(msg.optJSONObject("User"));
                        String follow_status = userDetailModel.getButton().toLowerCase();
                        if (!userDetailModel.getId().
                                equals(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))) {

                            if (follow_status.equalsIgnoreCase("following")) {
                                btnfollow.setVisibility(View.GONE);
                            } else if (follow_status.equalsIgnoreCase("friends")) {
                                btnfollow.setVisibility(View.GONE);
                            }
                            else if (follow_status.equalsIgnoreCase("follow back")){

                                btnfollow.setVisibility(View.VISIBLE);
                                btnfollow.setText(getString(R.string.follow_back));
                            }
                            else {
                                btnfollow.setVisibility(View.VISIBLE);
                                btnfollow.setText(getString(R.string.follow));
                            }


                        }


                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });

    }


    boolean isAudioActivated=true,isVideoActivated=true,isbeautyActivated=true;
    public void ShowDailogForJoinBroadcast() {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.live_join_broadcast_view);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

        ImageView swith_camera_btn=alertDialog.findViewById(R.id.swith_camera_btn);
        ImageView live_btn_mute_audio=alertDialog.findViewById(R.id.live_btn_mute_audio);
        ImageView live_btn_beautification=alertDialog.findViewById(R.id.live_btn_beautification);
        ImageView live_btn_mute_video=alertDialog.findViewById(R.id.live_btn_mute_video);
        RelativeLayout tab_cancel = alertDialog.findViewById(R.id.tab_cancel);
        ImageView closeBtn = alertDialog.findViewById(R.id.closeBtn);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        live_btn_mute_audio.setActivated(!isAudioActivated);
        live_btn_mute_video.setActivated(!isVideoActivated);
        live_btn_beautification.setActivated(!isbeautyActivated);

        setBeautyEffectOptions(live_btn_mute_video.isActivated());

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
                muteLocalAudioStream(isAudioActivated);
                view.setActivated(!isAudioActivated);
            }
        });
        live_btn_beautification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isbeautyActivated=view.isActivated();
                view.setActivated(!isbeautyActivated);
                setBeautyEffectOptions(isbeautyActivated);
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

    public void ShowGiftSheet() {
        StickerGiftF giftFragment = new StickerGiftF(item.getUserId(), item.getUserName(), item.getUserPicture(), new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    StickerModel model= (StickerModel) bundle.getSerializable("Data");
                    String counter=bundle.getString("count");
                    addGiftComment("gift",counter,model);

                    Log.d(com.qboxus.tictic.Constants.tag,"Test : "+streamerLiveModel.getUserCoins());
                    if(streamerLiveModel!=null)
                    {
                        double userCoins=Double.valueOf(streamerLiveModel.getUserCoins());
                        double userGift=(Long.valueOf(counter)*Double.valueOf(model.coins));
                        userGift=userCoins+userGift;
                        HashMap map = new HashMap();
                        map.put("userCoins",""+userGift);
                        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).updateChildren(map);
                    }
                }
                else
                {
                    if (bundle.getBoolean("showCount",false))
                    {
                        StickerModel model= (StickerModel) bundle.getSerializable("Data");
                        tvGiftCount.setText(" X "+bundle.getString("count")+" "+ model.name);

                        ivGiftCount.setController(Functions.frescoImageLoad(model.getImage(),ivGiftCount,false));

                        tabGiftCount.animate().translationY(animationCapture.getY()).setDuration(700).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                tabGiftCount.setAlpha(1);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tabGiftCount.clearAnimation();
                                tabGiftCount.animate().alpha(0).translationY(0).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        tabGiftCount.clearAnimation();
                                    }
                                }).start();

                            }
                        }).start();

                    }
                }
            }
        });
        giftFragment.show(getSupportFragmentManager(), "");
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
        player = MediaPlayer.create(context, R.raw.gift_tone);
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


        final String key = rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(context).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(context).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(context).getString(Variables.U_PIC, "");

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

        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").child(key).setValue(commentItem);

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }


    // send the comment to the live user
    public void addLikeComment(String type) {

        final String key = rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(context).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(context).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(context).getString(Variables.U_PIC, "");

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
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").child(key).setValue(commentItem);

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }


    // send the comment to the live user
    public void addGiftComment(String type, String count, StickerModel model) {

        final String key = rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").push().getKey();
        String my_id = Functions.getSharedPreference(context).getString(Variables.U_ID, "");
        String my_name = Functions.getSharedPreference(context).getString(Variables.U_NAME, "");
        String my_image = Functions.getSharedPreference(context).getString(Variables.U_PIC, "");

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
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("Chat").child(key).setValue(commentItem);
        LiveCoinsModel coinsModel=new LiveCoinsModel();
        coinsModel.setUserId(my_id);
        coinsModel.setUserName(my_name);
        coinsModel.setUserPic(my_image);
        coinsModel.setSendedCoins(""+(Double.valueOf(count)*Double.valueOf(model.coins)));
        rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    LiveCoinsModel preModel=snapshot.getValue(LiveCoinsModel.class);
                    double totalCoins=Double.valueOf(preModel.getSendedCoins());
                    totalCoins=totalCoins+((Double.valueOf(count)*Double.valueOf(model.coins)));

                    HashMap<String,Object> updateMap=new HashMap<>();
                    updateMap.put("sendedCoins",""+totalCoins);

                    rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").child(my_id).updateChildren(updateMap);
                }
                else
                {
                    rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CoinsStream").child(my_id).setValue(coinsModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tvMessage.setText(context.getString(R.string.add_a_comment));

    }


    private void lounchStreamerCam() {
        joinStream();
        AddJoinNode();
        ListenerCoinNode();
        ListenerJoinNode();
        ListCommentData();
        addLikeStream();
        addNodeCameraRequest();
        addStreamerOnlineStatus();

    }


    ValueEventListener  cameraRequestEventListener;
    boolean isCameraConnect=false;
    private void addNodeCameraRequest() {
        if(cameraRequestEventListener==null)
        {

            cameraRequestEventListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        CameraRequestModel model=snapshot.getValue(CameraRequestModel.class);
                        if (model.getRequestState().equals("2"))
                        {
                            Toast.makeText(context, context.getString(R.string.camera_request_granted), Toast.LENGTH_SHORT).show();
//                            tabJoinVideo.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_duet_post_new));
                            isCameraConnect=true;
                        }
                        else
                        if(model.getRequestState().equals("1"))
                        {
//                            tabJoinVideo.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_non_duet_post_new));
                            isCameraConnect=false;
                        }
                        else
                        {
//                            tabJoinVideo.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_non_duet_post_new));
                            isCameraConnect=false;
                            Toast.makeText(context, context.getString(R.string.camera_request_rejected), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    isCameraConnect=false;
                }
            };
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CameraRequest").addValueEventListener(cameraRequestEventListener);

        }
    }

    private void removeNodeCameraRequest() {
        if (rootref!=null && cameraRequestEventListener != null)
        {
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("CameraRequest").removeEventListener(cameraRequestEventListener);
            cameraRequestEventListener =null;
        }
    }



    private Timer timer = new Timer();
    private final long DELAY = 20000;
    ChildEventListener  streamerOnlineListener;
    private void addStreamerOnlineStatus() {
        if(streamerOnlineListener==null)
        {
            streamerOnlineListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        UserOnlineModel itemUpdate=snapshot.getValue(UserOnlineModel.class);
                        if (item.getUserId().equalsIgnoreCase(itemUpdate.getUserId()))
                        {
                            if (timer!=null)
                            {
                                Functions.cancelIndeterminentLoader();
                                timer.cancel();
                            }
                        }

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                       SingleCastJoinA.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserOnlineModel itemUpdate=snapshot.getValue(UserOnlineModel.class);
                                if (item.getUserId().equalsIgnoreCase(itemUpdate.getUserId()))
                                {
                                    Functions.showIndeterminentLoader(SingleCastJoinA.this,itemUpdate.getUserName()+" "+context.getString(R.string.single_is_week)
                                            ,false,false);
                                    timer.cancel();
                                    timer = new Timer();
                                    timer.schedule(
                                            new TimerTask() {
                                                @Override
                                                public void run() {
                                                    SingleCastJoinA.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Functions.cancelIndeterminentLoader();
                                                            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).removeValue();
                                                        }
                                                    });
                                                }
                                            },
                                            DELAY
                                    );
                                }

                            }
                        });

                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            rootref.child(Variables.onlineUser).addChildEventListener(streamerOnlineListener);

        }
    }

    private void removeStreamerOnlineStatus() {
        if (rootref!=null && streamerOnlineListener != null)
        {
            rootref.child(Variables.onlineUser).removeEventListener(streamerOnlineListener);
            streamerOnlineListener =null;
        }
    }


    ChildEventListener  likeValueEventListener;
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
                        SingleCastJoinA.this.runOnUiThread(new Runnable() {
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
            rootref.child("LiveStreamingUsers").child(item.getStreamingId()).child("LikesStream").addChildEventListener(likeValueEventListener);

        }
    }





    private void joinStream() {
        boolean isBroadcaster = false;
        isAudioActivated=!isBroadcaster;isVideoActivated=!isBroadcaster;
        isbeautyActivated=true;
        setBeautyEffectOptions(isbeautyActivated);
        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        mVideoGridContainer.setStatsManager(setStatsManager());
        setClientRole(userRole);
        if (isBroadcaster)
            startBroadcast();
        mVideoDimension = getconfigDimenIndex();
    }



    private void removeNodeListener() {
        removeJoinNode();
        removeCoinListener();
        removeJoinListener();
        removeCommentListener();
        removeLikeStream();
        removeNodeCameraRequest();
        removeStreamerOnlineStatus();
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
                if (rootref!=null && item!=null)
                {
                    rootref.child("LiveStreamingUsers").child(item.getStreamingId()).removeValue();
                }
                finish();
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Functions.printLog(com.qboxus.tictic.Constants.tag, "onFirstRemoteVideoDecoded");
                renderRemoteUser(uid);
            }
        });
    }

    private void renderRemoteUser(int uid) {
        Functions.printLog(com.qboxus.tictic.Constants.tag, "renderRemoteUser");
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




    public void muteLocalAudioStream(boolean isAudioActivated)
    {
        rtcEngine().muteLocalAudioStream(isAudioActivated);
    }

    public void setBeautyEffectOptions(boolean isbeautyActivated)
    {
        rtcEngine().setBeautyEffectOptions(isbeautyActivated,
                com.qboxus.tictic.activitesfragments.livestreaming.Constants.DEFAULT_BEAUTY_OPTIONS);
    }


    public VideoEncoderConfiguration.VideoDimensions getconfigDimenIndex()
    {
        return com.qboxus.tictic.activitesfragments.livestreaming.Constants.VIDEO_DIMENSIONS[
                config().getVideoDimenIndex()];
    }

    public StatsManager setStatsManager()
    {
        return statsManager();
    }

    public void setClientRole(int userRole)
    {
        rtcEngine().setClientRole(userRole);
    }


    @Override
    public void onBackPressed() {
        removeNodeListener();
        finish();
    }

    @Override
    protected void onDestroy() {
        statsManager().clearAllData();
        super.onDestroy();
    }
}