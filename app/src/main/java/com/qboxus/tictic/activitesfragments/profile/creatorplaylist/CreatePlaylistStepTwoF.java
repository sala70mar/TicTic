package com.qboxus.tictic.activitesfragments.profile.creatorplaylist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.adapters.VideosPlaylistSelectionAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.CreatePlaylistModel;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.HomeSelectionModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class CreatePlaylistStepTwoF extends Fragment {

    CreatePlaylistModel playlistModel;
    Context context;
    Button btnNext;
    TextView tvTitle;
    int pageCount = 0;
    boolean ispostFinsh;
    ProgressBar loadMoreProgress;
    GridLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    ArrayList<HomeSelectionModel> dataList=new ArrayList<>();
    VideosPlaylistSelectionAdapter adapter;
    HashMap<String,HomeSelectionModel> itemCountList=new HashMap<>();
    View view;
    boolean isFromCreate;
    HashMap<String, HomeModel> playlistMapList=new HashMap<>();

    FragmentCallBack callBack;
    public CreatePlaylistStepTwoF(boolean isFromCreate,FragmentCallBack callBack) {
        this.callBack = callBack;
        this.isFromCreate=isFromCreate;
    }

    public CreatePlaylistStepTwoF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_create_playlist_step_two, container, false);
        initContol();
        return view;
    }

    private void initContol() {
        context=view.getContext();
        tvTitle=view.findViewById(R.id.tvTitle);
        playlistModel= (CreatePlaylistModel) getArguments().getSerializable("model");
        if (!isFromCreate)
        {
            playlistMapList= (HashMap<String, HomeModel>) getArguments().getSerializable("playlistMapList");
            tvTitle.setText(context.getString(R.string.edit_to_playlist));
        }
        else
        {
            tvTitle.setText(context.getString(R.string.add_to_playlist));
        }

        setupRecyclerView();
        view.findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnNext=view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCountList.keySet().size()<11)
                {
                    playlistModel.setItemCountList(itemCountList);
                    CreatePlaylistStepThreeF f = new CreatePlaylistStepThreeF(isFromCreate,new FragmentCallBack() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if (!(bundle.getBoolean("isShow")))
                            {
                                callBack.onResponce(bundle);
                                getActivity().onBackPressed();
                            }
                        }
                    });
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("model",playlistModel);
                    if (!(isFromCreate))
                    {
                        bundle.putString("playlist_id",getArguments().getString("playlist_id"));
                    }
                    f.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    ft.replace(R.id.stepTwoPlaylistContainerId, f,"CreatePlaylistStepThreeF").addToBackStack("CreatePlaylistStepThreeF").commit();
                }
                else
                {
                    Toast.makeText(context, context.getString(R.string.playlist_can_have_ten_video_only), Toast.LENGTH_SHORT).show();
                }

            }
        });

        pageCount = 0;
        callApiMyvideos();
    }

    private void setupRecyclerView() {
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        recyclerView = view.findViewById(R.id.recylerview);
        linearLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        adapter = new VideosPlaylistSelectionAdapter(context, dataList, (view, pos, object) -> {
            HomeSelectionModel itemUpdate = dataList.get(pos);
            if (itemCountList.containsKey(itemUpdate.getModel().video_id))
            {
                itemUpdate.setSelect(false);
                itemCountList.remove(itemUpdate.getModel().video_id);
            }
            else
            {
                itemUpdate.setSelect(true);
                itemCountList.put(itemUpdate.getModel().video_id,itemUpdate);
            }
            updateVideoCount();
            dataList.set(pos,itemUpdate);
            adapter.notifyDataSetChanged();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems,scrollInItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollInItem=linearLayoutManager.findFirstVisibleItemPosition();
                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                if (scrollInItem == 0)
                {
                    recyclerView.setNestedScrollingEnabled(true);
                }
                else
                {
                    recyclerView.setNestedScrollingEnabled(false);
                }
                if (userScrolled && (scrollOutitems == dataList.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiMyvideos();
                    }
                }


            }
        });
    }

    private void updateVideoCount() {
        if (itemCountList.keySet().size()==0)
        {
            btnNext.setEnabled(false);
            btnNext.setClickable(false);
            btnNext.setText(context.getString(R.string.next));
        }
        else
        {
            btnNext.setEnabled(true);
            btnNext.setClickable(true);
            btnNext.setText(context.getString(R.string.next)+"("+itemCountList.keySet().size()+")");
        }

    }


    Boolean isApiRun = false;
    //this will get the all videos data of user and then parse the data
    private void callApiMyvideos() {
        if (dataList == null)
            dataList = new ArrayList<>();

        isApiRun = true;
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
            parameters.put("starting_point", "" + pageCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showVideosAgainstUserID, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                isApiRun = false;
                parseData(resp);
            }
        });


    }

    public void parseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                ArrayList<HomeSelectionModel> temp_list = new ArrayList<>();


                JSONArray public_array = msg.optJSONArray("public");


                for (int i = 0; i < public_array.length(); i++) {
                    JSONObject itemdata = public_array.optJSONObject(i);

                    JSONObject video = itemdata.optJSONObject("Video");
                    JSONObject user = itemdata.optJSONObject("User");
                    JSONObject sound = itemdata.optJSONObject("Sound");
                    JSONObject userPrivacy = user.optJSONObject("PrivacySetting");
                    JSONObject userPushNotification = user.optJSONObject("PushNotification");

                    HomeModel item = Functions.parseVideoData(user, sound, video, userPrivacy, userPushNotification);
                    HomeSelectionModel itemModel=new HomeSelectionModel();
                    itemModel.setModel(item);
                    itemModel.setSelect(false);

                    if (item.user_id!=null && !(item.user_id.equals("null")) && !(item.user_id.equals("0")))
                    {
                        if (!(isFromCreate))
                        {
                            if (playlistMapList.containsKey(item.video_id))
                            {
                                itemModel.setSelect(true);
                                temp_list.add(itemModel);
                                itemCountList.put(itemModel.getModel().video_id,itemModel);
                            }
                            else
                            {
                                temp_list.add(itemModel);
                            }
                        }
                        else
                        {
                            temp_list.add(itemModel);
                        }
                    }



                }

                if (pageCount == 0) {
                    dataList.clear();
                    dataList.addAll(temp_list);
                } else {
                    dataList.addAll(temp_list);
                }

                adapter.notifyDataSetChanged();
            }
            else
            {
                if (pageCount==0)
                {
                    pageCount=0;
                    dataList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            if (dataList.isEmpty()) {
                view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.no_data_layout).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loadMoreProgress.setVisibility(View.GONE);
        }
    }

}