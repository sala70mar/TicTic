package com.qboxus.tictic.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.models.SoundCatagoryModel;
import com.qboxus.tictic.models.SoundsModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class SoundsAdapter extends RecyclerView.Adapter<SoundsAdapter.CustomViewHolder> {


    ArrayList<SoundCatagoryModel> datalist;
    ArrayList<SoundCatagoryModel> datalist_filter;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, SoundsModel item);
    }

    public OnItemClickListener listener;

    public SoundsAdapter( ArrayList<SoundCatagoryModel> arrayList, OnItemClickListener listener) {
        datalist = arrayList;
        datalist_filter = arrayList;
        this.listener = listener;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_sound_layout, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return datalist_filter.size();
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);


        SoundCatagoryModel item = datalist_filter.get(i);
        holder.title.setText(item.catagory);

        holder.bind(i, new SoundsModel(), listener);

        SoundItemsAdapter adapter = new SoundItemsAdapter( item.sound_list, new SoundItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, SoundsModel item) {

                listener.onItemClick(view, postion, item);
            }
        });

        GridLayoutManager gridLayoutManager;
        if (item.sound_list.size() == 1)
            gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), 1);

        else if (item.sound_list.size() == 2)
            gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), 2);

        else
            gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), 3);

        gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.findSnapView(gridLayoutManager);
        snapHelper.attachToRecyclerView(holder.recyclerView);


    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView title, see_all_btn;
        RecyclerView recyclerView;

        public CustomViewHolder(View view) {
            super(view);
            see_all_btn = view.findViewById(R.id.see_all_btn);
            title = view.findViewById(R.id.title);
            recyclerView = view.findViewById(R.id.horizontal_recylerview);


        }

        public void bind(final int pos, final SoundsModel item, final SoundsAdapter.OnItemClickListener listener) {

            see_all_btn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });
        }

    }

}


class SoundItemsAdapter extends RecyclerView.Adapter<SoundItemsAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<SoundsModel> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, SoundsModel item);
    }

    public SoundItemsAdapter.OnItemClickListener listener;


    public SoundItemsAdapter( ArrayList<SoundsModel> arrayList, SoundItemsAdapter.OnItemClickListener listener) {
        datalist = arrayList;
        this.listener = listener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout, viewGroup, false);
        return new CustomViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        SoundsModel item = datalist.get(i);
        try {

            holder.bind(i, datalist.get(i), listener);

            holder.sound_name.setText(item.sound_name);
            holder.description_txt.setText(item.description);
            holder.duration_time_txt.setText(item.duration);

            if (item.fav.equals("1"))
                holder.fav_btn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_favourite));
            else
                holder.fav_btn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_un_favourite));

            holder.sound_image.setController(Functions.frescoImageLoad(item.getThum(),R.drawable.ractengle_solid_primary,holder.sound_image,false));


        } catch (Exception e) {
            Functions.printLog(Constants.tag,"Exception : "+e);
        }

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView done, fav_btn;
        TextView sound_name, description_txt, duration_time_txt;
        SimpleDraweeView sound_image;

        public CustomViewHolder(View view) {
            super(view);

            done = view.findViewById(R.id.done);
            fav_btn = view.findViewById(R.id.fav_btn);


            sound_name = view.findViewById(R.id.sound_name);
            description_txt = view.findViewById(R.id.description_txt);
            sound_image = view.findViewById(R.id.sound_image);

            duration_time_txt = view.findViewById(R.id.duration_time_txt);

        }

        public void bind(final int pos, final SoundsModel item, final SoundItemsAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            done.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            fav_btn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

        }


    }


}

