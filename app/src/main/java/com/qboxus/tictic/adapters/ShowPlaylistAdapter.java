package com.qboxus.tictic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.PlaylistHomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;

public class ShowPlaylistAdapter extends RecyclerView.Adapter<ShowPlaylistAdapter.CustomViewHolder> {

    ArrayList<PlaylistHomeModel> datalist;
    AdapterClickListener adapterClickListener;
    String userId;

    public ShowPlaylistAdapter(ArrayList<PlaylistHomeModel> arrayList,String userId, AdapterClickListener adapterClickListener) {
        this.datalist = arrayList;
        this.userId=userId;
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_playlist_item_view, viewGroup, false);
        return new CustomViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        PlaylistHomeModel itemModel = (PlaylistHomeModel) datalist.get(i);
        HomeModel item=itemModel.getModel();

        holder.image.setController(Functions.frescoImageLoad(item.getThum(),R.drawable.image_placeholder,holder.image,false));

        if (itemModel.isSelection())
        {
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.colorwhite_80));
        }
        else
        {
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
        }
        if (userId.equalsIgnoreCase(Functions.getSharedPreference(holder.itemView.getContext()).getString(Variables.U_ID,"")))
        {
            holder.ivOption.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivOption.setVisibility(View.GONE);
        }

        holder.tvDescription.setText(item.video_description);
        holder.tvViews.setText(Functions.getSuffix(item.views) + " "+holder.itemView.getContext().getString(R.string.views));
        holder.bind(i, item, adapterClickListener);

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView image;
        TextView tvDescription, tvViews;
        RelativeLayout mainlayout;
        ImageView ivOption;

        public CustomViewHolder(View view) {
            super(view);
            mainlayout=view.findViewById(R.id.mainlayout);
            image = view.findViewById(R.id.image);
            ivOption=view.findViewById(R.id.ivOption);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvViews = view.findViewById(R.id.tvViews);

        }

        public void bind(final int pos, final Object item, final AdapterClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

            ivOption.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

        }

    }


}

