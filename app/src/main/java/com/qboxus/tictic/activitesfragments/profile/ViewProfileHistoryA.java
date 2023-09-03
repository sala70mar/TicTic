package com.qboxus.tictic.activitesfragments.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qboxus.tictic.adapters.FollowingAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.databinding.ActivityViewProfileHistoryBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.FollowingModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewProfileHistoryA extends AppCompatLocaleActivity {

    ActivityViewProfileHistoryBinding binding;
    String profileView;
    ArrayList<FollowingModel> datalist=new ArrayList<>();
    FollowingAdapter adapter;
    int pageCount = 0;
    boolean ispostFinsh;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE), this, getClass(),false);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_view_profile_history);

        initControl();
        actionControl();
    }

    private void actionControl() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileViewSetting();
            }
        });
        binding.tabNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.tabTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfileViewStatus();
            }
        });
    }

    private void openProfileViewSetting() {
        final EditProfileViewRuleSheetF fragment = new EditProfileViewRuleSheetF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    setupScreenData();
                    isActivityCallback=true;
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "EditProfileViewRuleF");
    }

    private void initControl() {
        linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(linearLayoutManager);
        binding.recylerview.setHasFixedSize(true);

        setupAdapter();
        setupScreenData();
    }

    private void setupAdapter() {
        adapter = new FollowingAdapter(binding.getRoot().getContext(),false,"visitor", datalist, new FollowingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, FollowingModel item) {

                switch (view.getId()) {
                    case R.id.action_txt:
                        if (Functions.checkLoginUser(ViewProfileHistoryA.this)) {
                            if (!item.fb_id.equals(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, "")))
                                followUnFollowUser(item, postion);
                        }
                        break;

                    case R.id.mainlayout:
                        openProfile(item);
                        break;
                }

            }
        }
        );
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

                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                Functions.printLog("resp", "" + scrollOutitems);
                if (userScrolled && (scrollOutitems == datalist.size() - 1)) {
                    userScrolled = false;

                    if (binding.loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        binding.loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callProfileVisitorApi();
                    }
                }


            }
        });

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                pageCount=0;
                callProfileVisitorApi();
            }
        });
    }


    public void followUnFollowUser(final FollowingModel item, final int position) {

        Functions.callApiForFollowUnFollow(ViewProfileHistoryA.this,
                Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, ""),
                item.fb_id,
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {


                    }

                    @Override
                    public void onSuccess(String responce) {
                        try {
                            JSONObject jsonObject=new JSONObject(responce);
                            String code=jsonObject.optString("code");
                            if(code.equalsIgnoreCase("200")){
                                JSONObject msg=jsonObject.optJSONObject("msg");
                                if(msg!=null){
                                    UserModel userDetailModel= DataParsing.getUserDataModel(msg.optJSONObject("User"));
                                    if(!(TextUtils.isEmpty(userDetailModel.getId()))){
                                        FollowingModel itemUpdte=item;
                                        String userStatus=userDetailModel.getButton().toLowerCase();
                                        itemUpdte.follow_status_button=Functions.getFollowButtonStatus(userStatus,binding.getRoot().getContext());
                                        datalist.set(position,itemUpdte);
                                        adapter.notifyDataSetChanged();

                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d(Constants.tag,"Exception : "+e);
                        }
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });


    }





    // this will open the profile of user which have uploaded the currenlty running video
    private void openProfile(final FollowingModel item) {
        Intent intent=new Intent(binding.getRoot().getContext(), ProfileA.class);
        intent.putExtra("user_id", ""+item.fb_id);
        intent.putExtra("user_name", ""+item.username);
        intent.putExtra("user_pic", ""+item.getProfile_pic());
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    private void setupScreenData() {
        profileView= Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_PROFILE_VIEW,"0");
        if (profileView.equals("1"))
        {
            if (binding.viewflliper.getCurrentView()==binding.containerHide)
            {
                binding.viewflliper.showNext();
            }
            binding.containerHide.setAlpha(0);
            binding.containerShow.setAlpha(1);
            binding.ivSetting.setVisibility(View.VISIBLE);
            pageCount=0;
            callProfileVisitorApi();
        }
        else
        {
            if (binding.viewflliper.getCurrentView()==binding.containerShow)
            {
                binding.viewflliper.showPrevious();
            }
            binding.containerHide.setAlpha(1);
            binding.containerShow.setAlpha(0);
            binding.ivSetting.setVisibility(View.GONE);
        }
    }


    // get the list of videos that you favourite
    public void callProfileVisitorApi() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, ""));
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (datalist.isEmpty())
        {
            Functions.showLoader(ViewProfileHistoryA.this,false,false);
        }
        VolleyRequest.JsonPostRequest(ViewProfileHistoryA.this, ApiLinks.showProfileVisitors, parameters, Functions.getHeaders(binding.getRoot().getContext()),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(ViewProfileHistoryA.this,resp);
                Functions.cancelLoader();
                parseProfileVisitorData(resp);
            }
        });


    }

    // parse the list of user that follow the profile
    public void parseProfileVisitorData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                ArrayList<FollowingModel> temp_list = new ArrayList<>();

                for (int i = 0; i < msgArray.length(); i++) {

                    JSONObject object = msgArray.optJSONObject(i);
                    UserModel userDetailModel= DataParsing.getUserDataModel(object.optJSONObject("Visitor"));

                    FollowingModel item = new FollowingModel();
                    item.fb_id = userDetailModel.getId();
                    item.first_name = userDetailModel.getFirstName();
                    item.last_name = userDetailModel.getLastName();
                    item.bio = userDetailModel.getBio();
                    item.username = userDetailModel.getUsername();
                    item.setProfile_pic(userDetailModel.getProfilePic());
                    item.isFollow=true;
                    String userStatus=userDetailModel.getButton();
                    if (userStatus.equalsIgnoreCase("following"))
                    {
                        item.follow_status_button = "Following";
                    }
                    else
                    if (userStatus.equalsIgnoreCase("friends"))
                    {
                        item.follow_status_button = "Friends";
                    }
                    else
                    if (userStatus.equalsIgnoreCase("follow back"))
                    {
                        item.follow_status_button = "Follow back";
                    }
                    else
                    {
                        item.follow_status_button = "Follow";
                    }
                    item.notificationType=userDetailModel.getNotification();

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
                binding.noDataLayout.setVisibility(View.VISIBLE);
                binding.tvVisitorInfo.setVisibility(View.GONE);
            } else {
                isActivityCallback=true;
                binding.noDataLayout.setVisibility(View.GONE);
                binding.tvVisitorInfo.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        } finally {
            binding.loadMoreProgress.setVisibility(View.GONE);
        }
    }


    private void updateProfileViewStatus() {
        Functions.showLoader(ViewProfileHistoryA.this, false, false);
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, "0"));
            profileView="1";
            parameters.put("profile_view", ""+profileView);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(ViewProfileHistoryA.this,false,false);
        VolleyRequest.JsonPostRequest(ViewProfileHistoryA.this, ApiLinks.editProfile, parameters,Functions.getHeaders(binding.getRoot().getContext()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(ViewProfileHistoryA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    JSONArray msg = response.optJSONArray("msg");
                    if (code.equals("200")) {

                        SharedPreferences.Editor editor = Functions.getSharedPreference(binding.getRoot().getContext()).edit();
                        editor.putString(Variables.U_PROFILE_VIEW, profileView);
                        editor.commit();
                        isActivityCallback=true;
                        setupScreenData();
                    } else {
                        if (msg != null) {
                            JSONObject jsonObject = msg.optJSONObject(0);
                            Functions.showToast(binding.getRoot().getContext(), jsonObject.optString("response"));
                        }
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });

    }


    boolean isActivityCallback=false;
    @Override
    public void onBackPressed() {
        if(isActivityCallback)
        {
            Intent intent = new Intent();
            intent.putExtra("isShow", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }
}