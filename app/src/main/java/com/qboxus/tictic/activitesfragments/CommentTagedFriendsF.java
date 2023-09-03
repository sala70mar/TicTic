package com.qboxus.tictic.activitesfragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.adapters.UsersAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentCommentTagedFriendsBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.models.UsersModel;
import com.qboxus.tictic.R;
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


public class CommentTagedFriendsF extends BottomSheetDialogFragment {

    View view;
    Context context;
    String userId;
    UsersAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<UsersModel> datalist;
    EditText searchEdit;
    ProgressBar pbar;
    CardView searchLayout;
    TextView titleTxt;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // Milliseconds
    SwipeRefreshLayout refreshLayout;
    int pageCount = 0;
    boolean ispostFinsh;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;

    FragmentCallBack callBack;

    public CommentTagedFriendsF(String userId,FragmentCallBack callBack) {
        this.userId=userId;
        this.callBack=callBack;
    }

    public CommentTagedFriendsF() {
    }

    private BottomSheetBehavior mBehavior;
    BottomSheetDialog dialog;

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_comment_taged_friends, null);
        dialog.setContentView(view);

        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setHideable(false);
        mBehavior.setDraggable(false);
        mBehavior.setPeekHeight((int) view.getContext().getResources().getDimension(R.dimen._500sdp),true);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comment_taged_friends, container, false);
        context = view.getContext();
        titleTxt =view.findViewById(R.id.title_txt);

        datalist = new ArrayList<>();
        refreshLayout=view.findViewById(R.id.refreshLayout);
        searchEdit =view.findViewById(R.id.search_edit);
        searchLayout =view.findViewById(R.id.search_layout);
        pbar =view.findViewById(R.id.pbar);
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        recyclerView = view.findViewById(R.id.recylerview);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        callApiForGetAllfollowing(true);

        searchEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String search_txt = searchEdit.getText().toString();
                                                pageCount=0;
                                                if (search_txt.length() > 0) {
                                                    callApiForOtherUsers();
                                                }
                                                else
                                                {
                                                    callApiForGetAllfollowing(true);
                                                }
                                            }
                                        });
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );

        adapter = new UsersAdapter(context, datalist, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {

                UsersModel item1 = (UsersModel) object;
                switch (view.getId()) {
                    case R.id.mainlayout:

                        item1.isSelected=!item1.isSelected;
                        adapter.notifyDataSetChanged();


                        break;
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                Functions.printLog("resp", "" + scrollOutitems);
                if (userScrolled && (scrollOutitems == datalist.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        if (searchEdit.getText().toString().length()>0)
                        {
                            callApiForOtherUsers();
                        }
                        else
                        {
                            callApiForGetAllfollowing(false);
                        }
                    }
                }


            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                pageCount=0;
                if (searchEdit.getText().toString().length()>0)
                {
                    callApiForOtherUsers();
                }
                else
                {
                    callApiForGetAllfollowing(false);
                }

            }
        });

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.donebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<UsersModel> selectedArray=new ArrayList<>();
                for (int i=0;i<datalist.size();i++){
                    if(datalist.get(i).isSelected){
                        selectedArray.add(datalist.get(i));
                    }
                }

                passDataBack(selectedArray);

            }
        });

        return view;
    }


    //call api for get the all follwers of specific profile
    private void callApiForOtherUsers() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("type", "user");
            parameters.put("keyword", searchEdit.getText().toString());
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }


        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.search, parameters,Functions.getHeaders(view.getContext()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                parseFollowingData(resp);
            }
        });


    }


    // Bottom two function will call the api and get all the videos form api and parse the json data
    private void callApiForGetAllfollowing(boolean isProgressShow) {
        if (datalist == null)
            datalist = new ArrayList<>();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isProgressShow)
        {
            pbar.setVisibility(View.VISIBLE);
        }
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showFollowing, parameters,Functions.getHeaders(view.getContext()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                if (isProgressShow)
                {
                    pbar.setVisibility(View.GONE);
                }
                parseFollowingData(resp);
            }
        });


    }

    public void parseFollowingData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msg = jsonObject.optJSONArray("msg");
                ArrayList<UsersModel> temp_list = new ArrayList<>();

                for (int i = 0; i < msg.length(); i++) {
                    JSONObject data = msg.optJSONObject(i);

                    JSONObject userObj = data.optJSONObject("User");
                    if (userObj == null)
                        userObj = data.optJSONObject("FollowingList");

                    UserModel userDetailModel= DataParsing.getUserDataModel(userObj);

                    UsersModel user = new UsersModel();
                    user.fb_id = userDetailModel.getId();
                    user.username = userDetailModel.getUsername();
                    user.first_name = userDetailModel.getFirstName();
                    user.last_name = userDetailModel.getLastName();
                    user.gender = userDetailModel.getGender();

                    user.profile_pic = userDetailModel.getProfilePic();

                    user.followers_count = userDetailModel.getFollowersCount();
                    user.videos = userDetailModel.getVideoCount();


                    temp_list.add(user);


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


    // this will open the profile of user which have uploaded the currenlty running video
    private void passDataBack(final ArrayList<UsersModel> datalist) {
        Bundle bundle=new Bundle();
        bundle.putBoolean("isShow", true);
        bundle.putSerializable("data", datalist);
        callBack.onResponce(bundle);
        dismiss();
    }

}