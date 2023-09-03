package com.qboxus.tictic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.R;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.models.SoundsModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/19/2019.
 */


public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<Object> datalist;
    AdapterClickListener adapterClickListener;

    public SoundListAdapter(Context context, ArrayList<Object> arrayList, AdapterClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.adapterClickListener = listener;
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
        SoundsModel item = (SoundsModel) datalist.get(i);
        holder.soundName.setText(item.sound_name);
        holder.descriptionTxt.setText(item.description);
        holder.durationTimeTxt.setText(item.duration);

        holder.soundImage.setController(Functions.frescoImageLoad(item.getThum(),R.drawable.ractengle_solid_primary,holder.soundImage,false));

        if (item.fav.equals("1"))
            holder.favBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_favourite));
        else
            holder.favBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_un_favourite));

        holder.bind(i, item, adapterClickListener);

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView done, favBtn;
        TextView soundName, descriptionTxt, durationTimeTxt;
        SimpleDraweeView soundImage;

        public CustomViewHolder(View view) {
            super(view);
            done = view.findViewById(R.id.done);
            favBtn = view.findViewById(R.id.fav_btn);


            soundName = view.findViewById(R.id.sound_name);
            descriptionTxt = view.findViewById(R.id.description_txt);
            soundImage = view.findViewById(R.id.sound_image);

            durationTimeTxt = view.findViewById(R.id.duration_time_txt);
        }

        public void bind(final int pos, final SoundsModel item, final AdapterClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            done.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

            favBtn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });

        }


    }


}

