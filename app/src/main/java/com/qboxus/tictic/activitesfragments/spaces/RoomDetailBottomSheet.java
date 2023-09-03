package com.qboxus.tictic.activitesfragments.spaces;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.chat.ChatA;
import com.qboxus.tictic.activitesfragments.profile.ReportTypeA;
import com.qboxus.tictic.activitesfragments.spaces.adapters.CurrentSpeakerRoomAdapter;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.activitesfragments.spaces.services.RoomStreamService;
import com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager.MainStreamingModel;
import com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager.RoomFirebaseListener;
import com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager.RoomFirebaseManager;
import com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager.RoomManager;
import com.qboxus.tictic.databinding.CurrentRoomLayoutSheetBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.InviteForSpeakModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RoomDetailBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    CurrentRoomLayoutSheetBinding binding;
    MainStreamingModel mainStreamingModel;
    DatabaseReference reference;
    HomeUserModel myUserModel=null;


    public RoomDetailBottomSheet() {

    }

    public static RoomDetailBottomSheet newInstance(MainStreamingModel mainStreamingModel, FragmentCallBack fragmentCallBack) {
        RoomDetailBottomSheet fragment = new RoomDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("data",mainStreamingModel);
        fragment.setArguments(args);
        return fragment;
    }


    private BottomSheetBehavior mBehavior;
    BottomSheetDialog dialog;


    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.current_room_layout_sheet, null);
        dialog.setContentView(view);

        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mBehavior.setPeekHeight(metrics.heightPixels);
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState!=BottomSheetBehavior.STATE_EXPANDED)
                {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        return  dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.current_room_layout_sheet, container, false);


         mainStreamingModel=(MainStreamingModel) getArguments().getSerializable("data");

         reference= FirebaseDatabase.getInstance().getReference();





        initRoomSheet();
        connectWithRoom();

        return binding.getRoot();
    }




    RoomManager roomManager;
    RoomFirebaseManager firebaseRoomManager;
    public void addManagerListeners(){


        firebaseRoomManager= RoomFirebaseManager.getInstance(getActivity());
        firebaseRoomManager.setMainStreamingModel(mainStreamingModel);
        firebaseRoomManager.setListerner3(new RoomFirebaseListener() {
            @Override
            public void createRoom(Bundle bundle) {

            }

            @Override
            public void JoinedRoom(Bundle bundle) {

            }

            @Override
            public void onRoomLeave(Bundle bundle) {

                closeRoomScreen();
            }

            @Override
            public void onRoomDelete(Bundle bundle) {
                closeRoomScreen();
            }

            @Override
            public void onRoomUpdate(Bundle bundle) {
                setRoomData();
            }

            @Override
            public void onRoomUsersUpdate(Bundle bundle) {
                setRoomUserData();

            }

            @Override
            public void onMyUserUpdate(Bundle bundle) {
                setMyUserModelData();
            }

            @Override
            public void onSpeakInvitationReceived(Bundle bundle) {

            }

            @Override
            public void onWaveUserUpdate(Bundle bundle) {

            }

        });

        roomManager=RoomManager.getInstance(getActivity());


    }



    private void connectWithRoom() {

        addManagerListeners();

        setupSpeakerRoomAdapter();
        setupAudienceRoomAdapter();

        setRoomData();
        setRoomUserData();
        setMyUserModelData();
    }

    private void initRoomSheet() {
        binding.ivRoomShare.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.ivRoomShare);

        binding.ivRoomClose.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.ivRoomClose);

        binding.ivOption.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.ivOption);

        binding.tabRoomChat.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabRoomChat);


        binding.tabLeaveQueitly.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabLeaveQueitly);

        binding.tabQueitly.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabQueitly);

        binding.tabRaiseHand.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabRaiseHand);

        binding.tabRiseHandUser.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabRiseHandUser);

        binding.tabMice.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabMice);


    }


    CurrentSpeakerRoomAdapter speakerAdapter;
    private void setupSpeakerRoomAdapter() {
        GridLayoutManager layoutManager=new GridLayoutManager(binding.getRoot().getContext(),3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerviewSpeaker.setLayoutManager(layoutManager);
        speakerAdapter=new CurrentSpeakerRoomAdapter(firebaseRoomManager.getSpeakersUserList(), new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                HomeUserModel itemUpdate= firebaseRoomManager.getSpeakersUserList().get(pos);
                switch (view.getId())
                {
                    case R.id.tabMain:
                    {
                        openUserProfile(itemUpdate);
                    }
                    break;
                }

            }
        });
        binding.recylerviewSpeaker.setAdapter(speakerAdapter);
    }

    CurrentSpeakerRoomAdapter audienceAdapter;
    private void setupAudienceRoomAdapter() {
        GridLayoutManager layoutManager=new GridLayoutManager(binding.getRoot().getContext(),3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerviewOtherUser.setLayoutManager(layoutManager);
        audienceAdapter=new CurrentSpeakerRoomAdapter(firebaseRoomManager.getAudienceUserList(), new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                HomeUserModel itemUpdate= firebaseRoomManager.getAudienceUserList().get(pos);
                switch (view.getId())
                {
                    case R.id.tabMain: {
                        openUserProfile(itemUpdate);
                    }
                    break;
                }

            }
        });
        binding.recylerviewOtherUser.setAdapter(audienceAdapter);
    }



    public void setRoomData(){
        mainStreamingModel= firebaseRoomManager.getMainStreamingModel();
        myUserModel = firebaseRoomManager.getMyUserModel();

        if(!(TextUtils.isEmpty(mainStreamingModel.getModel().getTitle())))
        {
            binding.roomTitle.setText(""+mainStreamingModel.getModel().getTitle());
        }

        if (myUserModel!=null && myUserModel.getUserRoleType().equals("0")) {
            if(mainStreamingModel.getModel().getRiseHandRule().equals("1")) {
                binding.tabRaiseHand.setVisibility(View.VISIBLE);
            }
            else {
                binding.tabRaiseHand.setVisibility(View.GONE);
            }
        }
    }

    public void setMyUserModelData() {
        mainStreamingModel = firebaseRoomManager.getMainStreamingModel();
        myUserModel = firebaseRoomManager.getMyUserModel();


        if (myUserModel != null) {


            if (myUserModel.getUserRoleType().equals("1") || myUserModel.getUserRoleType().equals("2")) {


                if (myUserModel.getMice().equals("1")) {
                    binding.ivMice.setImageDrawable(ContextCompat.getDrawable(binding.getRoot().getContext(),
                            R.drawable.ic_mice));

                    if (RoomStreamService.streamingInstance != null && RoomStreamService.streamingInstance.ismAudioMuted())
                        RoomStreamService.streamingInstance.enableVoiceCall();

                } else {
                    binding.ivMice.setImageDrawable(ContextCompat.getDrawable(binding.getRoot().getContext(),
                            R.drawable.ic_mice_mute));

                    if (RoomStreamService.streamingInstance != null && !RoomStreamService.streamingInstance.ismAudioMuted())
                        RoomStreamService.streamingInstance.muteVoiceCall();
                }

                binding.tabMice.setVisibility(View.VISIBLE);
                binding.tabRaiseHand.setVisibility(View.GONE);
                binding.tabRiseHandUser.setVisibility(View.VISIBLE);
            }

            else {
                if (myUserModel.getRiseHand().equals("1")) {
                    binding.ivRaiseHand.setImageDrawable(ContextCompat.getDrawable(
                            binding.getRoot().getContext(), R.drawable.ic_hand
                    ));
                } else {
                    binding.ivRaiseHand.setImageDrawable(ContextCompat.getDrawable(
                            binding.getRoot().getContext(), R.drawable.ic_hand_black
                    ));
                }

                if (RoomStreamService.streamingInstance != null && !RoomStreamService.streamingInstance.ismAudioMuted())
                    RoomStreamService.streamingInstance.muteVoiceCall();


                binding.tabMice.setVisibility(View.GONE);
                binding.tabRiseHandUser.setVisibility(View.GONE);
            }

            if (myUserModel.getUserRoleType().equals("1")) {
                binding.tabRiseHandUser.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setRoomUserData(){
        mainStreamingModel= firebaseRoomManager.getMainStreamingModel();
        myUserModel = firebaseRoomManager.getMyUserModel();

        speakerAdapter.notifyDataSetChanged();
        audienceAdapter.notifyDataSetChanged();

        if (Integer.parseInt(mainStreamingModel.getModel().getRiseHandCount())>0)
        {
            binding.tvRiseHandCount.setText(""+Functions.getSuffix(""+mainStreamingModel.getModel().getRiseHandCount()));
            binding.tvRiseHandCount.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvRiseHandCount.setText("0");
            binding.tvRiseHandCount.setVisibility(View.GONE);
        }

        if(firebaseRoomManager.getSpeakersUserList().size()>0) {
            checkRoomOwnerOnline();
        }
    }


    public void checkRoomOwnerOnline(){
        String online="0";
        for (int i=0;i<firebaseRoomManager.getSpeakersUserList().size();i++){

            Functions.printLog(Constants.tag,"ID:"+firebaseRoomManager.getSpeakersUserList().get(i).getUserModel().getId());
            if(firebaseRoomManager.getSpeakersUserList().get(i).getOnline()!=null &&
                    firebaseRoomManager.getSpeakersUserList().get(i).getOnline().equals("1")){
                online="1";
                Functions.printLog(Constants.tag,"Online:"+firebaseRoomManager.getSpeakersUserList().get(i).getOnline());
            }
        }

        Functions.printLog(Constants.tag,"Online2:"+online);
        if(online.equals("0")){
            roomManager.deleteRoom(mainStreamingModel.getModel().getId());
        }

    }

    private void openUserProfile(HomeUserModel itemUpdate) {
        Log.d(Constants.tag,"AdminUser: "+itemUpdate.userModel.getId()+"    "+mainStreamingModel.getModel().getId());
        Log.d(Constants.tag,"AdminUserName: "+itemUpdate.userModel.getUsername()+"    "+mainStreamingModel.getModel().getTitle());

        final OtherUserProfileF fragment =OtherUserProfileF.newInstance(itemUpdate.userModel,mainStreamingModel.getModel().getId(),itemUpdate.getUserRoleType(), firebaseRoomManager.getSpeakersUserList(), new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow"))
                {
                    handleProfileClick(bundle,itemUpdate.userModel);
                }
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), "UserProfileF");
    }

    private void handleProfileClick(Bundle bundle,UserModel userModel){

        if (bundle.getString("action").equals("openChat")) {
            openChat(userModel);
        }

        else if (bundle.getString("action").equals("moveToAudience")) {
            moveToRoomAudiance(userModel);
        }

        else if (bundle.getString("action").equals("inviteToSpeaker")) {
            sendInvitationForSpeak(userModel);
        }

        else if (bundle.getString("action").equals("acceptInviteToSpeaker")) {

            roomManager.speakerJoinRoomHitApi(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""),mainStreamingModel.getModel().getId(),"2");
        }

        else if (bundle.getString("action").equals("makeToModerator")) {
            makeRoomModerator(userModel);
        }

        else if (bundle.getString("action").equals("makeModeratorToSpeakerAndLeave")) {
            makeModeratorToSpeakerAndLeave(userModel,(HomeUserModel)bundle.getSerializable("speakerModel"));
        }

    }

    public void openChat(UserModel userModel){
        Intent intent1=new Intent(getActivity(), ChatA.class);
        intent1.putExtra("user_id",userModel.getId());
        intent1.putExtra("user_name", userModel.getUsername());
        intent1.putExtra("user_pic",userModel.getProfilePic());
        startActivity(intent1);
    }

    public void openRoomReport() {
        Intent intent = new Intent(getActivity(), ReportTypeA.class);
        intent.putExtra("roomData", mainStreamingModel);
        intent.putExtra("isFrom", "room");
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    private void moveToRoomAudiance(UserModel userModel) {
        if (mainStreamingModel!=null)
        {
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                    child(Variables.roomUsers)
                    .child(userModel.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                                dataItem.setUserRoleType("0");

                                reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                                        child(Variables.roomUsers)
                                        .child(userModel.getId())
                                        .setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                                                            child(Variables.roomInvitation)
                                                            .child(userModel.getId())
                                                            .removeValue();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    }


    private void makeRoomModerator(UserModel userModel) {
        if (mainStreamingModel!=null) {

            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                    child(Variables.roomUsers)
                    .child(userModel.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                                dataItem.setUserRoleType("1");

                                reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                                        child(Variables.roomUsers)
                                        .child(userModel.getId())
                                        .setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Functions.showSuccess(getActivity(),binding.getRoot().getContext().getString(R.string.great_they_are_now_moderator));
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    private void makeModeratorToSpeakerAndLeave(UserModel userModel, HomeUserModel speakerModel) {

        makeRoomModerator(userModel,speakerModel);
    }

    private void makeRoomModerator(UserModel userModel,HomeUserModel speakerModel) {
        if (mainStreamingModel!=null) {

            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                    child(Variables.roomUsers)
                    .child(speakerModel.getUserModel().getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                                dataItem.setUserRoleType("1");

                                reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                                        child(Variables.roomUsers)
                                        .child(speakerModel.getUserModel().getId())
                                        .setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    roomManager.speakerJoinRoomHitApi(userModel.getId(),mainStreamingModel.getModel().getId(),"0");
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }




    private void closeRoomScreen() {
      dismiss();
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivOption:
            {
                openRoomSettingOption();
            }
            break;

            case R.id.tabRoomChat: {
                openRoomChat();
            }
            break;
            case R.id.ivRoomClose:
            {
                closeRoomScreen();
            }
            break;
            case R.id.ivRoomShare:
            {
                Functions.shareData(getActivity(),Functions.getShareRoomLink(getContext(),mainStreamingModel.getModel().getId()));
            }
            break;

            case R.id.tabRiseHandUser:
            {
                openRiseHandList();
            }
            break;

            case R.id.tabRaiseHand:
            {
                openRiseHandToSpeak();
            }
            break;

            case R.id.tabLeaveQueitly:
            {
                removeRoom();
            }

            case R.id.tabMice:
            {
                updateMyMiceStatus();
            }
            break;
        }
    }


    private void updateMyMiceStatus() {
        if(RoomStreamService.streamingInstance!=null)
        {
            HashMap<String,Object> updateMice=new HashMap<>();
            if(RoomStreamService.streamingInstance.ismAudioMuted())
            {
                updateMice.put("mice","1");
            }
            else
            {
                updateMice.put("mice","0");
            }
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId())
                    .child(Variables.roomUsers).child(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""))
                    .updateChildren(updateMice).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {  }
                        }
                    });
        }
    }


    private void openRoomSettingOption() {
        final RoomStreamingSettingF fragment = new RoomStreamingSettingF(firebaseRoomManager.getSpeakersUserList(), new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow"))
                {
                    String actionType=bundle.getString("action");
                    if (actionType.equals("ShareRoom"))
                    {
                        Functions.shareData(getActivity(),Functions.getShareRoomLink(getContext(),mainStreamingModel.getModel().getId()));

                    }
                    else
                    if (actionType.equals("EndRoom"))
                    {
                        roomManager.deleteRoom(mainStreamingModel.getModel().getId());
                    }
                    else
                    if (actionType.equals("UserShareRoom"))
                    {

                    }
                    else
                    if (actionType.equals("UserReportRoomTitle"))
                    {
                        openRoomReport();
                    }
                }
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), "RoomStreamingSettingF");
    }




    private void openRoomChat() {
        RoomChatF roomChatF = RoomChatF.newInstance(mainStreamingModel, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
            }
        });
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top,
                R.anim.in_from_top, R.anim.out_from_bottom);
        ft.replace(R.id.mainRoomContainer, roomChatF,"RoomChatF")
                .addToBackStack("RoomChatF").commit();
    }



    private void removeRoom() {
        Bundle bundle = roomManager.checkRoomCanDeleteOrLeave(firebaseRoomManager.getSpeakersUserList());
        Functions.printLog(Constants.tag,bundle.getString("action"));
        if (bundle.getString("action").equals("removeRoom")) {
            roomManager.deleteRoom(mainStreamingModel.getModel().getId());
        }

        else if (bundle.getString("action").equals("leaveRoom")) {
            roomManager.leaveRoom(mainStreamingModel.getModel().getId());
        }

        else {
            HomeUserModel speakerAsModeratorModel = (HomeUserModel)bundle.getSerializable("model");
            makeRoomModeratorAndLeave(speakerAsModeratorModel);
        }
    }



    private void openRiseHandToSpeak() {
        RiseHandForSpeakF riseHandForSpeakF = new RiseHandForSpeakF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if(bundle.getBoolean("isShow"))
                {
                    if (bundle.getString("action").equals("riseHandForSpeak"))
                    {

                        HashMap<String,Object> riseHandMap=new HashMap<>();
                        riseHandMap.put("riseHand","1");

                        reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId())
                                .child(Variables.roomUsers).child(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""))
                                .updateChildren(riseHandMap);
                    }
                    else
                    if (bundle.getString("action").equals("neverMind"))
                    {
                        HashMap<String,Object> riseHandMap=new HashMap<>();
                        riseHandMap.put("riseHand","0");

                        reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId())
                                .child(Variables.roomUsers).child(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""))
                                .updateChildren(riseHandMap);
                    }
                }
            }
        });
        riseHandForSpeakF.show(getActivity().getSupportFragmentManager(), "RiseHandForSpeakF");
    }

    private void openRiseHandList() {
        RiseHandUsersF fragment = new RiseHandUsersF(mainStreamingModel.getModel().getId(),mainStreamingModel.getModel().getRiseHandRule(),new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if(bundle.getBoolean("isShow"))
                {
                    if(bundle.getString("action").equals("invite"))
                    {
                        HomeUserModel itemUpdate= (HomeUserModel) bundle.getSerializable("itemModel");
                        sendInvitationForSpeak(itemUpdate.userModel);
                    }

                }
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), "RiseHandUsersF");
    }


    private void sendInvitationForSpeak(UserModel userModel) {
        if (mainStreamingModel!=null)
        {
            InviteForSpeakModel invitation=new InviteForSpeakModel();
            invitation.setInvite("1");
            invitation.setUserId(Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""));
            invitation.setUserName(Functions.getSharedPreference(getContext()).getString(Variables.U_NAME,""));

            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                    child(Variables.roomInvitation)
                    .child(userModel.getId())
                    .setValue(invitation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Functions.showSuccess(getActivity(),binding.getRoot().getContext().getString(R.string.great_we_are_sent_them_an_invite));
                            }
                        }
                    });
        }

    }


    private void makeRoomModeratorAndLeave(HomeUserModel itemUpdate) {
        if (mainStreamingModel.getModel()!=null)
        {
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                    child(Variables.roomUsers)
                    .child(itemUpdate.getUserModel().getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                                dataItem.setUserRoleType("1");

                                reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                                        child(Variables.roomUsers)
                                        .child(itemUpdate.getUserModel().getId())
                                        .setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    roomManager.leaveRoom(mainStreamingModel.getModel().getId());
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    }


    @Override
    public void onDetach() {
        if(firebaseRoomManager!=null)
            firebaseRoomManager.setListerner3(null);

        super.onDetach();
    }
}