package com.qboxus.tictic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.models.ReportTypeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

public class ReportTypeAdapter extends RecyclerView.Adapter<ReportTypeAdapter.CustomViewHolder> {

    public Context context;
    private OnItemClickListener listener;
    private ArrayList<ReportTypeModel> dataList;


    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, ReportTypeModel item, View view);
    }


    public ReportTypeAdapter(Context context, ArrayList<ReportTypeModel> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_report_list, viewGroup,false);
        return new CustomViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        ReportTypeModel item = dataList.get(i);
        holder.setIsRecyclable(false);

        holder.bind(i, item, listener);
        holder.reportName.setText(item.title);

        Functions.printLog(Constants.tag, item.title);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView reportName;
        RelativeLayout rltReport;

        public CustomViewHolder(View view) {
            super(view);


            reportName = view.findViewById(R.id.report_name);
            rltReport = view.findViewById(R.id.rlt_report);
        }

        public void bind(final int postion, final ReportTypeModel item, final OnItemClickListener listener) {

            rltReport.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);

            });
        }


    }
}
