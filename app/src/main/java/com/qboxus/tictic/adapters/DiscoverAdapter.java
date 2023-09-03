package com.qboxus.tictic.adapters;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.models.DiscoverModel;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<DiscoverModel> datalist;
    ArrayList<DiscoverModel> datalistFilter;

    public interface OnItemClickListener {
        void onItemClick(View view, ArrayList<HomeModel> video_list, int main_position, int child_position);
    }

    public DiscoverAdapter.OnItemClickListener listener;

    public DiscoverAdapter(Context context, ArrayList<DiscoverModel> arrayList, DiscoverAdapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        datalistFilter = arrayList;
        this.listener = listener;
    }


    @Override
    public DiscoverAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_discover_layout, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return datalistFilter.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        RecyclerView horizontal_reycerview;

        TextView title, viewsTxt;
        RelativeLayout hashtagLayout;

        public CustomViewHolder(View view) {
            super(view);

            horizontal_reycerview = view.findViewById(R.id.horizontal_recylerview);
            title = view.findViewById(R.id.title);
            viewsTxt = view.findViewById(R.id.views_txt);

            hashtagLayout = view.findViewById(R.id.hashtag_layout);
        }

        public void bind(final int pos, final ArrayList<HomeModel> datalist) {
            hashtagLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, datalist, pos, -1);
                }
            });
        }


    }


    @Override
    public void onBindViewHolder(final DiscoverAdapter.CustomViewHolder holder, final int i) {

        DiscoverModel item = datalistFilter.get(i);

        holder.title.setText(item.title);
        holder.viewsTxt.setText(Functions.getSuffix(item.videos_count));

        HorizontalAdapter adapter = new HorizontalAdapter(context, i, item.arrayList);
        GridLayoutManager layoutManager=new GridLayoutManager(holder.itemView.getContext(),1);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.horizontal_reycerview.setLayoutManager(layoutManager);
        holder.horizontal_reycerview.setAdapter(adapter);

        holder.bind(i, item.arrayList);
    }


    class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.CustomViewHolder> {
        public Context context;

        ArrayList<HomeModel> datalist;
        int main_position;

        public HorizontalAdapter(Context context, int position, ArrayList<HomeModel> arrayList) {
            this.context = context;
            datalist = arrayList;
            this.main_position = position;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_discover_horizontal_layout, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {

            SimpleDraweeView video_thumbnail;
            TextView tab_more_txt;

            public CustomViewHolder(View view) {
                super(view);
                video_thumbnail = view.findViewById(R.id.video_thumbnail);
                tab_more_txt = view.findViewById(R.id.tab_more_txt);

            }

            public void bind(final int pos, final ArrayList<HomeModel> datalist) {
                itemView.setOnClickListener(v -> {
                    listener.onItemClick(itemView, datalist, main_position, pos);
                });
            }


        }

        @Override
        public void onBindViewHolder(final HorizontalAdapter.CustomViewHolder holder, final int i) {
            holder.setIsRecyclable(false);


            HomeModel item = datalist.get(i);
            holder.bind(i, datalist);

            if (item != null) {


                holder.tab_more_txt.setVisibility(View.GONE);

                try {

                    if (Constants.IS_SHOW_GIF) {

                        holder.video_thumbnail.setController(Functions.frescoImageLoad(item.getGif(),holder.video_thumbnail,true));

                    } else {
                        holder.video_thumbnail.setController(Functions.frescoImageLoad(item.getThum(),R.drawable.image_placeholder,holder.video_thumbnail,false));

                    }
                } catch (Exception e) {
                    Functions.printLog(Constants.tag, e.toString());
                }

            } else {

                holder.tab_more_txt.setVisibility(View.VISIBLE);
                holder.video_thumbnail.setVisibility(View.GONE);

            }


        }


    }


}