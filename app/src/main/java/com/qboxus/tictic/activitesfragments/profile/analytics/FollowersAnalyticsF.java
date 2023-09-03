package com.qboxus.tictic.activitesfragments.profile.analytics;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentFollowersAnalyticsBinding;
import com.qboxus.tictic.databinding.ItemProgressChatLayoutBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class FollowersAnalyticsF extends Fragment implements View.OnClickListener{

    Calendar startCalender,endCalender;

    FragmentFollowersAnalyticsBinding binding;
    YourMarkerView marker;

    long totalDays=7;

    int totalFollower=4;
    int totalMaleFollowers=2,totalFemalFollowers=2;
    public FollowersAnalyticsF() {
        // Required empty public constructor
    }


    public static FollowersAnalyticsF newInstance() {
        FollowersAnalyticsF fragment = new FollowersAnalyticsF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_followers_analytics,container,false);



        startCalender= Calendar.getInstance();
        endCalender=Calendar.getInstance();

        startCalender.set(Calendar.DAY_OF_YEAR,startCalender.get(Calendar.DAY_OF_YEAR)-7);


        setupChart(binding.follwersChart);

        binding.selectDateLayout.setOnClickListener(this);


        binding.followersExplainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DetailMsgF fragment = DetailMsgF.newInstance(binding.getRoot().getContext().getString(R.string.followers),binding.getRoot().getContext().getString(R.string.followers_msg));
                fragment.show(getChildFragmentManager(), "DetailMsgF");
            }
        });

        binding.insightsExplainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DetailMsgF fragment = DetailMsgF.newInstance(binding.getRoot().getContext().getString(R.string.followers_insights),binding.getRoot().getContext().getString(R.string.followers_insights_msg));
                fragment.show(getChildFragmentManager(), "DetailMsgF");
            }
        });

        return binding.getRoot();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    callApiShowAnalytics();
                }
            },200);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.selectDateLayout:
                openBottomSheetforDate();
                break;
        }
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
                        callApiShowAnalytics();
                    }
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putLong("startDate",startCalender.getTimeInMillis());
        bundle.putLong("endDate",endCalender.getTimeInMillis());
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "DateSelectSheetF");
    }



    private void openBottomSheetforCalender() {
        final CustomeCalenderF fragment = new CustomeCalenderF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle!=null) {
                    startCalender.setTimeInMillis(bundle.getLong("startDate"));
                    endCalender.setTimeInMillis(bundle.getLong("endDate"));
                    callApiShowAnalytics();
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putLong("startDate",startCalender.getTimeInMillis());
        bundle.putLong("endDate",endCalender.getTimeInMillis());
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "DateSelectSheetF");
    }

    private void setupChart(LineChart chart) {

        {
            chart.setBackgroundColor(Color.WHITE);
            chart.getDescription().setEnabled(false);

            chart.setTouchEnabled(true);
            chart.setDrawGridBackground(false);
            chart.setDragEnabled(false);
            chart.setScaleEnabled(false);
            chart.setPinchZoom(false);

            marker = new YourMarkerView(getContext(), R.layout.item_chart_marker_view);
            chart.setMarker(marker);
        }



    }


    private void setData() {

        Functions.printLog(Constants.tag,"setData method call");

        if (binding.follwersChart.getData() != null &&
                binding.follwersChart.getData().getDataSetCount() > 0) {
            binding.follwersChart.getData().getDataSetByIndex(0).clear();
            binding.follwersChart.clear();
            binding.follwersChart.invalidate();
            binding.follwersChart.notifyDataSetChanged();
        }

        ArrayList<Entry> values = new ArrayList<>();
        float maxValue=0;

        for (int i = 0; i < graphDataArrayList.size(); i++) {

            GraphData data=graphDataArrayList.get(i);
            if(maxValue<data.count){
                maxValue=data.count;
            }
            values.add(new Entry(i, data.count));
        }

        marker.setDataList(graphDataArrayList);

        XAxis xAxis = binding.follwersChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(value<graphDataArrayList.size())
                return DateOperations.INSTANCE.changeDateFormat("yyyy-MM-dd","dd MMM",graphDataArrayList.get((int) value).date);
                else
                    return "";
            }
        });

        YAxis yAxis;
        {
            yAxis = binding.follwersChart.getAxisLeft();
            binding.follwersChart.getAxisRight().setEnabled(false);
            yAxis.enableGridDashedLine(10f, 10f, 0f);
            yAxis.setAxisMaximum(maxValue);
            yAxis.setAxisMinimum(0f);
        }



        LineDataSet set1;



      //  else
        {
            set1 = new LineDataSet(values,null);
            set1.setDrawIcons(false);
            set1.setDrawCircles(false);
            set1.setDrawCircleHole(false);
            set1.setDrawValues(false);

            set1.disableDashedLine();
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setValueTextSize(9f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return binding.follwersChart.getAxisLeft().getAxisMinimum();
                }
            });

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.d_fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            binding.follwersChart.setData(data);
        }

    }



    private void callApiShowAnalytics() {
        totalDays = DateOperations.INSTANCE.getDays(startCalender.getTime(),endCalender.getTime());

        binding.dateRangeTxt.setText(DateOperations.INSTANCE.getDate(startCalender.getTimeInMillis(),"MMM dd") +" - "+
                DateOperations.INSTANCE.getDate(endCalender.getTimeInMillis(),"MMM dd"));

        binding.daysTxt.setText(binding.getRoot().getContext().getString(R.string.last)+" "+totalDays+" "+binding.getRoot().getContext().getString(R.string.days));

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(getContext()).getString(Variables.U_ID, ""));
            parameters.put("start_datetime", DateOperations.INSTANCE.getDate(startCalender.getTimeInMillis(),"yyyy-MM-dd hh:mm:ss"));
            parameters.put("end_datetime", DateOperations.INSTANCE.getDate(endCalender.getTimeInMillis(),"yyyy-MM-dd hh:mm:ss"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showAnalytics, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);

                parseData(resp);
            }
        });


    }
    ArrayList<GraphData> graphDataArrayList=new ArrayList<>();
    public void parseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                JSONObject Stats= msg.optJSONObject("Stats");

                totalFollower=Integer.parseInt(Stats.optString("total_followers","0"));
                totalMaleFollowers=Integer.parseInt(Stats.optString("total_male_followers","0"));
                totalFemalFollowers=Integer.parseInt(Stats.optString("total_female_followers","0"));
                binding.totalFollowerCount.setText(""+totalFollower);


                graphDataArrayList.clear();
                JSONArray followers_graph = Stats.optJSONArray("followers_graph");
                if (followers_graph != null) {
                    for (int i = 0; i < followers_graph.length(); i++) {
                        JSONArray jsonArray = followers_graph.getJSONArray(i);
                        JSONObject data = jsonArray.optJSONObject(0);
                        GraphData graphData = new GraphData();
                        graphData.date = data.optString("date");
                        graphData.count = Integer.parseInt(data.optString("count"));
                        graphDataArrayList.add(graphData);
                        Functions.printLog(Constants.tag, "followers_graph" + graphDataArrayList.get(i).count);
                    }
                }

                setData();
                binding.follwersChart.animateX(1500);
                Legend l = binding.follwersChart.getLegend();
                l.setForm(Legend.LegendForm.LINE);


                if(totalFollower==0){
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                    binding.insightsLayout.setVisibility(View.GONE);
                }
                else {
                    binding.noDataLayout.setVisibility(View.GONE);
                    binding.insightsLayout.setVisibility(View.VISIBLE);

                    setPieChatGender();
                    setPieChatGenderData();


                    JSONArray age_group = Stats.optJSONArray("age_group");
                    if (age_group != null) {
                        for (int i = 0; i < age_group.length(); i++) {
                            binding.agePercentLayout.removeAllViews();
                            JSONArray jsonArray = age_group.getJSONArray(i);
                            JSONObject data = jsonArray.optJSONObject(0);
                            ItemProgressChatLayoutBinding binding1 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_progress_chat_layout, null, false);
                            binding1.titleTxt.setText(data.optString("age_range"));
                            binding1.progressBar.setProgress(Functions.getPercentage(Integer.parseInt(data.optString("count", "0")), totalFollower));
                            binding1.percentValueTxt.setText(Functions.getPercentage(Integer.parseInt(data.optString("count", "0")), totalFollower) + "%");
                            binding.agePercentLayout.addView(binding1.getRoot());
                        }
                    }


                    JSONArray followers_country = Stats.optJSONArray("followers_country");
                    if (followers_country != null) {
                        for (int i = 0; i < followers_country.length(); i++) {
                            binding.countryPercentLayout.removeAllViews();
                            JSONObject data = followers_country.optJSONObject(i);
                            ItemProgressChatLayoutBinding binding1 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_progress_chat_layout, null, false);
                            binding1.titleTxt.setText(data.optString("Country"));
                            binding1.progressBar.setProgress(Functions.getPercentage(Integer.parseInt(data.optString("count", "0")), totalFollower));
                            binding1.percentValueTxt.setText(Functions.getPercentage(Integer.parseInt(data.optString("count", "0")), totalFollower) + "%");
                            binding.countryPercentLayout.addView(binding1.getRoot());
                        }
                    }


                    JSONArray followers_city = Stats.optJSONArray("followers_city");
                    if (followers_city != null) {
                        for (int i = 0; i < followers_city.length(); i++) {
                            binding.cityPercentLayout.removeAllViews();
                            JSONObject data = followers_city.optJSONObject(i);
                            ItemProgressChatLayoutBinding binding1 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_progress_chat_layout, null, false);
                            binding1.titleTxt.setText(data.optString("Country"));
                            binding1.progressBar.setProgress(Functions.getPercentage(Integer.parseInt(data.optString("count", "0")), totalFollower));
                            binding1.percentValueTxt.setText(Functions.getPercentage(Integer.parseInt(data.optString("count", "0")), totalFollower) + "%");
                            binding.cityPercentLayout.addView(binding1.getRoot());
                        }
                    }

                }

            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }




    public void setPieChatGender(){
        binding.genderPieChart.setUsePercentValues(false);
        binding.genderPieChart.getDescription().setEnabled(false);
        binding.genderPieChart.setExtraOffsets(5, 10, 5, 5);

        binding.genderPieChart.setDragDecelerationFrictionCoef(0.95f);



        binding.genderPieChart.setDrawCenterText(false);
        binding.genderPieChart.setDrawEntryLabels(true);
        binding.genderPieChart.setDrawHoleEnabled(false);



        binding.genderPieChart.setRotationEnabled(false);
        binding.genderPieChart.setHighlightPerTapEnabled(true);


        binding.genderPieChart.animateY(1400, Easing.EaseInOutQuad);


        Legend l =   binding.genderPieChart.getLegend();
        l.setEnabled(false);


        // entry label styling
        binding.genderPieChart.setEntryLabelColor(Color.WHITE);
        binding.genderPieChart.setEntryLabelTextSize(12f);
    }

    public void setPieChatGenderData(){
        ArrayList<PieEntry> entries = new ArrayList<>();

            entries.add(new PieEntry((float) Functions.getPercentage(totalMaleFollowers,totalFollower),
                    "Male"));
        entries.add(new PieEntry((float) Functions.getPercentage(totalFemalFollowers,totalFollower),
                "Female"));

        binding.malepercentagetxt.setText(Functions.getPercentage(totalMaleFollowers,totalFollower)+"%");
        binding.femalepercentagetxt.setText(Functions.getPercentage(totalFemalFollowers,totalFollower)+"%");

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);



        dataSet.setColors(ContextCompat.getColor(requireContext(), R.color.appColor), ContextCompat.getColor(requireContext(), R.color.pink_color_picker));

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(9f);

        data.setValueTextColor(Color.WHITE);
        binding.genderPieChart.setData(data);

        binding.genderPieChart.highlightValues(null);

        binding.genderPieChart.invalidate();
    }

}