package com.qboxus.tictic.activitesfragments.profile.usersstory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.tictic.activitesfragments.chat.ChatA;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.databinding.FragmentStoryItemBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.StoryModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.models.StoryVideoModel;
import com.qboxus.tictic.simpleclasses.CountDownTimerPausable;
import com.qboxus.tictic.simpleclasses.DebounceClickHandler;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.OnStoryTouchListener;
import com.qboxus.tictic.simpleclasses.OnSwipeTouchListener;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class StoryItemF extends Fragment implements Player.Listener{

    ///Story VIew
    FragmentStoryItemBinding bindingRef;
    ExoPlayer exoplayer;
    StoryModel selectedStoryItem;
    ArrayList<ProgressBar> pBarList=new ArrayList<>();
    int maxProgressTime=10000;
    int currentIndex=0;
    int targetIndex=0;
    CountDownTimerPausable currentTimer;
    DatabaseReference rootref;
    private DatabaseReference adduserInbox;
    FragmentCallBack callBack;

    ArrayList<StoryModel> allDataList;
    int currentPagePosition;

    public StoryItemF(ArrayList<StoryModel> allDataList, int currentPagePosition, FragmentCallBack callBack) {
        this.allDataList = allDataList;
        this.currentPagePosition=currentPagePosition;
        this.callBack=callBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bindingRef= DataBindingUtil.inflate(inflater, R.layout.fragment_story_item, container, false);
        initControl();
        actionControl();
        return bindingRef.getRoot();
    }

    private void initControl() {
        rootref = FirebaseDatabase.getInstance().getReference();
        adduserInbox = FirebaseDatabase.getInstance().getReference();
        selectedStoryItem= allDataList.get(currentPagePosition);
        setuplinearLayoutWithProgress();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void actionControl() {
        bindingRef.mediaContainer.setOnTouchListener(new OnStoryTouchListener(bindingRef.getRoot().getContext()) {
            @Override
            public void onSingleClick(MotionEvent e) {
                float x=e.getX();

                if(x < ( bindingRef.mediaContainer.getWidth() * 0.5 )){
                    Log.d(Constants.tag,"OnLeft click");

                }else{
                    Log.d(Constants.tag,"OnRight click");
                    moveToRightChunk();
                }
            }

            @Override
            public void onButtonReleased() {
                Log.d(Constants.tag,"onReleased Press");
                performResumeAction();
            }

            @Override
            public void onButtonPressed(MotionEvent e) {
                Log.d(Constants.tag,"onPressed Press");
                performStopAction();
            }
        });

        bindingRef.ivOption.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteVideo(view,bindingRef.getRoot().getContext());
            }
        }));

        bindingRef.ivLike.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStoryEmoticons();
            }
        }));

        bindingRef.ivSend.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(bindingRef.etMessage.getText().toString().isEmpty()))
                {
                    sendStoryComment(selectedStoryItem,selectedStoryItem.getVideoList().get(currentIndex));
                }
            }
        }));

        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                        {
                            performStopAction();
                        }
                        else
                        {
                            performResumeAction();
                        }
                    }
                });
    }

    private void openStoryEmoticons() {
        performStopAction();
        StoryEmoticonF fragment =StoryEmoticonF.newInstance(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow"))
                {
                    String emojiCode=bundle.getString("data");
                    sendStoryLike(selectedStoryItem,selectedStoryItem.getVideoList().get(currentIndex),emojiCode);
                }
                else
                {
                    performResumeAction();
                }
            }
        });
        fragment.show(getChildFragmentManager(), "StoryEmoticonF");
    }

    private void moveToRightChunk() {
        if (currentIndex<targetIndex)
        {
            completeChunkProgress(100);
            currentIndex=currentIndex+1;
            moveToVideoChunk();
        }
        else
        {
            MoveToNextUserVideos();
        }
    }

    private void completeChunkProgress(int progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            pBarList.get(currentIndex).setProgress(progress,true);
        }
        else
        {
            pBarList.get(currentIndex).setProgress(progress);
        }
    }

    private void showDeleteVideo(View view,Context context) {
        Context wrapper = new ContextThemeWrapper(context, R.style.AlertDialogCustom);
        PopupMenu popup = new PopupMenu(wrapper, view);

        popup.getMenuInflater().inflate(R.menu.menu_playlist, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.setGravity(Gravity.TOP | Gravity.RIGHT);
        }

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menuDelete:
                    {
                        deleteStoryItem();
                    }
                    break;
                }
                return true;
            }
        });
    }

    // this method will upload the image in chhat
    public void sendStoryLike(StoryModel selectedStory, StoryVideoModel storyModel,String emojiCode) {
        Functions.showLoader(getActivity(),false,false);
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        String senderId=Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_ID, "");
        String receiverId=selectedStory.getUserModel().getId();

        DatabaseReference dref = rootref.child("chat").child(senderId + "-" + receiverId).push();
        final String key = dref.getKey();

        String current_user_ref = "chat" + "/" + senderId + "-" + receiverId;
        String chat_user_ref = "chat" + "/" + receiverId + "-" + senderId;


        JSONObject object=new JSONObject();
        try {
            object.put("storyId",storyModel.getId());
            object.put("storyGif",storyModel.getGif());
            object.put("storyUrl",storyModel.getVideo());
            object.put("storyEmoticon",""+emojiCode);
        }catch (Exception e){}

        HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", receiverId);
        message_user_map.put("sender_id", senderId);
        message_user_map.put("chat_id", key);
        message_user_map.put("text", ""+object);
        message_user_map.put("type", "storyLike");
        message_user_map.put("pic_url", selectedStory.getUserModel().getProfilePic());
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, ""));
        message_user_map.put("timestamp", formattedDate);
        HashMap user_map = new HashMap<>();

        user_map.put(current_user_ref + "/" + key, message_user_map);
        user_map.put(chat_user_ref + "/" + key, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Functions.cancelLoader();
                performResumeAction();
                String inbox_sender_ref = "Inbox" + "/" + senderId + "/" + receiverId;
                String inbox_receiver_ref = "Inbox" + "/" + receiverId + "/" + senderId;

                String messageForPush=Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, "")
                        +" liked your story...";

                HashMap sendermap = new HashMap<>();
                sendermap.put("rid", senderId);
                sendermap.put("name", Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, ""));
                sendermap.put("pic", Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_PIC, ""));
                sendermap.put("msg", ""+messageForPush);
                sendermap.put("status", "0");
                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                sendermap.put("date", formattedDate);

                HashMap receivermap = new HashMap<>();
                receivermap.put("rid", receiverId);
                receivermap.put("name", selectedStory.getUserModel().getUsername());
                receivermap.put("pic", selectedStory.getUserModel().getProfilePic());
                receivermap.put("msg", ""+messageForPush);
                receivermap.put("status", "1");
                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                receivermap.put("date", formattedDate);

                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref, receivermap);
                both_user_map.put(inbox_receiver_ref, sendermap);

                adduserInbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ChatA.sendPushNotification(getActivity(), Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, ""), "Send an gif image....",
                                receiverId, senderId);

                    }
                });

            }
        });
    }


    // this method will upload the image in chhat
    public void sendStoryComment(StoryModel selectedStory, StoryVideoModel storyModel) {
        Functions.showLoader(getActivity(),false,false);
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        String senderId=Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_ID, "");
        String receiverId=selectedStory.getUserModel().getId();

        DatabaseReference dref = rootref.child("chat").child(senderId + "-" + receiverId).push();
        final String key = dref.getKey();

        String current_user_ref = "chat" + "/" + senderId + "-" + receiverId;
        String chat_user_ref = "chat" + "/" + receiverId + "-" + senderId;


        JSONObject object=new JSONObject();
        try {
            object.put("storyId",storyModel.getId());
            object.put("storyGif",storyModel.getGif());
            object.put("storyUrl",storyModel.getVideo());
            object.put("storyComment",""+bindingRef.etMessage.getText().toString());
        }catch (Exception e){}


        HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", receiverId);
        message_user_map.put("sender_id", senderId);
        message_user_map.put("chat_id", key);
        message_user_map.put("text", ""+object);
        message_user_map.put("type", "storyComment");
        message_user_map.put("pic_url", selectedStory.getUserModel().getProfilePic());
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, ""));
        message_user_map.put("timestamp", formattedDate);
        HashMap user_map = new HashMap<>();

        user_map.put(current_user_ref + "/" + key, message_user_map);
        user_map.put(chat_user_ref + "/" + key, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Functions.cancelLoader();
                bindingRef.etMessage.setText("");
                String inbox_sender_ref = "Inbox" + "/" + senderId + "/" + receiverId;
                String inbox_receiver_ref = "Inbox" + "/" + receiverId + "/" + senderId;

                String messageForPush=Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, "")
                        +" commented on your story...";

                HashMap sendermap = new HashMap<>();
                sendermap.put("rid", senderId);
                sendermap.put("name", Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, ""));
                sendermap.put("pic", Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_PIC, ""));
                sendermap.put("msg", ""+messageForPush);
                sendermap.put("status", "0");
                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                sendermap.put("date", formattedDate);

                HashMap receivermap = new HashMap<>();
                receivermap.put("rid", receiverId);
                receivermap.put("name", selectedStory.getUserModel().getUsername());
                receivermap.put("pic", selectedStory.getUserModel().getProfilePic());
                receivermap.put("msg", ""+messageForPush);
                receivermap.put("status", "1");
                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                receivermap.put("date", formattedDate);

                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref, receivermap);
                both_user_map.put(inbox_receiver_ref, sendermap);

                adduserInbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ChatA.sendPushNotification(getActivity(), Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_NAME, ""), "Send an gif image....",
                                receiverId, senderId);

                    }
                });

            }
        });
    }



    private void deleteStoryItem() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", selectedStoryItem.getVideoList().get(currentIndex).getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.deleteVideo, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("isShow",true);
                        bundle.putString("action","deleteItem");
                        bundle.putInt("itemPos",currentIndex);
                        callBack.onResponce(bundle);

                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception callBack: "+e);
                }

            }
        });

    }


    private void performResumeAction() {
        if (currentTimer!=null)
        {
            currentTimer.start();
        }
        if (exoplayer!=null)
        {
            exoplayer.play();
        }
    }

    private void performStopAction() {
        if (currentTimer!=null)
        {
            if (!(currentTimer.isPaused()))
            {
                currentTimer.pause();
            }
        }

        if (exoplayer!=null)
        {
            exoplayer.pause();
        }
    }



    private void setuplinearLayoutWithProgress() {
        Log.d(Constants.tag,"onSetup StoryProgress");
        bindingRef.progressView.removeAllViews();
        bindingRef.progressView.setWeightSum(selectedStoryItem.getVideoList().size());

        //delete option manage
        if (selectedStoryItem.getUserModel().getId().equals(Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_ID,"")))
        {
            bindingRef.ivOption.setVisibility(View.VISIBLE);
        }
        else
        {
            bindingRef.ivOption.setVisibility(View.GONE);
        }
        showProfileData();
        setupVideoProgresses(selectedStoryItem);
        startFirstTimeVideo();
    }


    private void startFirstTimeVideo() {
        if (selectedStoryItem.getVideoList().size()>currentIndex)
        {
            if (currentTimer!=null)
            {
                currentTimer.cancel();
                currentTimer=null;
            }
            showMedia();
            currentTimer=new CountDownTimerPausable((maxProgressTime*1000), Functions.getDevidedChunks(maxProgressTime,100)) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int progress= (int) (millisUntilFinished/Functions.getDevidedChunks(maxProgressTime,100));
                    CDTimerInnerProgress= (int) (100-progress);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        pBarList.get(currentIndex).setProgress(CDTimerInnerProgress,true);
                    }
                    else
                    {
                        pBarList.get(currentIndex).setProgress(CDTimerInnerProgress);
                    }

                }

                @Override
                public void onFinish() {
                    if (currentIndex<targetIndex)
                    {
                        currentIndex=currentIndex+1;
                        moveToVideoChunk();
                    }
                    else
                    {
                        MoveToNextUserVideos();
                    }
                }
            }.start();

        }
        else
        {
            storyProgressComplete();
        }
    }

    private void moveToVideoChunk() {
        if (currentTimer!=null)
        {
            currentTimer.cancel();
            currentTimer=null;
        }
        exoplayer=null;
        showMedia();
        currentTimer=new CountDownTimerPausable((maxProgressTime*1000), Functions.getDevidedChunks(maxProgressTime,100)) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress= (int) (millisUntilFinished/Functions.getDevidedChunks(maxProgressTime,100));
                CDTimerInnerProgress= (int) (100-progress);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    pBarList.get(currentIndex).setProgress(CDTimerInnerProgress,true);
                }
                else
                {
                    pBarList.get(currentIndex).setProgress(CDTimerInnerProgress);
                }

            }

            @Override
            public void onFinish() {
                if (currentIndex<targetIndex)
                {
                    currentIndex=currentIndex+1;
                    moveToVideoChunk();
                }
                else
                {
                    MoveToNextUserVideos();
                }
            }
        }.start();
    }


    private void MoveToNextUserVideos() {
        currentPagePosition=currentPagePosition+1;
        if (currentTimer!=null)
        {
            currentTimer.cancel();
            currentTimer=null;
        }
        if (currentPagePosition==(allDataList.size()))
        {
            getActivity().onBackPressed();
        }
        else
        {
            ViewStoryA.mPager.setCurrentItem(currentPagePosition,true);
        }
    }

    private void setupVideoProgresses(StoryModel storyModel) {
        for (int i=0;i<storyModel.getVideoList().size();i++)
        {
            ProgressBar pBar=new ProgressBar(bindingRef.getRoot().getContext(),null, android.R.attr.progressBarStyleHorizontal);
            pBar.setMax(100);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, (int) getResources().getDimension(com.intuit.sdp.R.dimen._10sdp));
            lp.weight = 1;
            if (storyModel.getVideoList().size()>(i+1))
            {
                lp.setMarginEnd((int) getResources().getDimension(com.intuit.sdp.R.dimen._2sdp));
            }
            pBar.setLayoutParams(lp);
            pBar.setProgress(0);
            Drawable progressDrawable = pBar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
            pBar.setProgressDrawable(progressDrawable);
            pBarList.add(pBar);
            bindingRef.progressView.addView(pBarList.get(i));
        }

        targetIndex=selectedStoryItem.getVideoList().size()-1;

    }



    private void showProfileData() {
        String profileUrl = selectedStoryItem.getUserModel().getProfilePic();
        String name = selectedStoryItem.getUserModel().getUsername();

        bindingRef.profilePic.setController(Functions.frescoImageLoad(profileUrl, bindingRef.profilePic,false));
        bindingRef.userName.setText(""+name);

        if (selectedStoryItem.getUserModel().getId().equals(Functions.getSharedPreference(bindingRef.getRoot().getContext()).getString(Variables.U_ID,"")))
        {
            bindingRef.bottomLayout.setVisibility(View.GONE);
        }
        else
        {
            bindingRef.bottomLayout.setVisibility(View.VISIBLE);
        }
    }


    int CDTimerInnerProgress=0;
    private void showMedia() {
        try {
            String time = Functions.getTimeAgoOrg(selectedStoryItem.getVideoList().get(currentIndex).getCreated());
            bindingRef.time.setText("" + time);

            showStoryPlayer();
        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception showMedia: "+e);
        }
    }


    private void showStoryPlayer() {
        performStopAction();

        String videoAttachment=""+selectedStoryItem.getVideoList().get(currentIndex).getVideo();
        if (Functions.isWebUrl(videoAttachment))
        {
            initExoPlayer(videoAttachment);
        }
        else
        {
            Log.d(Constants.tag,"videoAttachment: "+videoAttachment);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Functions.showToastOnTop(getActivity(),null,bindingRef.getRoot().getContext().getString(R.string.invalid_video_url));
                    MoveToNextUserVideos();
                }
            },1500);
        }

    }




    private void storyProgressComplete() {
        Log.d(Constants.tag,"Complete Story Progress");
        if (currentTimer!=null)
        {
            currentTimer.cancel();
            currentTimer=null;
        }
        Log.d(Constants.tag,currentPagePosition+" currentPagePosition: "+ allDataList.size());
        if (currentPagePosition==(allDataList.size()))
        {
            getActivity().onBackPressed();
        }
        else
        {
            currentPagePosition=currentPagePosition+1;
            ViewStoryA.mPager.setCurrentItem(currentPagePosition,true);
        }

    }


    private void initExoPlayer(String videoAttachment) {
        if(exoplayer==null && videoAttachment!=null)
        {

            Log.d(Constants.tag,"Check Exo player Init: "+videoAttachment);
            maxProgressTime= Functions.showVideoDurationInSec(videoAttachment);
            exoplayer =new ExoPlayer.Builder(bindingRef.getRoot().getContext()).
                    setTrackSelector(new DefaultTrackSelector(bindingRef.getRoot().getContext())).
                    setLoadControl(Functions.getExoControler()).
                    build();
                exoplayer.setMediaItem(MediaItem.fromUri(videoAttachment));
                exoplayer.prepare();
                exoplayer.addListener(StoryItemF.this);
                exoplayer.setRepeatMode(Player.REPEAT_MODE_ALL);

            try {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build();
                exoplayer.setAudioAttributes(audioAttributes, true);
            }
            catch (Exception e)
            {
                Log.d(Constants.tag,"Exception audio focus : "+e);
            }
            setPlayer();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bindingRef.playerview.findViewById(R.id.exo_play).setVisibility(View.GONE);
                    if(exoplayer!=null) {
                        bindingRef.playerview.setPlayer(exoplayer);
                    }
                    performResumeAction();
                }
            });


        }
        else
        {
            Log.d(Constants.tag,"initExoPlayer: ");
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        if (currentTimer!=null)
        {
            currentTimer.cancel();
            currentTimer=null;
        }
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            bindingRef.playerview.findViewById(R.id.exo_play).setAlpha(1);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            bindingRef.playerview.findViewById(R.id.exo_play).setAlpha(1);
        }
    }


    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            bindingRef.progressBar.setVisibility(View.VISIBLE);
            Log.d(Constants.tag," buffering ");

        }
        else if (playbackState == Player.STATE_READY) {
            bindingRef.progressBar.setVisibility(View.GONE);
            Log.d(Constants.tag," ready ");
        }
    }

    public void setPlayer() {

        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(true);
            bindingRef.playerview.findViewById(R.id.exo_play).setAlpha(0);
            bindingRef.playerview.setOnTouchListener(new OnSwipeTouchListener(bindingRef.getRoot().getContext()) {

                @Override
                public void onSingleClick() {
                    if (!exoplayer.getPlayWhenReady()) {
                        exoplayer.setPlayWhenReady(true);
                        bindingRef.playerview.findViewById(R.id.exo_play).setAlpha(0);
                    } else {
                        exoplayer.setPlayWhenReady(false);
                        bindingRef.playerview.findViewById(R.id.exo_play).setAlpha(1);
                    }
                }

                @Override
                public void onDoubleClick(MotionEvent e) {
                    if (!exoplayer.getPlayWhenReady()) {
                        exoplayer.setPlayWhenReady(true);
                    }
                }

            });

        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            bindingRef.playerview.findViewById(R.id.exo_play).setAlpha(1);
            exoplayer.removeListener(this);
            exoplayer.release();
            exoplayer = null;
        }
    }


}