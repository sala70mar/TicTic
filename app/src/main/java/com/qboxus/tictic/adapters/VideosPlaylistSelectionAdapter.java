package com.qboxus.tictic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.HomeSelectionModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

public class VideosPlaylistSelectionAdapter extends RecyclerView.Adapter<VideosPlaylistSelectionAdapter.CustomViewHolder> {

    public Context context;
    private ArrayList<HomeSelectionModel> dataList;

    AdapterClickListener adapterClickListener;

    public VideosPlaylistSelectionAdapter(Context context, ArrayList<HomeSelectionModel> dataList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.adapterClickListener = adapterClickListener;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_playlist_selection_layout, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView thumbImage;
        TextView viewTxt;
        ImageView ivSelection;

        public CustomViewHolder(View view) {
            super(view);
            ivSelection=view.findViewById(R.id.ivSelection);
            thumbImage = view.findViewById(R.id.thumb_image);
            viewTxt = view.findViewById(R.id.view_txt);

        }

        public void bind(final int position, final HomeModel item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> {
                adapterClickListener.onItemClick(v, position, item);

            });

        }

    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        final HomeSelectionModel itemSelected = dataList.get(i);
        HomeModel item=itemSelected.getModel();

        holder.thumbImage.setController(Functions.frescoImageLoad(item.getThum(),holder.thumbImage,false));

        if (itemSelected.isSelect())
        {
            holder.ivSelection.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ic_selection));
        }
        else
        {
            holder.ivSelection.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ic_un_selected));
        }

        holder.viewTxt.setText(item.views);
        holder.viewTxt.setText(Functions.getSuffix(item.views));


        holder.bind(i, item, adapterClickListener);

    }

}