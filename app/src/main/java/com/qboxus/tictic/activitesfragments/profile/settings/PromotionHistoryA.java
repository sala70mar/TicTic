package com.qboxus.tictic.activitesfragments.profile.settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.profile.analytics.CustomeCalenderF;
import com.qboxus.tictic.activitesfragments.profile.analytics.DateOperations;
import com.qboxus.tictic.activitesfragments.profile.analytics.DateSelectSheetF;
import com.qboxus.tictic.activitesfragments.walletandwithdraw.MyWallet;
import com.qboxus.tictic.adapters.PromotionHistoryAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.ActivityPromotionHistoryBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.PromotionHistoryModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;

public class PromotionHistoryA extends AppCompatLocaleActivity {

    ActivityPromotionHistoryBinding binding;
    Calendar startCalender,endCalender;
    long totalDays=7;
    String totalCoins="0",totalDestinationTap="0",totalLikes="0",totalViews="0";
    long myWalletCoins=0;

    LinearLayoutManager linearLayoutManager;
    int pageCount = 0;
    boolean ispostFinsh;
    ArrayList<PromotionHistoryModel> dataList=new ArrayList<>();
    PromotionHistoryModel itemPromotionSelected;
    PromotionHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_promotion_history);
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
        binding.selectDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetforDate();
            }
        });

    }

    private void openBottomSheetforDate() {
        final DateSelectSheetF fragment = new DateSelectSheetF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle!=null) {
                    if(bundle.getBoolean("isCustom")){
                        openBottomSheetforCalender();
                    }
                    else {
                        startCalender.setTimeInMillis(bundle.getLong("startDate"));
                        endCalender.setTimeInMillis(bundle.getLong("endDate"));
                        pageCount=0;
                        callApiShowHistory();
                    }
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putLong("startDate",startCalender.getTimeInMillis());
        bundle.putLong("endDate",endCalender.getTimeInMillis());
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "DateSelectSheetF");
    }

    private void openBottomSheetforCalender() {
        final CustomeCalenderF fragment = new CustomeCalenderF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle!=null) {
                    startCalender.setTimeInMillis(bundle.getLong("startDate"));
                    endCalender.setTimeInMillis(bundle.getLong("endDate"));
                    pageCount=0;
                    callApiShowHistory();
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putLong("startDate",startCalender.getTimeInMillis());
        bundle.putLong("endDate",endCalender.getTimeInMillis());
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "DateSelectSheetF");
    }


    private void initControl() {
        myWalletCoins=Long.parseLong(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_WALLET, "0"));

        setupDates();
        setupAdapter();

        pageCount=0;
        callApiShowHistory();
    }

    private void setupDates() {
        startCalender= Calendar.getInstance();
        endCalender=Calendar.getInstance();

        startCalender.set(Calendar.DAY_OF_YEAR,startCalender.get(Calendar.DAY_OF_YEAR)-7);

    }

    private void setupAdapter() {
        linearLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(linearLayoutManager);
        adapter = new PromotionHistoryAdapter(dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                itemPromotionSelected=dataList.get(pos);
                switch (view.getId()) {
                    case R.id.btnPromoteAgain:
                    {
                        addNewPromotionByUsingOldData();
                    }
                    break;
                }

            }
        });
        ((SimpleItemAnimator) binding.recylerview.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.recylerview.setAdapter(adapter);
        binding.recylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    if (binding.loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        binding.loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiShowHistory();
                    }
                }


            }
        });

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                pageCount=0;
                callApiShowHistory();
            }
        });
    }

    private void addNewPromotionByUsingOldData() {
        long total=Long.parseLong(itemPromotionSelected.getCoin());
        if (myWalletCoins>total)
        {
            requestToPromoteUserVideo();
        }
        else
        {
            Intent intent=new Intent(binding.getRoot().getContext(), MyWallet.class);
            startActivity(intent);
            resultCallback.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            myWalletCoins=Long.parseLong(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_WALLET, "0"));
                            addNewPromotionByUsingOldData();
                        }
                    }
                }
            });


    public void requestToPromoteUserVideo(){
        JSONObject params=new JSONObject();
        try {

            String differenceDays=Functions.getDurationInDays("yyyy-MM-dd HH:mm:ss",itemPromotionSelected.getStart_datetime(),itemPromotionSelected.getEnd_datetime());

            params.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,""));
            params.put("video_id", ""+itemPromotionSelected.getVideo_id());
            params.put("destination", ""+itemPromotionSelected.getDestination());
            params.put("audience_id", ""+itemPromotionSelected.getAudience_id());
            params.put("start_datetime", ""+Functions.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
            params.put("end_datetime", ""+Functions.getCurrentDate("yyyy-MM-dd HH:mm:ss",Integer.valueOf(differenceDays)));
            params.put("coin", ""+itemPromotionSelected.getCoin());
            params.put("total_reach", ""+itemPromotionSelected.getTotal_reach());
            if (itemPromotionSelected.getDestination().equalsIgnoreCase("website"))
            {
                params.put("action_button", ""+itemPromotionSelected.getAction_button());
                params.put("website_url", ""+itemPromotionSelected.getWebsite_url());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(Constants.tag,"params: "+params);
        Functions.showLoader(PromotionHistoryA.this,false,false);
        VolleyRequest.JsonPostRequest(PromotionHistoryA.this, ApiLinks.addPromotion, params,Functions.getHeaders(PromotionHistoryA.this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(PromotionHistoryA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String code=jsonObject.optString("code");
                    if(code!=null && code.equals("200")){
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        UserModel userDetailModel= DataParsing.getUserDataModel(msgObj.optJSONObject("User"));
                        SharedPreferences.Editor editor = Functions.getSharedPreference(binding.getRoot().getContext()).edit();
                        editor.putString(Variables.U_WALLET, ""+userDetailModel.getWallet());
                        editor.commit();

                        pageCount=0;
                        callApiShowHistory();
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }


            }
        });

    }



    private void callApiShowHistory() {
        totalDays = DateOperations.INSTANCE.getDays(startCalender.getTime(),endCalender.getTime());

        binding.dateRangeTxt.setText(DateOperations.INSTANCE.getDate(startCalender.getTimeInMillis(),"MMM dd") +" - "+
                DateOperations.INSTANCE.getDate(endCalender.getTimeInMillis(),"MMM dd"));

        binding.daysTxt.setText(binding.getRoot().getContext().getString(R.string.last)+" "+totalDays+" "+binding.getRoot().getContext().getString(R.string.days));

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, ""));
            parameters.put("start_datetime", DateOperations.INSTANCE.getDate(startCalender.getTimeInMillis(),"yyyy-MM-dd hh:mm:ss"));
            parameters.put("end_datetime", DateOperations.INSTANCE.getDate(endCalender.getTimeInMillis(),"yyyy-MM-dd hh:mm:ss"));
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(PromotionHistoryA.this, ApiLinks.showPromotions, parameters,Functions.getHeaders(binding.getRoot().getContext()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(PromotionHistoryA.this,resp);
                binding.refreshLayout.setRefreshing(false);
                parseData(resp);
            }
        });


    }

    // parse the video list data
    public void parseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONObject msgObj=jsonObject.getJSONObject("msg");
                JSONObject statsObj=msgObj.getJSONObject("Stats");
                JSONArray detailsArray = msgObj.getJSONArray("Details");
                ArrayList<PromotionHistoryModel> temp_list = new ArrayList<>();


                totalCoins=statsObj.optString("total_coins","0");
                totalDestinationTap=statsObj.optString("total_destination_tap","0");
                totalLikes=statsObj.optString("total_likes","0");
                totalViews=statsObj.optString("total_views","0");
                setupDashboard();

                for (int i = 0; i < detailsArray.length(); i++) {
                    JSONObject itemdata = detailsArray.optJSONObject(i);
                    JSONObject video = itemdata.optJSONObject("Video");
                    PromotionHistoryModel item=DataParsing.parsePromotionHistory(itemdata.optJSONObject("Promotion"));
                    item.setVideo_thumb(video.optString("thum"));
                    item.setVideo_views(video.optString("view"));
                    temp_list.add(item);
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
                binding.noDataLayout.setVisibility(View.VISIBLE);
            } else {
                binding.noDataLayout.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        } finally {
            binding.loadMoreProgress.setVisibility(View.GONE);
        }
    }

    private void setupDashboard() {
        binding.tvCoinSpent.setText(Functions.getSuffix(totalCoins));
        binding.tvVideoViews.setText(Functions.getSuffix(totalViews));
        binding.tvLinkClicks.setText(Functions.getSuffix(totalDestinationTap));
        binding.tvTotalLikes.setText(Functions.getSuffix(totalLikes));
    }


    boolean isNotifyCallback=false;
    @Override
    public void onBackPressed() {
        if (isNotifyCallback)
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