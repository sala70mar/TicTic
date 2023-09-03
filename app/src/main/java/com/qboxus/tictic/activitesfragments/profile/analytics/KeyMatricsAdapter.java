package com.qboxus.tictic.activitesfragments.profile.analytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.R;

import java.util.ArrayList;
import java.util.List;


public class KeyMatricsAdapter extends RecyclerView.Adapter<KeyMatricsAdapter.CustomViewHolder >{

    public Context context;
    List<KeyMatricsModel> gif_list = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(KeyMatricsModel item);
    }

    public KeyMatricsAdapter(Context context, List<KeyMatricsModel> gif_list, OnItemClickListener listener) {
        this.context = context;
        this.gif_list = gif_list;
        this.listener = listener;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_key_matrics,viewGroup,false);
        return  new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return gif_list.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView name_txt,count_txt;

        public CustomViewHolder(View view) {
            super(view);
            count_txt=view.findViewById(R.id.count_txt);
            name_txt=view.findViewById(R.id.name_txt);

        }

        public void bind(final KeyMatricsModel item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

        }

    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        KeyMatricsModel model = gif_list.get(i);
        holder.name_txt.setText(model.name);
        holder.count_txt.setText(model.count);
        holder.bind(model,listener);
   }

}