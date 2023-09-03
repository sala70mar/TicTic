package com.qboxus.tictic.activitesfragments.spaces.utils;

import android.app.Activity;
import android.util.Log;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.simpleclasses.Functions;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;

import org.json.JSONObject;


public class ApiCalling {




    public static void createRoomBYUserId(Activity activity,JSONObject params,APICallBack apiCallBack) {


        VolleyRequest.JsonPostRequest(activity, ApiLinks.addRoom, params, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");

                    if (code.equals("200")) {
                        apiCallBack.onSuccess(resp);
                    } else {
                        apiCallBack.onFail(jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });


    }


    public static void inviteMembersIntoRoom(Activity activity,JSONObject params,APICallBack apiCallBack) {



        VolleyRequest.JsonPostRequest(activity, ApiLinks.inviteUserToRoom, params, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");

                    if (code.equals("200")) {
                        apiCallBack.onSuccess(resp);
                    } else {
                        apiCallBack.onFail(jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });


    }


    public static void leaveRoom(Activity activity,JSONObject params,APICallBack apiCallBack) {



        VolleyRequest.JsonPostRequest(activity, ApiLinks.leaveRoom, params, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");

                    if (code.equals("200")) {
                        apiCallBack.onSuccess(resp);
                    } else {
                        apiCallBack.onFail(jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });


    }

    public static void deleteRoom(Activity activity,JSONObject params,APICallBack apiCallBack) {



        VolleyRequest.JsonPostRequest(activity, ApiLinks.deleteRoom, params, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");

                    if (code.equals("200")) {
                        apiCallBack.onSuccess(resp);
                    } else {
                        apiCallBack.onFail(jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });



    }


    public static void checkMyRoomJoinStatus(Activity activity,JSONObject params,APICallBack apiCallBack) {



        VolleyRequest.JsonPostRequest(activity, ApiLinks.showUserJoinedRooms, params, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");

                    if (code.equals("200")) {
                        apiCallBack.onSuccess(resp);
                    } else {
                        apiCallBack.onFail(jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });


    }


    public static void showRoomDetail(Activity activity,JSONObject params,APICallBack apiCallBack) {


        VolleyRequest.JsonPostRequest(activity, ApiLinks.showRoomDetail, params, Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");

                    if (code.equals("200")) {
                        apiCallBack.onSuccess(resp);
                    } else {
                        apiCallBack.onFail(jsonObject.optString("msg"));
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });

    }


}
