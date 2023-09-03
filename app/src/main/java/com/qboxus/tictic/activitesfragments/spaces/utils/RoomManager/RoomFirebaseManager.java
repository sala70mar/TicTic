package com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.models.InviteForSpeakModel;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomFirebaseManager implements RoomFirebaseListener{

    Activity activity;
    DatabaseReference reference;
    RoomManager roomManager;

    MainStreamingModel mainStreamingModel;
    ArrayList<HomeUserModel> speakersUserList =new ArrayList<>();
    ArrayList<HomeUserModel> audienceUserList =new ArrayList<>();
    HomeUserModel myUserModel=null;

    RoomFirebaseListener listerner1;
    RoomFirebaseListener listerner2;
    RoomFirebaseListener listerner3;


    private static volatile RoomFirebaseManager INSTANCE = null;

    public static RoomFirebaseManager getInstance(Activity activity) {
        if(INSTANCE == null) {
            synchronized (RoomFirebaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RoomFirebaseManager(activity);
                }
            }
        }
        return INSTANCE;
    }


    public RoomFirebaseManager(Activity activity) {
        this.activity=activity;
        roomManager=new RoomManager(activity);
       reference= FirebaseDatabase.getInstance().getReference();

        registerJoinListener();
    }

    public void addAllRoomListerner(){
        registerMyRoomListener();
        registerRoomUserListener();
        registerMyJoinRoomListener();
        registerSpeakInvitationListener();

    }


    public void setListerner1(RoomFirebaseListener responseListener) {
        this.listerner1=responseListener;
    }

    public void setListerner2(RoomFirebaseListener responseListener) {
        this.listerner2=responseListener;
    }

    public void setListerner3(RoomFirebaseListener responseListener) {
        this.listerner3=responseListener;
    }





    public void createRoomNode(MainStreamingModel model) {

        reference.child(Variables.roomKey).child(model.getModel().getId()).setValue(model.getModel()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    if (model!=null) {

                        Log.d(Constants.tag,"MainStreamingModel: "+model.getModel().getId());

                        for (HomeUserModel userModel:model.getUserList())
                        {
                            if (userModel.getUserModel().getId().equals(Functions.getSharedPreference(activity).getString(Variables.U_ID,""))) {
                                joinRoom(model.getModel().id,userModel);
                            break;
                            }
                        }

                    }
                }
            }
        });
    }

    public void joinRoom(String roomId, HomeUserModel userModel){
        reference.child(Variables.roomKey).child(roomId).child(Variables.roomUsers)
                .child(userModel.userModel.getId()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            HashMap<String,String> map=new HashMap<>();
                            map.put("roomId",roomId);
                            reference.child(Variables.joinedKey).child(userModel.userModel.getId()).setValue(map);
                        }
                    }
                });

    }

    public void updateMemberModel(HomeUserModel myUserModel) {
        reference.child(Variables.roomKey)
                .child(mainStreamingModel.getModel().getId())
                .child(Variables.roomUsers)
                .child(myUserModel.getUserModel().getId())
                .setValue(myUserModel);
    }


    public void removeRoomNode(String roomID) {
        removeInvitation();
        removeJoindNode();
        reference.child(Variables.roomKey)
                .child(roomID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            removeAllListener();
                            onRoomDelete(null);

                        }
                    }
                });
    }

    public void removeUserLeaveNode(String roomId) {
        removeInvitation();
        removeJoindNode();
        reference.child(Variables.roomKey)
                .child(roomId)
                .child(Variables.roomUsers)
                .child(Functions.getSharedPreference(this.activity).getString(Variables.U_ID,""))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            removeAllListener();
                            onRoomLeave(null);
                        }
                    }
                });
    }




    ValueEventListener myjoinListener;
    private void registerJoinListener() {
        if(myjoinListener == null) {

            Functions.printLog(Constants.tag,"registerJoinListener call");
            myjoinListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        Functions.printLog(Constants.tag,"myjoinListener"+dataSnapshot.toString());
                        String roomId =dataSnapshot.child("roomId").getValue(String.class);
                        Functions.printLog(Constants.tag,"joined User roomId"+roomId);


                            Bundle bundle=new Bundle();
                            bundle.putString("action","roomJoin");
                            bundle.putString("roomId",roomId);
                            JoinedRoom(bundle);

                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.child(Variables.joinedKey).child(Functions.getSharedPreference(this.activity).getString(Variables.U_ID,"")).addValueEventListener(myjoinListener);

        }
    }

    private void removeJoindNode(){
        reference.child(Variables.joinedKey).child(Functions.getSharedPreference(this.activity).getString(Variables.U_ID,"")).removeValue();
    }

    public void removeInvitation(){
        reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                child(Variables.roomInvitation)
                .child(Functions.getSharedPreference(this.activity).getString(Variables.U_ID,"")).setValue(null);
    }







    ValueEventListener myRoomListener;
    private void registerMyRoomListener() {
        if(myRoomListener ==null) {

            myRoomListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {

                        String riseHandRule=  ""+dataSnapshot.child("riseHandRule").getValue(String.class);

                        if (mainStreamingModel==null || mainStreamingModel.getModel()==null)
                        {
                            return;
                        }
                        mainStreamingModel.getModel().setRiseHandRule(riseHandRule);

                        Bundle bundle=new Bundle();
                        bundle.putSerializable("data",mainStreamingModel);
                        onRoomUpdate(bundle);

                    }
                    else {
                       onRoomDelete(null);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).addValueEventListener(myRoomListener);

        }
        else {
            Log.d(Constants.tag,"myRoomListener not null");
        }
    }

    ChildEventListener roomUserUpdateListener;
    private void registerRoomUserListener() {
        if(roomUserUpdateListener==null) {
            roomUserUpdateListener=new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try {
                        Log.d(Constants.tag,"onChildAdded ::");

                        if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                        {
                            HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                            if(dataItem.getUserRoleType().equals("0")){
                                audienceUserList.add(dataItem);
                            }
                            else {
                                speakersUserList.add(dataItem);
                            }

                            if (dataItem.getUserModel().getId().equals(Functions.getSharedPreference(activity).getString(Variables.U_ID,""))) {
                                myUserModel=dataItem;
                            }

                            Log.d(Constants.tag,"speakersUserList size onChildAdded"+speakersUserList.size());
                            Log.d(Constants.tag,"audienceUserList size onChildAdded"+audienceUserList.size());

                            onRoomUsersUpdate(null);
                        }

                    }catch (Exception e)
                    {
                        Log.d(Constants.tag,"onChildAdded: checkPoint:  "+e.getMessage());
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try {

                        Log.d(Constants.tag,"onChildChanged ::");

                        if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                        {
                            HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                            int speakerPostion=getlistPostion(speakersUserList,dataItem);
                            int audiencePosition=getlistPostion(audienceUserList,dataItem);
                            if(dataItem.getUserRoleType().equals("0")){
                                if(speakerPostion>=0){
                                    speakersUserList.remove(speakerPostion);
                                }
                                if(audiencePosition>=0){
                                    audienceUserList.set(audiencePosition, dataItem);
                                }else{
                                    audienceUserList.add(dataItem);
                                }

                            }
                            else {
                                if(audiencePosition>=0){
                                    audienceUserList.remove(audiencePosition);
                                }
                                if(speakerPostion>=0){
                                    speakersUserList.set(speakerPostion, dataItem);
                                }else{
                                    speakersUserList.add(dataItem);
                                }
                            }

                            onRoomUsersUpdate(null);


                            if (dataItem.getUserModel().getId().equals(Functions.getSharedPreference(activity).getString(Variables.U_ID,"")))
                            {
                                if (dataItem.getUserRoleType().equals("1"))
                                {
                                    getRiseHandCounts();
                                }
                            }

                            Log.d(Constants.tag,"speakersUserList size onChildChanged"+speakersUserList.size());
                            Log.d(Constants.tag,"audienceUserList size onChildChanged"+audienceUserList.size());

                        }
                    }
                    catch (Exception e)
                    {Log.d(Constants.tag,"onChildChanged: "+e);}
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    try {
                        Log.d(Constants.tag,"onChildRemoved ::");

                        HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                        int speakerPostion=getlistPostion(speakersUserList,dataItem);
                        int audiencePosition=getlistPostion(audienceUserList,dataItem);

                        if(speakerPostion>=0){
                            speakersUserList.remove(speakerPostion);
                        }
                        if(audiencePosition>=0){
                            audienceUserList.remove(audiencePosition);
                        }

                        Log.d(Constants.tag,"speakersUserList size onChildRemoved"+speakersUserList.size());
                        Log.d(Constants.tag,"audienceUserList size onChildRemoved"+audienceUserList.size());

                        onRoomUsersUpdate(null);

                    }
                    catch (Exception e)
                    {Log.d(Constants.tag,"onChildRemoved: "+e);}
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d(Constants.tag,"onChildMoved ::");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(Constants.tag,"onCancelled ::");

                }
            };
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).child(Variables.roomUsers).addChildEventListener(roomUserUpdateListener);
        }

    }

    private int getlistPostion(ArrayList<HomeUserModel> currentUserList, HomeUserModel dataItem) {
        for (int i=0;i<currentUserList.size();i++) {

            if (currentUserList.get(i).getUserModel().getId().equals(dataItem.getUserModel().getId())) {
                return i;
            }
        }
        return -1;
    }

    private void getRiseHandCounts() {
        int riseHandCount=0;
        for (HomeUserModel riseHandUser: speakersUserList)
        {
            if (riseHandUser.getRiseHand().equals("1"))
            {
                riseHandCount=riseHandCount+1;
            }
        }
        mainStreamingModel.getModel().setRiseHandCount(""+riseHandCount);


    }



    ValueEventListener currentJoinRoomListener;
    private void registerMyJoinRoomListener() {
        if(currentJoinRoomListener==null) {
            currentJoinRoomListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        HomeUserModel dataItem = snapshot.getValue(HomeUserModel.class);

                        if (dataItem!=null && dataItem.getUserModel()!=null &&
                                dataItem.getUserModel().getId()!=null && myUserModel != null) {
                            if (!(myUserModel.getUserRoleType().equals(dataItem.getUserRoleType()))) {

                                if (dataItem.getUserRoleType().equals("1")) {
                                    roomManager.speakerJoinRoomHitApi(Functions.getSharedPreference(activity).getString(Variables.U_ID,""), mainStreamingModel.getModel().getId(),"1");
                                } else if (dataItem.getUserRoleType().equals("0")) {
                                    roomManager.speakerJoinRoomHitApi(Functions.getSharedPreference(activity).getString(Variables.U_ID,""), mainStreamingModel.getModel().getId(), "0");
                                }
                            }
                        }
                        myUserModel = dataItem;
                        onMyUserUpdate(null);

                    }
                    else {
                       onRoomLeave(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            };
        }

        reference.child(Variables.roomKey)
                .child(mainStreamingModel.getModel().getId())
                .child(Variables.roomUsers).child(Functions.getSharedPreference(this.activity).getString(Variables.U_ID,""))
                .addValueEventListener(currentJoinRoomListener);

    }



    ValueEventListener speakInvitationListener;
    private void registerSpeakInvitationListener() {
        if(speakInvitationListener ==null) {
            speakInvitationListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(Constants.tag,"roomUpdateListener : "+dataSnapshot);
                    if (dataSnapshot.exists())
                    {
                        InviteForSpeakModel invitation=dataSnapshot.getValue(InviteForSpeakModel.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("data",invitation);
                        onSpeakInvitationReceived(bundle);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.child(Variables.roomKey)
                    .child(mainStreamingModel.getModel().getId())
                    .child(Variables.roomInvitation)
                    .child(Functions.getSharedPreference(this.activity).getString(Variables.U_ID,""))
                    .addValueEventListener(speakInvitationListener);

        }
    }






    public void removeAllListener(){


        if (reference!=null && myRoomListener != null) {
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).removeEventListener(myRoomListener);
        }
        if (reference!=null && roomUserUpdateListener != null) {
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).child(Variables.roomUsers).removeEventListener(roomUserUpdateListener);
        }
        if (reference!=null && currentJoinRoomListener != null) {
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).child(Variables.roomUsers).child(Functions.getSharedPreference(activity).getString(Variables.U_ID,"")).removeEventListener(currentJoinRoomListener);
        }
        if (reference!=null && speakInvitationListener != null) {
            reference.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).
                    child(Variables.roomInvitation)
                    .child(Functions.getSharedPreference(this.activity).getString(Variables.U_ID,"")).removeEventListener(speakInvitationListener);
        }


        myRoomListener=null;
        roomUserUpdateListener=null;
        currentJoinRoomListener=null;
        speakInvitationListener=null;


        speakersUserList.clear();
        audienceUserList.clear();


        mainStreamingModel=null;
        myUserModel=null;
    }



    public void removeMainListener(){


        if (reference!=null && myjoinListener != null) {
            reference.child(Variables.joinedKey).child(Functions.getSharedPreference(activity).getString(Variables.U_ID,"")).removeEventListener(myjoinListener);

        }


        myjoinListener=null;

        INSTANCE=null;
    }





    public MainStreamingModel getMainStreamingModel() {
        return mainStreamingModel;
    }

    public void setMainStreamingModel(MainStreamingModel mainStreamingModel) {
        this.mainStreamingModel = mainStreamingModel;
    }

    public ArrayList<HomeUserModel> getSpeakersUserList() {
        return speakersUserList;
    }

    public void setSpeakersUserList(ArrayList<HomeUserModel> speakersUserList) {
        this.speakersUserList = speakersUserList;
    }

    public ArrayList<HomeUserModel> getAudienceUserList() {
        return audienceUserList;
    }

    public void setAudienceUserList(ArrayList<HomeUserModel> audienceUserList) {
        this.audienceUserList = audienceUserList;
    }

    public HomeUserModel getMyUserModel() {
        return myUserModel;
    }

    public void setMyUserModel(HomeUserModel myUserModel) {
        this.myUserModel = myUserModel;
    }















    @Override
    public void createRoom(Bundle bundle) {
        if(listerner1!=null)
            listerner1.createRoom(bundle);

        if(listerner2!=null)
            listerner2.createRoom(bundle);

        if(listerner3!=null)
            listerner3.createRoom(bundle);
    }

    @Override
    public void JoinedRoom(Bundle bundle) {
        if(listerner1!=null)
            listerner1.JoinedRoom(bundle);

        if(listerner2!=null)
            listerner2.JoinedRoom(bundle);

        if(listerner3!=null)
            listerner3.JoinedRoom(bundle);
    }

    @Override
    public void onRoomLeave(Bundle bundle) {
        if(listerner1!=null)
            listerner1.onRoomLeave(bundle);

        if(listerner2!=null)
            listerner2.onRoomLeave(bundle);

        if(listerner3!=null)
            listerner3.onRoomLeave(bundle);
    }

    @Override
    public void onRoomDelete(Bundle bundle) {
        if(listerner1!=null)
            listerner1.onRoomDelete(bundle);

        if(listerner2!=null)
            listerner2.onRoomDelete(bundle);

        if(listerner3!=null)
            listerner3.onRoomDelete(bundle);
    }

    @Override
    public void onRoomUpdate(Bundle bundle) {
        if(listerner1!=null)
            listerner1.onRoomUpdate(bundle);

        if(listerner2!=null)
            listerner2.onRoomUpdate(bundle);

        if(listerner3!=null)
            listerner3.onRoomUpdate(bundle);
    }

    @Override
    public void onRoomUsersUpdate(Bundle bundle) {
        if(listerner1!=null)
            listerner1.onRoomUsersUpdate(bundle);

        if(listerner2!=null)
            listerner2.onRoomUsersUpdate(bundle);

        if(listerner3!=null)
            listerner3.onRoomUsersUpdate(bundle);
    }

    @Override
    public void onMyUserUpdate(Bundle bundle) {
        if(listerner1!=null)
            listerner1.onMyUserUpdate(bundle);

        if(listerner2!=null)
            listerner2.onMyUserUpdate(bundle);

        if(listerner3!=null)
            listerner3.onMyUserUpdate(bundle);
    }

    @Override
    public void onSpeakInvitationReceived(Bundle bundle) {
        if(listerner1!=null)
            listerner1.onSpeakInvitationReceived(bundle);

        if(listerner2!=null)
            listerner2.onSpeakInvitationReceived(bundle);

        if(listerner3!=null)
            listerner3.onSpeakInvitationReceived(bundle);
    }

    @Override
    public void onWaveUserUpdate(Bundle bundle) {
        if(listerner1!=null)
            listerner1.onWaveUserUpdate(bundle);

        if(listerner2!=null)
            listerner2.onWaveUserUpdate(bundle);

        if(listerner3!=null)
            listerner3.onWaveUserUpdate(bundle);
    }


}
