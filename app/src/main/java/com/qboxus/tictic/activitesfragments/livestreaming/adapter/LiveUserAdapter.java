package com.qboxus.tictic.activitesfragments.livestreaming.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.activitesfragments.livestreaming.model.LiveUserModel;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.CustomViewHolder> {

    public Context context;
    ArrayList<LiveUserModel> dataList;
    AdapterClickListener adapterClickListener;


    public LiveUserAdapter(Context context, ArrayList<LiveUserModel> userDatalist, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.dataList = userDatalist;
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_live_layout, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {

        final LiveUserModel item = dataList.get(i);

        if (TextUtils.isEmpty(item.getJoinStreamPrice()) || item.getJoinStreamPrice().equals("0"))
        {
            holder.ivLocker.setVisibility(View.GONE);
        }
        else
        {
            holder.ivLocker.setVisibility(View.VISIBLE);
        }
        holder.ivProfile.setController(Functions.frescoImageLoad(item.getUserPicture(),holder.ivProfile,false));

        if (item.getIsVerified().equals("1"))
        {
            holder.ivVerified.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivVerified.setVisibility(View.GONE);
        }
        holder.tvName.setText(item.getUserName());
        if (item.getOnlineType().equals("multicast"))
        {
            if (item.isDualStreaming())
            {
                holder.tvLive.setText(holder.itemView.getContext().getString(R.string.public_live));
            }
            else
            {
                holder.tvLive.setText(holder.itemView.getContext().getString(R.string.live));
            }
        }
        else
        {
            holder.tvLive.setText(holder.itemView.getContext().getString(R.string.private_live));
        }


        holder.bind(i, item, adapterClickListener);
    }



    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView ivProfile;
        ImageView ivLocker,ivVerified;
        public TextView tvName,tvLive;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            ivLocker = itemView.findViewById(R.id.ivLocker);
            ivVerified = itemView.findViewById(R.id.ivVerified);
            tvName = itemView.findViewById(R.id.tvName);
            tvLive=itemView.findViewById(R.id.tvLive);
        }

        public void bind(final int position, final Object item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, position, item);
            });
        }

    }

}