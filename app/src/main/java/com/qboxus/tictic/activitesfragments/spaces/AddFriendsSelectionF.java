package com.qboxus.tictic.activitesfragments.spaces;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.adapters.FriendsSelectionAdapter;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentAddFriendsSelectionBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class AddFriendsSelectionF extends BottomSheetDialogFragment implements View.OnClickListener{

    FragmentAddFriendsSelectionBinding binding;
    FragmentCallBack callBack;
    FriendsSelectionAdapter adapter;
    ArrayList<UserModel> datalist=new ArrayList<>();
    private Timer timer;

    int pageCount = 0;
    boolean ispostFinsh;
    GridLayoutManager layoutManager;
    boolean isFromClub;
    String clubId;


    public AddFriendsSelectionF() {
    }

    public AddFriendsSelectionF(FragmentCallBack callBack, boolean isFromClub) {
        this.callBack = callBack;
        this.isFromClub=isFromClub;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_friends_selection, container, false);
        initControl();
        return binding.getRoot();
    }

    private void initControl() {
        binding.tvDone.setOnClickListener(this);

        if (isFromClub)
        {
            clubId=getArguments().getString("clubId");
        }

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                }, 500);
            }
        });

        setupAdapter();

        populateData();

    }

    private void populateData() {
        if (isFromClub)
        {
            getClubMembersList();
        }
        else
        {
            getFriendsList();
        }
    }


    public void getFriendsList() {

        if (datalist == null)
            datalist = new ArrayList<>();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""));
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }


        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showFriends, parameters, Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                parseFriendsData(resp);
            }
        });




    }


    public void getClubMembersList() {

        if (datalist == null)
            datalist = new ArrayList<>();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""));
            parameters.put("club_id", clubId);
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }



        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showClubMembers, parameters, Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                parseClubMembersData(resp);
            }
        });


    }

    public void parseFriendsData(String responce) {
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                ArrayList<UserModel> temp_list = new ArrayList<>();

                for (int i = 0; i < msgArray.length(); i++) {

                    JSONObject object = msgArray.optJSONObject(i);
                    UserModel item= DataParsing.getUserDataModel(object.optJSONObject("Friends"));


                    String userStatus=item.getButton().toLowerCase();
                    if (userStatus.equalsIgnoreCase("following"))
                    {
                        item.setButton("Following");
                    }
                    else
                    if (userStatus.equalsIgnoreCase("friends"))
                    {
                        item.setButton("Friends");
                    }
                    else
                    if (userStatus.equalsIgnoreCase("follow back"))
                    {
                        item.setButton("Follow back");
                    }
                    else
                    {
                        item.setButton("Follow");
                    }

                    temp_list.add(item);

                }

                if (pageCount == 0) {
                    datalist.clear();
                    datalist.addAll(temp_list);
                } else {
                    datalist.addAll(temp_list);
                }

                adapter.notifyDataSetChanged();
            }

            if (datalist.isEmpty()) {
                binding.tabNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(binding.getRoot().getContext().getString(R.string.you_have_no_friends));
            } else {
                binding.tabNoData.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }
        finally {
            binding.loadMoreProgress.setVisibility(View.GONE);
        }
    }


    public void parseClubMembersData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                ArrayList<UserModel> temp_list = new ArrayList<>();

                for (int i = 0; i < msgArray.length(); i++) {

                    JSONObject object = msgArray.optJSONObject(i);
                    UserModel userDetailModel=DataParsing.getUserDataModel(object.optJSONObject("User"));

                    temp_list.add(userDetailModel);

                }

                if (pageCount == 0) {
                    datalist.clear();
                    datalist.addAll(temp_list);
                } else {
                    datalist.addAll(temp_list);
                }

                adapter.notifyDataSetChanged();
            }

            if (datalist.isEmpty()) {
                binding.tabNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(binding.getRoot().getContext().getString(R.string.you_have_no_friends));
            } else {
                binding.tabNoData.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        } finally {
            binding.loadMoreProgress.setVisibility(View.GONE);
        }
    }


    private void setupAdapter() {
        layoutManager = new GridLayoutManager(binding.getRoot().getContext(),3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(layoutManager);
        binding.recylerview.setHasFixedSize(true);

        adapter = new FriendsSelectionAdapter(datalist, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                UserModel itemUpdate=datalist.get(pos);
                if (itemUpdate.isSelected())
                {
                    itemUpdate.setSelected(false);
                }
                else
                {
                    itemUpdate.setSelected(true);
                }
                datalist.set(pos,itemUpdate);
                adapter.notifyDataSetChanged();
            }
        });
        binding.recylerview.setAdapter(adapter);
        binding.recylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

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

                scrollOutitems = layoutManager.findLastVisibleItemPosition();

                if (userScrolled && (scrollOutitems == datalist.size() - 1)) {
                    userScrolled = false;

                    if (binding.loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        binding.loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        populateData();
                    }
                }


            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvDone:
            {
                ArrayList<UserModel> selectedUser=new ArrayList<>();
                for (UserModel item:datalist)
                {
                    if (item.isSelected())
                    {
                        selectedUser.add(item);
                    }
                }
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                bundle.putSerializable("UserList",selectedUser);
                callBack.onResponce(bundle);
                dismiss();
            }
            break;
        }
    }
}