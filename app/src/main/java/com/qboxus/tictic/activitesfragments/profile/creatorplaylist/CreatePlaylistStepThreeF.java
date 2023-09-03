package com.qboxus.tictic.activitesfragments.profile.creatorplaylist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.adapters.VideoPlaylistReorderAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.CreatePlaylistModel;
import com.qboxus.tictic.models.HomeSelectionModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class CreatePlaylistStepThreeF extends Fragment {

    CreatePlaylistModel playlistModel;
    RecyclerView recyclerView;
    VideoPlaylistReorderAdapter adapter;
    ArrayList<HomeSelectionModel> dataList=new ArrayList<>();
    Context context;
    View view;
    Button btnCreatePlaylist;
    FragmentCallBack callBack;
    boolean isFromCreate;

    public CreatePlaylistStepThreeF(boolean isFromCreate,FragmentCallBack callBack) {
        this.callBack = callBack;
        this.isFromCreate=isFromCreate;
    }

    public CreatePlaylistStepThreeF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_create_playlist_step_three, container, false);
        initContol();
        return view;
    }

    private void initContol() {
        context=view.getContext();
        playlistModel= (CreatePlaylistModel) getArguments().getSerializable("model");
        btnCreatePlaylist=view.findViewById(R.id.btnCreatePlaylist);
        if (isFromCreate)
        {
            btnCreatePlaylist.setText(view.getContext().getString(R.string.create_playlist));
        }
        else
        {
            btnCreatePlaylist.setText(view.getContext().getString(R.string.update_playlist));
        }

        setupRecyclerview();
        view.findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    hitAddAndUpdatePlaylistApi();
                }
            }
        });

    }

    private void hitAddAndUpdatePlaylistApi() {
        JSONObject parameters = new JSONObject();
        try {
            JSONArray playlistArray=new JSONArray();
            for (int i=0;i<dataList.size();i++)
            {
                JSONObject videoObj=new JSONObject();
                HomeSelectionModel item=dataList.get(i);
                videoObj.put("video_id",item.getModel().video_id);
                videoObj.put("order",i+1);

                playlistArray.put(videoObj);
            }
            if (!(isFromCreate))
            {
                parameters.put("id", getArguments().getString("playlist_id"));
            }
            parameters.put("videos", playlistArray);
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
            parameters.put("name", "" + playlistModel.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.addPlaylist, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    if (jsonObject.optString("code").equals("200"))
                    {
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("isShow",false);
                        callBack.onResponce(bundle);
                        getActivity().onBackPressed();
                    }

                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });
    }


    private void setupRecyclerview() {
        recyclerView=view.findViewById(R.id.recylerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ItemTouchHelper itemDecor = new ItemTouchHelper((ItemTouchHelper.Callback)(new ItemTouchHelper.SimpleCallback(3, 0) {
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        }));
        itemDecor.attachToRecyclerView(recyclerView);
        adapter=new VideoPlaylistReorderAdapter(context, dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                HomeSelectionModel itemUpdate=dataList.get(pos);
            }
        });
        recyclerView.setAdapter(adapter);


        getListData();
    }

    private void getListData() {
        dataList.clear();
        for (String key:playlistModel.getItemCountList().keySet())
        {
            HomeSelectionModel itemModel=playlistModel.getItemCountList().get(key);
            dataList.add(itemModel);
        }
        adapter.notifyDataSetChanged();
    }
}