package com.qboxus.tictic.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.models.SoundsModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/19/2019.
 */


public class FavouriteSoundAdapter extends RecyclerView.Adapter<FavouriteSoundAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<SoundsModel> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, SoundsModel item);
    }

    public FavouriteSoundAdapter.OnItemClickListener listener;


    public FavouriteSoundAdapter(Context context, ArrayList<SoundsModel> arrayList, FavouriteSoundAdapter.OnItemClickListener listener) {
        this.context = context;
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

            holder.soundName.setText(item.sound_name);
            holder.descriptionTxt.setText(item.description);
            holder.durationTimeTxt.setText(item.duration);

            holder.soundImage.setController(Functions.frescoImageLoad(item.getThum(),R.drawable.ractengle_solid_primary,holder.soundImage,false));

            holder.favBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_favourite));
            holder.bind(i, datalist.get(i), listener);


        } catch (Exception e) {

        }

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

        public void bind(final int pos, final SoundsModel item, final FavouriteSoundAdapter.OnItemClickListener listener) {

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

