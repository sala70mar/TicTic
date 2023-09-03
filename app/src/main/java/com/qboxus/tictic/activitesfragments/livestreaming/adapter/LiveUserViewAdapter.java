package com.qboxus.tictic.activitesfragments.livestreaming.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.StreamJoinModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

public class LiveUserViewAdapter extends RecyclerView.Adapter<LiveUserViewAdapter.CustomViewHolder> {

    ArrayList<StreamJoinModel> dataList;
    AdapterClickListener adapterClickListener;
    Context context;

    public LiveUserViewAdapter(Context context,ArrayList<StreamJoinModel> userDatalist, AdapterClickListener adapterClickListener) {
        this.context=context;
        this.dataList = userDatalist;
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_live_view_layout, null);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {

        final StreamJoinModel item = dataList.get(i);

        holder.tvName.setText(item.getUserName());
        holder.ivProfile.setController(Functions.frescoImageLoad(item.getUserPic(),holder.ivProfile,false));
        holder.bind(i, item, adapterClickListener);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView ivProfile;
        public TextView tvName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(final int position, final Object item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> {
                    listener.onItemClick(v, position, item);
            });
        }

    }

}