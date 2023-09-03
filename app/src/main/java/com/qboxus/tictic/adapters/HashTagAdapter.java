package com.qboxus.tictic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.models.HashTagModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/19/2019.
 */


public class HashTagAdapter extends RecyclerView.Adapter<HashTagAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<HashTagModel> datalist;
    AdapterClickListener adapterClickListener;

    public HashTagAdapter(Context context, ArrayList<HashTagModel> arrayList, AdapterClickListener adapterClickListener) {
        this.context = context;
        datalist = arrayList;
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public HashTagAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_hashtag_list, viewGroup, false);
        HashTagAdapter.CustomViewHolder viewHolder = new HashTagAdapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final HashTagAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);
        HashTagModel item = datalist.get(i);
        holder.nameTxt.setText(item.name);

        holder.viewsTxt.setText(Functions.getSuffix(item.videos_count));

        holder.bind(i, item, adapterClickListener);

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, viewsTxt;

        public CustomViewHolder(View view) {
            super(view);

            nameTxt = view.findViewById(R.id.name_txt);
            viewsTxt = view.findViewById(R.id.views_txt);
        }

        public void bind(final int pos, final Object item, final AdapterClickListener listener) {



            itemView.setOnClickListener(v -> {

                listener.onItemClick(v, pos, item);

            });
        }


    }


}

