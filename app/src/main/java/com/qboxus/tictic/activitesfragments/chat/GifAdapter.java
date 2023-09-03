package com.qboxus.tictic.activitesfragments.chat;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.CustomViewHolder> {
    public Context context;
    ArrayList<String> gifList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public GifAdapter(Context context, ArrayList<String> datalist, OnItemClickListener listener) {
        this.context = context;
        this.gifList = datalist;
        this.listener = listener;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gif_layout, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return gifList.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView gifImage;

        public CustomViewHolder(View view) {
            super(view);
            gifImage = view.findViewById(R.id.gif_image);
        }

        public void bind(final String item, final GifAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(v -> {
                    listener.onItemClick(item);

            });


        }

    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        holder.bind(gifList.get(i), listener);


        // show the gif images by fresco
         String url=Variables.GIF_FIRSTPART + gifList.get(i) + Variables.GIF_SECONDPART;
        holder.gifImage.setController(Functions.frescoGifLoad(url,R.drawable.ractengle_solid_lightblack,holder.gifImage));

//        Functions.printLog(Constants.tag, Variables.GIF_FIRSTPART + gifList.get(i) + Variables.GIF_SECONDPART);
    }


}