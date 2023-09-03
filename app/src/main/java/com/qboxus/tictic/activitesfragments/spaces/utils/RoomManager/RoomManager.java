package com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.activitesfragments.spaces.models.TopicModel;
import com.qboxus.tictic.activitesfragments.spaces.utils.ApiCalling;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomManager {

    Activity activity;
    RoomApisListener roomApisListener;
    MainStreamingModel model=null;

   public String roomName,privacyType;
   public ArrayList<UserModel> selectedInviteFriends;

    public ArrayList<TopicModel> selectedTopics;


    private static volatile RoomManager INSTANCE = null;

    public static RoomManager getInstance(Activity activity) {
        if(INSTANCE == null) {
            synchronized (RoomManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RoomManager(activity);
                }
            }
        }
        return INSTANCE;
    }


    public RoomManager(Activity activity) {
        this.activity=activity;
    }

    public void addResponseListener(RoomApisListener responseListener) {
        this.roomApisListener=responseListener;
    }


    public void createRoomBYUserId() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID,""));
            parameters.put("title", roomName);
            parameters.put("privacy", privacyType);
            if(selectedTopics!=null && !selectedTopics.isEmpty())
                parameters.put("topic_id",selectedTopics.get(0).getId());

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }

        Functions.showLoader(activity,false,false);
        ApiCalling.createRoomBYUserId(activity, parameters, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

            }
            @Override
            public void onSuccess(String responce) {
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject=new JSONObject(responce);
                    JSONObject msgObj=jsonObject.getJSONObject("msg");

                    JSONObject roomObj=msgObj.getJSONObject("Room");
                    JSONArray roomMemberArray=msgObj.getJSONArray("RoomMember");

                    model=new MainStreamingModel();
                    StreamModel streamModel=new StreamModel();

                    streamModel.setId(roomObj.optString("id"));
                    streamModel.setAdminId(roomObj.optString("user_id"));
                    streamModel.setTitle(roomObj.optString("title"));
                    streamModel.setPrivacyType(roomObj.optString("privacy"));
                    streamModel.setCreated(roomObj.optString("created"));

                    ArrayList<HomeUserModel> userList=new ArrayList<>();
                    for (int j=0;j<roomMemberArray.length();j++)
                    {
                        JSONObject innerObj=roomMemberArray.getJSONObject(j);
                        UserModel userModel= DataParsing.getUserDataModel(innerObj.getJSONObject("User"));

                        HomeUserModel userItemModel=new HomeUserModel();
                        userItemModel.setUserModel(userModel);
                        userItemModel.setMice("1");
                        userItemModel.setOnline("1");
                        userItemModel.setUserRoleType(innerObj.optString("moderator"));
                        userList.add(userItemModel);
                    }
                    model.setUserList(userList);
                    model.setModel(streamModel);

                    if (roomApisListener!=null)
                    {
                        Bundle bundle=new Bundle();
                        bundle.putString("action","roomCreated");
                        bundle.putSerializable("model",model);
                        roomApisListener.roomCreated(bundle);
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
            @Override
            public void onFail(String responce) {
                Functions.cancelLoader();
                Functions.showError(activity,responce);
            }
        });
    }


    public void inviteMembersIntoRoom(String userId,ArrayList<UserModel> selectedInviteFriends){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("sender_id", userId);
            parameters.put("room_id", model.getModel().getId());
            JSONArray friendsArray=new JSONArray();
            for(UserModel user:selectedInviteFriends)
            {
                JSONObject userObj = new JSONObject();
                userObj.put("receiver_id",user.getId());
                friendsArray.put(userObj);
            }
            parameters.put("receivers", friendsArray);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }


        ApiCalling.inviteMembersIntoRoom(activity, parameters, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

            }
            @Override
            public void onSuccess(String responce) {
                if (roomApisListener!=null)
                {
                    Bundle bundle=new Bundle();
                    bundle.putString("action","roomInvitationSended");
                    roomApisListener.roomInvitationsSended(bundle);
                }
            }
            @Override
            public void onFail(String responce) {
                Functions.cancelLoader();
                Functions.showError(activity,responce);
            }
        });
    }



    public void joinRoom(String userid,String roomId,String moderator) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", userid);
            parameters.put("room_id", roomId);
            parameters.put("moderator", moderator);
        }
        catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.joinRoom, parameters, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {

                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        JSONObject roomObj=msgObj.getJSONObject("RoomMember");
                        UserModel userModel=DataParsing.getUserDataModel(msgObj.getJSONObject("User"));
                        HomeUserModel myUserModel=new HomeUserModel();
                        myUserModel.setOnline("1");
                        myUserModel.setUserModel(userModel);
                        myUserModel.setUserRoleType(roomObj.optString("moderator"));

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", myUserModel);
                        bundle.putString("roomId", roomId);
                        roomApisListener.onRoomJoined(bundle);


                    }
                    else {
                        Functions.showError(activity,jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });


    }


    public void leaveRoom(String roomId){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID,""));
            parameters.put("room_id", roomId);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }


        ApiCalling.leaveRoom(activity, parameters, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

            }
            @Override
            public void onSuccess(String responce) {
                if (roomApisListener!=null) {
                    Bundle bundle=new Bundle();
                    bundle.putString("roomId",roomId);
                    roomApisListener.onRoomLeave(bundle);
                }
            }
            @Override
            public void onFail(String responce) {
                Functions.cancelLoader();
                Functions.showError(activity,responce);
            }
        });
    }

    public void deleteRoom(String roomId){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID,""));
            parameters.put("id", roomId);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }


        ApiCalling.deleteRoom(activity, parameters, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

            }
            @Override
            public void onSuccess(String responce) {
                if (roomApisListener!=null) {
                    Bundle bundle=new Bundle();
                    bundle.putString("roomId",roomId);
                    roomApisListener.onRoomDelete(bundle);
                }
            }
            @Override
            public void onFail(String responce) {
                Functions.cancelLoader();
                Functions.showError(activity,responce);
            }
        });
    }


    public void showRoomDetailAfterJoin(String roomId) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID,""));
            parameters.put("room_id", roomId);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }
        ApiCalling.showRoomDetail(activity, parameters, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

            }
            @Override
            public void onSuccess(String responce) {
                try {
                    JSONObject jsonObject=new JSONObject(responce);
                    JSONObject msgObj=jsonObject.getJSONObject("msg");

                    JSONObject roomObj=msgObj.getJSONObject("Room");
                    JSONArray roomMemberArray=msgObj.getJSONArray("RoomMember");

                    model=new MainStreamingModel();
                    StreamModel streamModel=new StreamModel();

                    streamModel.setId(roomObj.optString("id"));
                    streamModel.setAdminId(roomObj.optString("user_id"));
                    streamModel.setTitle(roomObj.optString("title"));
                    streamModel.setPrivacyType(roomObj.optString("privacy"));
                    streamModel.setCreated(roomObj.optString("created"));

                    ArrayList<HomeUserModel> userList=new ArrayList<>();
                    for (int j=0;j<roomMemberArray.length();j++)
                    {
                        JSONObject innerObj=roomMemberArray.getJSONObject(j);
                        UserModel userModel= DataParsing.getUserDataModel(innerObj.getJSONObject("User"));

                        HomeUserModel userItemModel=new HomeUserModel();
                        userItemModel.setUserModel(userModel);
                        userItemModel.setUserRoleType(innerObj.optString("moderator"));
                        userList.add(userItemModel);
                    }
                    model.setUserList(userList);
                    model.setModel(streamModel);

                    if (roomApisListener!=null)
                    {
                        Bundle bundle=new Bundle();
                        bundle.putString("action","showRoomDetailAfterJoin");
                        bundle.putSerializable("model",model);
                        roomApisListener.showRoomDetailAfterJoin(bundle);
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
            @Override
            public void onFail(String responce) {
                Functions.showError(activity,responce);
            }
        });
    }



    //need this function before create room or join room
    ArrayList<RoomJoinStatusModel> roomJoinStatusList=new ArrayList<>();
    public void checkMyRoomJoinStatus(String action,String roomId) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID,""));
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }

        ApiCalling.checkMyRoomJoinStatus(activity, parameters, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

            }
            @Override
            public void onSuccess(String responce) {
                try {
                    JSONObject resObj=new JSONObject(responce);
                    JSONArray msgArray=resObj.getJSONArray("msg");

                    roomJoinStatusList.clear();
                    for (int m=0;m<msgArray.length();m++)
                    {
                        JSONObject mainObj=msgArray.getJSONObject(m);
                        RoomJoinStatusModel joinStatusModel=new RoomJoinStatusModel();

                        JSONObject roomObj=mainObj.getJSONObject("Room");
                        JSONArray moderatorsArray=roomObj.getJSONArray("Moderators");
                        ArrayList<HomeUserModel> moderatorList=new ArrayList<>();
                        for (int i=0;i<moderatorsArray.length();i++)
                        {
                            JSONObject innerObj=moderatorsArray.getJSONObject(i);
                            UserModel user=DataParsing.getUserDataModel(innerObj.getJSONObject("User"));

                            HomeUserModel userModel=new HomeUserModel();
                            userModel.setOnline("1");
                            userModel.setUserModel(user);
                            userModel.setUserRoleType(innerObj.getJSONObject("RoomMember").optString("moderator"));
                            userModel.setMice("");
                            userModel.setRiseHand("");

                            moderatorList.add(userModel);
                        }

                        HomeUserModel myModel=new HomeUserModel();
                        myModel.setUserModel(DataParsing.getUserDataModel(mainObj.getJSONObject("User")));
                        myModel.setUserRoleType(mainObj.getJSONObject("RoomMember").optString("moderator"));
                        myModel.setMice("");
                        myModel.setRiseHand("");
                        myModel.setOnline("1");

                        joinStatusModel.setMyModel(myModel);
                        joinStatusModel.setRoomId(roomObj.optString("id"));
                        joinStatusModel.setUserList(moderatorList);
                        roomJoinStatusList.add(joinStatusModel);
                    }

                    if (action.equalsIgnoreCase("join")) {
                        performActionAgainstRoomJoin(roomId);
                    }
                    else if(action.equalsIgnoreCase("create")) {
                        performActionAgainstRoomGenrate();
                    }


                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
            @Override
            public void onFail(String responce) {

                if (action.equalsIgnoreCase("join")) {
                    joinRoomResponce(roomId);
                }
                else if(action.equalsIgnoreCase("create"))
                {
                    genrateRoomResponce();
                }


            }
        });
    }


    private void performActionAgainstRoomJoin(String roomId) {
        RoomJoinStatusModel matchedRoom=null;
        if (roomJoinStatusList.size()>0)
        {
            for (int i=0;i<roomJoinStatusList.size();i++)
            {
                RoomJoinStatusModel model=roomJoinStatusList.get(i);

                if (model.getRoomId().equals(""+roomId))
                {
                    matchedRoom=model;
                }
                else
                {
                    String myRole=model.getMyModel().getUserRoleType();
                    if (myRole.equals("1"))
                    {
                        if (model.getUserList().size()>1)
                        {
                            leaveRoomResponce(model.getRoomId());
                            roomJoinStatusList.remove(i);
                        }
                        else
                        {
                            deleteRoomResponce(model.getRoomId());
                            roomJoinStatusList.remove(i);
                        }
                    }
                    else
                    if (myRole.equals("2"))
                    {
                        leaveRoomResponce(model.getRoomId());
                        roomJoinStatusList.remove(i);
                    }
                    else
                    {
                        leaveRoomResponce(model.getRoomId());
                        roomJoinStatusList.remove(i);
                    }
                }

            }

            roomJoinStatusList.clear();
            if (matchedRoom!=null && matchedRoom.getRoomId()!=null && matchedRoom.getRoomId().equals(""+roomId))
            {
//                re-join room
                Bundle bundle=new Bundle();
                bundle.putSerializable("model",matchedRoom.myModel);
                bundle.putString("roomId",roomId);
                roomApisListener.onRoomReJoin(bundle);

            }
            else
            {
//                join new room
                joinRoomResponce(roomId);
            }
        }
        else
        {
            joinRoomResponce(roomId);
        }
    }

    private void performActionAgainstRoomGenrate() {
        if (roomJoinStatusList.size()>0)
        {
            for (int i=0;i<roomJoinStatusList.size();i++)
            {
                RoomJoinStatusModel model=roomJoinStatusList.get(i);
                String myRole=model.getMyModel().getUserRoleType();
                if (myRole.equals("1"))
                {
                    if (model.getUserList().size()>1)
                    {
                       leaveRoomResponce(model.getRoomId());
                        roomJoinStatusList.remove(i);
                    }
                    else
                    {
                        deleteRoomResponce(model.getRoomId());
                        roomJoinStatusList.remove(i);
                    }
                }
                else
                if (myRole.equals("2"))
                {
                    leaveRoomResponce(model.getRoomId());
                    roomJoinStatusList.remove(i);
                }
                else
                {
                    leaveRoomResponce(model.getRoomId());
                    roomJoinStatusList.remove(i);
                }
            }

            if (roomJoinStatusList.isEmpty())
            {
                genrateRoomResponce();
            }
            else
            {
                performActionAgainstRoomGenrate();
            }

        }
        else
        {
            genrateRoomResponce();
        }
    }

    private void leaveRoomResponce(String roomId) {
        Bundle bundle=new Bundle();
        bundle.putString("action","leaveRoom");
        bundle.putString("roomId",roomId);
        roomApisListener.doRoomLeave(bundle);
    }

    private void deleteRoomResponce(String roomId) {
        Bundle bundle=new Bundle();
        bundle.putString("action","deleteRoom");
        bundle.putString("roomId",roomId);
        roomApisListener.doRoomDelete(bundle);
    }

    private void genrateRoomResponce() {
        Bundle bundle=new Bundle();
        bundle.putString("action","goAheadForRoomGenrate");
        bundle.putString("resp","remove all and create new");
        roomApisListener.goAheadForRoomGenrate(bundle);
    }

    private void joinRoomResponce(String roomId) {
        Bundle bundle=new Bundle();
        bundle.putString("action","goAheadForJoinRoom");
        bundle.putString("roomId",roomId);
        roomApisListener.goAheadForRoomJoin(bundle);
    }

    public void speakerJoinRoomHitApi(String userId,String roomID,String userType)
    {
        JSONObject parameters = new JSONObject();
        try {

            parameters.put("user_id", userId);
            parameters.put("room_id",roomID);
            parameters.put("moderator", userType);

        }
        catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }


        VolleyRequest.JsonPostRequest(activity, ApiLinks.assignModerator, parameters, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    if (userType.equals("1"))
                    {
                        Functions.showSuccess(activity,activity.getString(R.string.you_are_now_moderator_you_can_now_invite_other_speakers));
                    }
                    else
                    if (userType.equals("0"))
                    {
                        Functions.showSuccess(activity,activity.getString(R.string.you_have_been_move_back_into_the_audience));
                    }
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        JSONObject roomObj=msgObj.getJSONObject("RoomMember");
                        UserModel userModel= DataParsing.getUserDataModel(msgObj.getJSONObject("User"));
                        HomeUserModel myUserModel=new HomeUserModel();
                        myUserModel.setUserModel(userModel);
                        myUserModel.setMice("1");
                        myUserModel.setOnline("1");
                        myUserModel.setUserRoleType(roomObj.optString("moderator"));

                        if (roomApisListener!=null)
                        {
                            Bundle bundle=new Bundle();
                            bundle.putString("action","updateRoomMember");
                            bundle.putSerializable("model",myUserModel);
                            roomApisListener.onRoomMemberUpdate(bundle);
                        }

                    } else {
                        Functions.showError(activity,jsonObject.optString("msg"));
                    }

                }
                catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });



    }

    public Bundle checkRoomCanDeleteOrLeave(ArrayList<HomeUserModel> speakersUserList) {

        HomeUserModel speakerAsModeratorModel=null;
        HomeUserModel myModel=null;
        int countModerator=0;
        int countSpeaker=0;
        Bundle bundle=new Bundle();

        for (HomeUserModel moderatorModel: speakersUserList) {
            if(moderatorModel.getUserModel().getId().equals(Functions.getSharedPreference(activity).getString(Variables.U_ID,""))){
                myModel=moderatorModel;
            }

            if (moderatorModel.getUserRoleType().equals("1"))
            {
                countModerator=countModerator+1;
            }
            else if (moderatorModel.getUserRoleType().equals("2"))
            {
                countSpeaker=countSpeaker+1;

                if (countSpeaker==1)
                {
                    speakerAsModeratorModel=moderatorModel;
                }
            }


        }

        if(myModel==null){
            bundle.putString("action","leaveRoom");
            return bundle;
        }

        else if (countModerator<2) {
            if (countSpeaker<1)
            {

                bundle.putString("action","removeRoom");
                return bundle;
            }
            else if (speakerAsModeratorModel!=null) {
                bundle.putString("action","leaveRoomAndAssign");
                bundle.putSerializable("model",speakerAsModeratorModel);
                return bundle;
            }
            else {
                bundle.putString("action", "leaveRoom");
                return bundle;
            }
        }
        else {
            bundle.putString("action","leaveRoom");
            return bundle;
        }
    }




}
