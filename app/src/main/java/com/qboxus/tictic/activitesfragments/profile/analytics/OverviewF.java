package com.qboxus.tictic.activitesfragments.profile.analytics;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentOverviewBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OverviewF extends Fragment implements View.OnClickListener{


    List<List<KeyMatricsModel>> sliderList=new ArrayList<>();
    KeyMatricsSliderAdapter adapter;
    List<KeyMatricsModel> data_list=new ArrayList<>();
    FragmentOverviewBinding binding;

    Calendar startCalender,endCalender;

    long totalDays=7;
    YourMarkerView marker;

    public OverviewF() {
    }


    public static OverviewF newInstance() {
        OverviewF fragment = new OverviewF();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_overview,container,false);


        data_list=new ArrayList<>();
        sliderList=new ArrayList<>();

        startCalender= Calendar.getInstance();
        endCalender=Calendar.getInstance();

        startCalender.set(Calendar.DAY_OF_YEAR,startCalender.get(Calendar.DAY_OF_YEAR)-7);


        setupChart(binding.videoViewChart);

        callApiShowAnalytics();

        binding.selectDateLayout.setOnClickListener(this);

        return binding.getRoot();
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


    private void SetUpGiftSliderAdapter() {
        sliderList.clear();
        sliderList.addAll(Functions.createChunksOfListKeyMatrics(data_list,4));
        adapter=new KeyMatricsSliderAdapter(sliderList, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                   KeyMatricsModel selectedModel= (KeyMatricsModel) bundle.getSerializable("Data");
                }
            }
        });
        binding.imageSlider.setSliderAdapter(adapter);
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


        if (binding.videoViewChart.getData() != null &&
                binding.videoViewChart.getData().getDataSetCount() > 0) {
            binding.videoViewChart.getData().getDataSetByIndex(0).clear();
            binding.videoViewChart.clear();
            binding.videoViewChart.invalidate();
            binding.videoViewChart.notifyDataSetChanged();
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

        XAxis xAxis = binding.videoViewChart.getXAxis();
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
            yAxis = binding.videoViewChart.getAxisLeft();
            binding.videoViewChart.getAxisRight().setEnabled(false);
            yAxis.enableGridDashedLine(10f, 10f, 0f);
            yAxis.setAxisMaximum(maxValue);
            yAxis.setAxisMinimum(0f);
        }



        LineDataSet set1;

        if (binding.videoViewChart.getData() != null &&
                binding.videoViewChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) binding.videoViewChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            binding.videoViewChart.getData().notifyDataChanged();
            binding.videoViewChart.notifyDataSetChanged();
        }

        else {
            set1 = new LineDataSet(values,null);
            set1.setDrawIcons(false);
           // set1.enableDashedLine(10f, 5f, 0f)
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
                    return binding.videoViewChart.getAxisLeft().getAxisMinimum();
                }
            });

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.d_fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            binding.videoViewChart.setData(data);
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
            parameters.put("start_datetime", DateOperations.INSTANCE.getDate(startCalender.getTimeInMillis(),"yyyy-MM-dd"));
            parameters.put("end_datetime", DateOperations.INSTANCE.getDate(endCalender.getTimeInMillis(),"yyyy-MM-dd"));

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
    ArrayList<GraphData> graphDataArrayList;
    public void parseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                JSONObject Stats= msg.optJSONObject("Stats");
                JSONArray video_views_graph=Stats.optJSONArray("video_views_graph");

                if(Stats!=null) {
                    data_list.clear();
                    data_list.add(new KeyMatricsModel(getActivity().getString(R.string.video_views), Stats.optString("total_video_views")));
                    data_list.add(new KeyMatricsModel(getActivity().getString(R.string.profile_views), Stats.optString("total_profile_visits")));
                    data_list.add(new KeyMatricsModel(getActivity().getString(R.string.likes), Stats.optString("total_video_likes")));
                    data_list.add(new KeyMatricsModel(getActivity().getString(R.string.comments), Stats.optString("total_video_comments")));
                    data_list.add(new KeyMatricsModel(getActivity().getString(R.string.followers), Stats.optString("total_followers")));
                    data_list.add(new KeyMatricsModel(getActivity().getString(R.string.male_followers), Stats.optString("total_male_followers")));
                    data_list.add(new KeyMatricsModel(getActivity().getString(R.string.female_followers), Stats.optString("total_female_followers")));
                    SetUpGiftSliderAdapter();
                }

                graphDataArrayList=new ArrayList<>();
                if(video_views_graph!=null){
                    for (int i=0;i<video_views_graph.length();i++){
                        JSONArray jsonArray=video_views_graph.getJSONArray(i);
                        JSONObject data=jsonArray.optJSONObject(0);
                        GraphData graphData=new GraphData();
                        graphData.date=data.optString("date");
                        graphData.count= Integer.parseInt(data.optString("count"));
                        graphDataArrayList.add(graphData);
                        Functions.printLog(Constants.tag,"video_views_graph"+graphDataArrayList.get(i).count);
                    }
                }

                setData();
                binding.videoViewChart.animateX(1500);
                Legend l = binding.videoViewChart.getLegend();
                l.setForm(Legend.LegendForm.LINE);

            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }



}