package com.qboxus.tictic.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class VideosListAdapter extends RecyclerView.Adapter<VideosListAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<HomeModel> datalist;
    AdapterClickListener adapterClickListener;

    public VideosListAdapter(Context context, ArrayList<HomeModel> arrayList, AdapterClickListener adapterClickListener) {
        this.context = context;
        datalist = arrayList;
        this.adapterClickListener = adapterClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_video_layout, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView image, userImage;

        TextView usernameTxt, descriptionTxt, firstLastNameTxt, likesCountTxt;


        public CustomViewHolder(View view) {
            super(view);
            userImage = view.findViewById(R.id.user_image);
            image = view.findViewById(R.id.image);
            usernameTxt = view.findViewById(R.id.username_txt);
            descriptionTxt = view.findViewById(R.id.description_txt);

            firstLastNameTxt = view.findViewById(R.id.first_last_name_txt);
            likesCountTxt = view.findViewById(R.id.likes_count_txt);
        }

        public void bind(final int pos, final HomeModel item, final AdapterClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);

            });


        }


    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        final HomeModel item = (HomeModel) datalist.get(i);

        holder.usernameTxt.setText(item.username);
        holder.descriptionTxt.setText(item.video_description);

        Uri uriThum = Uri.parse(item.getThum());
        holder.image.setImageURI(uriThum);

        Uri uriProfile = Uri.parse(item.getProfile_pic());
        holder.userImage.setImageURI(uriProfile);

        holder.firstLastNameTxt.setText(item.first_name + " " + item.last_name);
        holder.likesCountTxt.setText(Functions.getSuffix(item.like_count));

        holder.bind(i, item, adapterClickListener);

    }

}