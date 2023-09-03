package com.qboxus.tictic.activitesfragments.videorecording;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.livestreaming.activities.ConcertSelectionA;
import com.qboxus.tictic.activitesfragments.livestreaming.activities.StreamingMainA;
import com.qboxus.tictic.activitesfragments.spaces.services.RoomStreamService;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.services.UploadService;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;


import org.json.JSONObject;


public class CreateContentF extends BottomSheetDialogFragment implements View.OnClickListener{


    View view;
    Context context;
    private BottomSheetBehavior mBehavior;

    public CreateContentF() {
        //Required Empty
    }

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_video_creation, null);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return  dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_video_creation, container, false);
        context=getContext();




        view.findViewById(R.id.goBack).setOnClickListener(this);
        view.findViewById(R.id.post_video_layout).setOnClickListener(this);
        view.findViewById(R.id.live_layout).setOnClickListener(this);




        return view;
    }




    String streamingId="";
    private void getLiveStreamingId() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(view.getContext()).getString(Variables.U_ID,"0"));
            parameters.put("started_at", Functions.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.liveStream,parameters, Functions.getHeaders(context),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        JSONObject streamingObj=msgObj.getJSONObject("LiveStreaming");
                        streamingId=streamingObj.optString("id");
                        goLive();
                    }
                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });

    }

    private void goLive() {
        dismiss();

        Intent intent;
        String streamType="";
        if (streamType.equals("Normal"))
        {
            intent = new Intent(getActivity(), StreamingMainA.class);
        }
        else
        {
            intent = new Intent(getActivity(), ConcertSelectionA.class);
        }

        intent.putExtra("userId", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
        intent.putExtra("userName", Functions.getSharedPreference(context).getString(Variables.U_NAME, ""));
        intent.putExtra("userPicture", Functions.getSharedPreference(context).getString(Variables.U_PIC, ""));
        intent.putExtra("userRole", io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        intent.putExtra("streamingId",streamingId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.live_layout:
            {
                if (Functions.isMyServiceRunning(context, new RoomStreamService().getClass())) {
                Functions.showAlert(getActivity(),context.getString(R.string.app_name),context.getString(R.string.creating_streaming_check));
                }else {
                    getLiveStreamingId();
                }
            }
                break;
            case R.id.goBack:
                dismiss();
                break;

            case R.id.post_video_layout:
            {
                boolean isOpenGLSupported = Functions.isOpenGLVersionSupported(context, 0x00030001);
                if (isOpenGLSupported) {
                    openVideoCamera();
                } else {
                    Toast.makeText(context, view.getContext().getString(R.string.your_device_opengl_verison_is_not_compatible_to_use_this_feature), Toast.LENGTH_SHORT).show();
                }
            }

                break;

        }
    }



    public void openVideoCamera(){


        if (Functions.isMyServiceRunning(context, new UploadService().getClass())) {
            Toast.makeText(context, context.getString(R.string.video_already_in_progress), Toast.LENGTH_SHORT).show();

            Functions.showAlert(getActivity(),context.getString(R.string.app_name),context.getString(R.string.video_already_in_progress));

        }
        else if (Functions.isMyServiceRunning(context, new RoomStreamService().getClass())) {
            Functions.showAlert(getActivity(),context.getString(R.string.app_name),context.getString(R.string.creating_post_check));
        }
        else {
            dismiss();
            Intent intent = new Intent(context, VideoRecoderA.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
        }

    }



}