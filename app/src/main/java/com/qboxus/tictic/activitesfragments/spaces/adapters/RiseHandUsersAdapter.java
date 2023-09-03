package com.qboxus.tictic.activitesfragments.spaces.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.databinding.RiseHandUserItemViewBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.simpleclasses.Functions;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;

import java.util.ArrayList;

public class RiseHandUsersAdapter extends RecyclerView.Adapter<RiseHandUsersAdapter.ViewHolder> {

    ArrayList<HomeUserModel> list;
    AdapterClickListener listener;

    public RiseHandUsersAdapter(ArrayList<HomeUserModel> list, AdapterClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RiseHandUserItemViewBinding binding = RiseHandUserItemViewBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeUserModel item=list.get(position);

        holder.binding.tvName.setText(item.getUserModel().getFirstName()+" "+item.getUserModel().getLastName());

        holder.binding.ivProfile.setController(Functions.frescoImageLoad(holder.binding.getRoot().getContext(),
                Functions.getUserName(item.getUserModel()),item.getUserModel().getProfilePic(),holder.binding.ivProfile));


        if (item.getRiseHand().equals("2")) {
            holder.binding.tabAddToSpeak.setBackground(ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.d_round_gray25));
            holder.binding.ivAdd.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(),R.drawable.ic_tick));
            holder.binding.ivAdd.setColorFilter(ContextCompat.getColor(holder.binding.getRoot().getContext(),R.color.appColor), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.binding.ivMice.setColorFilter(ContextCompat.getColor(holder.binding.getRoot().getContext(),R.color.appColor), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else {
            holder.binding.tabAddToSpeak.setBackground(ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.button_rounded_background));
            holder.binding.ivAdd.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(),R.drawable.ic_add));
            holder.binding.ivAdd.setColorFilter(ContextCompat.getColor(holder.binding.getRoot().getContext(),R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.binding.ivMice.setColorFilter(ContextCompat.getColor(holder.binding.getRoot().getContext(),R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        holder.bind(position,listener,item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RiseHandUserItemViewBinding binding;
        public ViewHolder(@NonNull RiseHandUserItemViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int position, AdapterClickListener listener, Object object)
        {
            binding.tabAddToSpeak.setOnClickListener(v -> listener.onItemClick(v,position,object));
            ClickShrinkUtils.applyClickShrink(binding.tabAddToSpeak);

            binding.ivProfile.setOnClickListener(v -> listener.onItemClick(v,position,object));
            ClickShrinkUtils.applyClickShrink(binding.ivProfile);
        }
    }
}
