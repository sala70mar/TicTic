package com.qboxus.tictic.activitesfragments.spaces.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.databinding.CurrentSpeakerItemViewBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.simpleclasses.Functions;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;

import java.util.ArrayList;

public class CurrentSpeakerRoomAdapter extends RecyclerView.Adapter<CurrentSpeakerRoomAdapter.ViewHolder> {

    ArrayList<HomeUserModel> list;
    AdapterClickListener listener;

    public CurrentSpeakerRoomAdapter(ArrayList<HomeUserModel> list, AdapterClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CurrentSpeakerItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater
                .from(parent.getContext()), R.layout.current_speaker_item_view, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HomeUserModel item=list.get(position);

        holder.binding.tvUsername.setText(item.getUserModel().getUsername());

        holder.binding.ivProfile.setController(Functions.frescoImageLoad(holder.binding.getRoot().getContext(),
                Functions.getUserName(item.getUserModel()),item.getUserModel().getProfilePic(),holder.binding.ivProfile));


        if (item.getUserRoleType().equals("1"))
        {
            holder.binding.ivModerator.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.binding.ivModerator.setVisibility(View.GONE);
        }

        if (item.getUserRoleType().equals("1"))
        {
            if (item.getMice().equals("1"))
            {
                holder.binding.ivMuteMice.setVisibility(View.GONE);

            }
            else
            {
                holder.binding.ivMuteMice.setVisibility(View.VISIBLE);
            }

            holder.binding.ivRiseHand.setVisibility(View.GONE);
        }
        else
            if (item.getUserRoleType().equals("2"))
            {
                if (item.getMice().equals("1"))
                {
                    holder.binding.ivMuteMice.setVisibility(View.GONE);

                }
                else
                {
                    holder.binding.ivMuteMice.setVisibility(View.VISIBLE);
                }

                holder.binding.ivRiseHand.setVisibility(View.GONE);
            }
            else
            {
                holder.binding.ivMuteMice.setVisibility(View.GONE);

                if (item.getRiseHand().equals("1"))
                {
                    holder.binding.ivRiseHand.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.binding.ivRiseHand.setVisibility(View.GONE);
                }
            }


        holder.bind(position,listener,item);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CurrentSpeakerItemViewBinding binding;

        public ViewHolder(@NonNull CurrentSpeakerItemViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int position, AdapterClickListener listener, Object object)
        {
            binding.tabMain.setOnClickListener(v -> listener.onItemClick(v,position,object));
            ClickShrinkUtils.applyClickShrink(binding.tabMain);
        }
    }
}
