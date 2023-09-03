package com.qboxus.tictic.activitesfragments.spaces.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.FriendsSelectionItemViewBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

public class FriendsSelectionAdapter extends RecyclerView.Adapter<FriendsSelectionAdapter.ViewHolder> {

    ArrayList<UserModel> list;
    AdapterClickListener listener;

    public FriendsSelectionAdapter(ArrayList<UserModel> list, AdapterClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FriendsSelectionItemViewBinding binding = FriendsSelectionItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserModel item=list.get(position);
        holder.binding.ivProfile.setController(Functions.frescoImageLoad(holder.binding.getRoot().getContext(),
                item.getUsername(),item.getProfilePic(),holder.binding.ivProfile));
        if (item.isSelected())
        {
            holder.binding.tabProfile.setAlpha(0.3f);
            holder.binding.ivSelect.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.binding.tabProfile.setAlpha(1f);
            holder.binding.ivSelect.setVisibility(View.GONE);
        }

        if (item.getOnline().equals("1"))
        {
            holder.binding.ivOnline.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.d_online_circle_green));
        }
        else
        {
            holder.binding.ivOnline.setImageDrawable(ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.drawable.d_offline_circle_green));
        }

        holder.bind(position,listener,item);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        FriendsSelectionItemViewBinding binding ;

        public ViewHolder(@NonNull FriendsSelectionItemViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int position, AdapterClickListener listener, Object object)
        {
            itemView.setOnClickListener(v -> listener.onItemClick(v,position,object));
        }


    }
}
