package com.qboxus.tictic.activitesfragments.livestreaming.activities;

import static com.qboxus.tictic.activitesfragments.livestreaming.Constants.KEY_CLIENT_ROLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.activitesfragments.livestreaming.adapter.LiveUserAdapter;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.activitesfragments.spaces.services.RoomStreamService;
import com.qboxus.tictic.activitesfragments.walletandwithdraw.MyWallet;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.PermissionUtils;
import com.qboxus.tictic.simpleclasses.TicTic;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveUsersA extends AppCompatLocaleActivity implements View.OnClickListener {

    Context context;
    ArrayList<LiveUserModel> dataList = new ArrayList<>();
    RecyclerView recyclerView;
    LiveUserAdapter adapter;
    ImageView btnBack;
    DatabaseReference rootref;

    PermissionUtils takePermissionUtils;
    LiveUserModel selectLiveModel;
    int position;

    public static HashMap<String,String> unlockStream=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_live_users);
        context = LiveUsersA.this;
        rootref = FirebaseDatabase.getInstance().getReference();
        takePermissionUtils=new PermissionUtils(LiveUsersA.this,mPermissionResult);
        btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        recyclerView.setHasFixedSize(true);

        adapter = new LiveUserAdapter(context, dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                if (!(dataList.isEmpty()))
                {
                    position=pos;
                    LiveUserModel itemUpdate =dataList.get(pos);
                    selectLiveModel=itemUpdate;

                    if(Functions.checkLoginUser(LiveUsersA.this)) {

                        if (takePermissionUtils.isCameraRecordingPermissionGranted())
                        {
                            goLive();
                        }
                        else
                        {
                            takePermissionUtils.showCameraRecordingPermissionDailog(getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                        }

                    }
                }

            }
        });

        recyclerView.setAdapter(adapter);


        getData();
    }

    private void goLive() {

        if (Functions.isMyServiceRunning(context, new RoomStreamService().getClass())) {
            Functions.showAlert(LiveUsersA.this,context.getString(R.string.app_name),context.getString(R.string.watch_streaming_check));
        }else {

            if (Functions.checkLoginUser(LiveUsersA.this)) {
                if (selectLiveModel.getSecureCode().length() > 0) {
                    Intent intent = new Intent(LiveUsersA.this, LiveUserAuthenticationA.class);
                    intent.putExtra("userModel", selectLiveModel);
                    intent.putExtra("dataList", dataList);
                    intent.putExtra("position", position);
                    startActivity(intent);
                } else {
                    if (selectLiveModel.getOnlineType().equals("oneTwoOne")) {
                        if (selectLiveModel.getJoinStreamPrice().equalsIgnoreCase("0")) {
                            joinSingleStream();
                        } else {
                            rootref.child("LiveStreamingUsers").child(selectLiveModel.getStreamingId())
                                    .child("FeePaid").child(Functions.getSharedPreference(context).getString(Variables.U_ID, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            LiveUsersA.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (snapshot.exists()) {
                                                        joinSingleStream();
                                                    } else {
                                                        showPriceOffJoin(true);
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            LiveUsersA.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showPriceOffJoin(true);
                                                }
                                            });
                                        }
                                    });

                        }
                    } else {
                        if (selectLiveModel.getJoinStreamPrice().equalsIgnoreCase("0")) {
                            joinMulticastStream();
                        } else {
                            rootref.child("LiveStreamingUsers").child(selectLiveModel.getStreamingId())
                                    .child("FeePaid").child(Functions.getSharedPreference(context).getString(Variables.U_ID, "")).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            LiveUsersA.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (snapshot.exists()) {
                                                        joinMulticastStream();
                                                    } else {
                                                        showPriceOffJoin(false);
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            LiveUsersA.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showPriceOffJoin(false);
                                                }
                                            });
                                        }
                                    });
                        }

                    }
                }
            }
        }
    }

    private void showPriceOffJoin(boolean isSingle) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.price_to_join_stream_view);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

        RelativeLayout tabAccept = alertDialog.findViewById(R.id.tabAccept);
        ImageView closeBtn = alertDialog.findViewById(R.id.closeBtn);
        TextView tvJoiningAmount=alertDialog.findViewById(R.id.tvJoiningAmount);

        tvJoiningAmount.setText(""+selectLiveModel.getJoinStreamPrice()+" "+context.getString(R.string.coins_are_deducted_from_your_wallet_to_join_the_stream));


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                deductPriceFromWallet(isSingle);
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void deductPriceFromWallet(boolean isSingle) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID,"0"));
            parameters.put("live_streaming_id", selectLiveModel.getStreamingId());
            parameters.put("coin", selectLiveModel.getJoinStreamPrice());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(LiveUsersA.this,false,false);
        VolleyRequest.JsonPostRequest(LiveUsersA.this, ApiLinks.watchLiveStream,parameters, Functions.getHeaders(context),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(LiveUsersA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        UserModel userDetailModel= DataParsing.getUserDataModel(msgObj.getJSONObject("User"));
                        Functions.getSharedPreference(context).edit().putString(Variables.U_WALLET, ""+userDetailModel.getWallet()).commit();

                        String userId=Functions.getSharedPreference(context).getString(Variables.U_ID,"");
                        HashMap<String,String> map=new HashMap<>();
                        map.put("userId",userId);
                        rootref.child("LiveStreamingUsers").child(selectLiveModel.getStreamingId())
                                .child("FeePaid").child(userId)
                                .setValue(map);
                        if (isSingle)
                        {
                            joinSingleStream();
                        }
                        else
                        {
                            joinMulticastStream();
                        }

                    }
                    else
                    {
                        startActivity(new Intent(context, MyWallet.class));
                    }
                } catch (Exception e) {
                    android.util.Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });


    }

    private void joinMulticastStream() {
        final Intent intent = new Intent();
        intent.putExtra("user_id", selectLiveModel.getUserId());
        intent.putExtra("user_name", selectLiveModel.getUserName());
        intent.putExtra("user_picture", selectLiveModel.getUserPicture());
        intent.putExtra("user_role", io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        intent.putExtra("onlineType", "multicast");
        intent.putExtra("description", selectLiveModel.getDescription());
        intent.putExtra("secureCode", "");
        intent.putExtra("dataList",dataList);
        intent.putExtra("position",position);
        intent.setClass(LiveUsersA.this, MultiViewLiveA.class);
        startActivity(intent);
    }


    private void joinSingleStream() {
        final Intent intent = new Intent(LiveUsersA.this, SingleCastJoinA.class);
        intent.putExtra("bookingId",selectLiveModel.getStreamingId());
        intent.putExtra("dataModel",selectLiveModel);
        intent.putExtra(KEY_CLIENT_ROLE, io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
        TicTic ticTic = (TicTic)LiveUsersA.this.getApplication();
        ticTic.engineConfig().setChannelName(selectLiveModel.getStreamingId());
        startActivity(intent);
    }


    // get the list of all live user from the firebase
    ChildEventListener valueEventListener;
    public void getData() {

        valueEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    LiveUserModel model = dataSnapshot.getValue(LiveUserModel.class);
                    if (model.getUserId()!=null && !(TextUtils.isEmpty(model.getUserId())) && !(model.getUserId().equals("null")))
                    {

                        if (TicTic.allOnlineUser.containsKey(""+model.getUserId()))
                        {
                            if(model.getOnlineType().equalsIgnoreCase("multicast"))
                            {
                                dataList.add(model);
                                adapter.notifyDataSetChanged();
                            }
                            findViewById(R.id.no_data_found).setVisibility(View.GONE);
                        }
                        else
                        {
                            Log.d(com.qboxus.tictic.Constants.tag,"Removing Key Id: "+dataSnapshot.getKey());
                            removeStreamingHead(""+dataSnapshot.getKey());
                        }
                    }
                    else
                    {
                        Log.d(com.qboxus.tictic.Constants.tag,"Removing Key: "+dataSnapshot.getKey());
                        removeStreamingHead(""+dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    LiveUserModel model = dataSnapshot.getValue(LiveUserModel.class);
                    if (model.getUserId()!=null && !(TextUtils.isEmpty(model.getUserId())) && !(TextUtils.isEmpty(model.getUserId())) && !(model.getUserId().equals("null")))
                    {
                        for (int i = 0; i < dataList.size(); i++) {
                            if (model.getUserId().equals(dataList.get(i).getUserId())) {
                                dataList.remove(i);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (dataList.isEmpty()) {
                            findViewById(R.id.no_data_found).setVisibility(View.VISIBLE);
                        }
                    }

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        rootref.child("LiveStreamingUsers").addChildEventListener(valueEventListener);
    }


    private void removeStreamingHead(String key) {
        if (key==null || key.isEmpty())
        {
            return;
        }
        rootref.child("LiveUsers").child(key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.d(com.qboxus.tictic.Constants.tag,"Remove: "+error);
            }
        });
    }


    @Override
    public void onDestroy() {
        mPermissionResult.unregister();
        if (rootref != null && valueEventListener != null)
            rootref.child("LiveStreamingUsers").removeEventListener(valueEventListener);
        super.onDestroy();

    }





    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                LiveUsersA.super.onBackPressed();
                break;
            default:
            {}
        }
    }




    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(LiveUsersA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(LiveUsersA.this,getString(R.string.we_need_camera_and_recording_permission_for_live_streaming));
                    }
                    else
                    if (allPermissionClear)
                    {
                        goLive();
                    }

                }
            });


}
