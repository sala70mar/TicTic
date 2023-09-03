package com.qboxus.tictic.activitesfragments.profile.creatorplaylist;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.adapters.ShowPlaylistAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.PlaylistHomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONObject;

import java.util.ArrayList;


public class ShowPlaylistF extends BottomSheetDialogFragment implements View.OnClickListener{


    View view;
    Context context;
    ArrayList<HomeModel> dataList;
    ShowPlaylistAdapter adapter;
    String videoId,platlistId,userId;
    String playlistName;
    RecyclerView recylerview;
    TextView tvPlaylist;
    ImageView ivOption;
    FragmentCallBack callback;


    public ShowPlaylistF(ArrayList<HomeModel> dataList,String videoId,String platlistId,String userId,String playlistName,FragmentCallBack callback) {
        this.dataList=dataList;
        this.videoId=videoId;
        this.platlistId=platlistId;
        this.userId=userId;
        this.playlistName=playlistName;
        this.callback=callback;
    }

    public ShowPlaylistF() {
        //Required Empty
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_show_playlist, container, false);
        context=view.getContext();
        ivOption=view.findViewById(R.id.ivOption);
        ivOption.setOnClickListener(this);
        view.findViewById(R.id.ivBack).setOnClickListener(this);
        view.findViewById(R.id.ivOption).setOnClickListener(this);
        tvPlaylist=view.findViewById(R.id.tvPlaylist);
        setupScreenData();
        setupAdapter();
        return view;
    }

    private void setupScreenData() {
        tvPlaylist.setText(playlistName);
        if (userId.equalsIgnoreCase(Functions.getSharedPreference(view.getContext()).getString(Variables.U_ID,"")))
        {
            ivOption.setVisibility(View.VISIBLE);
        }
        else
        {
            ivOption.setVisibility(View.GONE);
        }
    }

    private void setupAdapter() {
        ArrayList<PlaylistHomeModel> playlist=new ArrayList<>();
        recylerview=view.findViewById(R.id.recylerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recylerview.setLayoutManager(layoutManager);
        for (HomeModel itemModel:dataList)
        {
            PlaylistHomeModel item=new PlaylistHomeModel();
            item.setModel(itemModel);
            if (itemModel.video_id.equals(videoId))
            {
                item.setSelection(true);
            }
            else
            {
                item.setSelection(false);
            }

            playlist.add(item);
        }
        adapter=new ShowPlaylistAdapter(playlist,userId, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                PlaylistHomeModel itemUpdate=playlist.get(pos);

                if (view.getId()==R.id.ivOption)
                {
                    showDeleteVideo(view,itemUpdate,pos);
                }
                else
                {
                    if (!(itemUpdate.isSelection()))
                    {
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("isShow",true);
                        bundle.putString("type","videoPlay");
                        bundle.putInt("position",pos);
                        callback.onResponce(bundle);
                        dismiss();
                    }
                }

            }
        });
        recylerview.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ivBack:
            {
                dismiss();
            }
            break;

            case R.id.ivOption:
            {
                showSetting();
            }
            break;


        }
    }

    private void showDeleteVideo(View view, PlaylistHomeModel itemUpdate, int pos) {
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
                        deletePlaylistVideo(itemUpdate,pos);
                    }
                    break;
                }
                return true;
            }
        });

    }

    private void showSetting() {
        Context wrapper = new ContextThemeWrapper(context, R.style.AlertDialogCustom);
        PopupMenu popup = new PopupMenu(wrapper, ivOption);

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
                        deletePlaylist();
                    }
                    break;
                }
                return true;
            }
        });

    }

    private void deletePlaylist() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", platlistId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.deletePlaylist, parameters,Functions.getHeaders(getActivity()), new Callback() {
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
                        bundle.putString("type","deletePlaylist");
                        callback.onResponce(bundle);
                        dismiss();
                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception: "+e);
                }

            }
        });

    }


    private void deletePlaylistVideo(PlaylistHomeModel itemUpdate, int pos) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", itemUpdate.getModel().playlistVideoId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.deletePlaylistVideo, parameters,Functions.getHeaders(getActivity()), new Callback() {
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
                        bundle.putString("type","deletePlaylistVideo");
                        bundle.putInt("position",pos);
                        callback.onResponce(bundle);
                        dismiss();

                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception: "+e);
                }

            }
        });

    }

}