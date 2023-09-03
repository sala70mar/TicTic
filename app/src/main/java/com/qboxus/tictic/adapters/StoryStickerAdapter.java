package com.qboxus.tictic.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.databinding.StoryStickerItemViewBinding;

import java.util.ArrayList;

public class StoryStickerAdapter extends RecyclerView.Adapter<StoryStickerAdapter.CustomViewHolder> {
    ArrayList<String> list;
    private AdapterClickListener listener;


    public StoryStickerAdapter(ArrayList<String> datalist, AdapterClickListener listener) {
        this.list = datalist;
        this.listener = listener;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        StoryStickerItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.story_sticker_item_view, viewGroup,false);
        return new CustomViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        String item=list.get(i);

        holder.binding.ivGif.setController(Functions.frescoImageLoad(item
                ,R.drawable.ractengle_solid_lightblack,holder.binding.ivGif,false));

        holder.bind(i,item, listener);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        StoryStickerItemViewBinding binding;

        public CustomViewHolder(StoryStickerItemViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int position,String item,AdapterClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v,position,item);
            });


        }

    }
}