package com.qboxus.tictic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.PlaylistTitleModel;
import com.qboxus.tictic.R;

import java.util.ArrayList;

public class PlaylistTitleAdapter extends RecyclerView.Adapter<PlaylistTitleAdapter.CustomViewHolder> {


    private ArrayList<PlaylistTitleModel> dataList;
    AdapterClickListener adapterClickListener;

    public PlaylistTitleAdapter(ArrayList<PlaylistTitleModel> dataList, AdapterClickListener adapterClickListener) {
        this.dataList = dataList;
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_playlist_title_layout, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivTitle;
        public CustomViewHolder(View view) {
            super(view);
            ivTitle=view.findViewById(R.id.ivTitle);
            tvTitle = view.findViewById(R.id.tvTitle);

        }
        public void bind(final int position, final Object item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> {
                adapterClickListener.onItemClick(v, position, item);

            });

        }
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        final PlaylistTitleModel item = dataList.get(i);
        holder.tvTitle.setText(item.getName());
        if (item.getId().equals("0"))
        {
            holder.tvTitle.setVisibility(View.GONE);
            holder.ivTitle.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ic_add_round));
        }
        else
        {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.ivTitle.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ic_playlist_add));
        }
        holder.bind(i, item, adapterClickListener);

    }

}