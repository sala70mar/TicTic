package com.qboxus.tictic.activitesfragments.spaces.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.activitesfragments.spaces.models.UserSuggestionModel;
import com.qboxus.tictic.databinding.UserProfileSuggestionItemViewBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

public class ProfileSuggestionAdapter extends RecyclerView.Adapter<ProfileSuggestionAdapter.ViewHolder> {

    ArrayList<UserSuggestionModel> list;
    AdapterClickListener listener;

    public ProfileSuggestionAdapter(ArrayList<UserSuggestionModel> list, AdapterClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserProfileSuggestionItemViewBinding binding = UserProfileSuggestionItemViewBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserSuggestionModel item=list.get(position);

        holder.binding.ivProfile.setController(Functions.frescoImageLoad(holder.binding.getRoot().getContext(),
                Functions.getUserName(item.getUserModel()),item.getUserModel().getProfilePic(),holder.binding.ivProfile));

        holder.binding.tvFullName.setText(item.getUserModel().getFirstName()+" "+item.getUserModel().getLastName());
        holder.binding.tvBio.setText(item.getUserModel().getBio());
        holder.binding.tvFollow.setText(Functions.getButtonStatus(item.getUserModel().getButton()));


        holder.bind(position,listener,item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        UserProfileSuggestionItemViewBinding binding;
        public ViewHolder(@NonNull UserProfileSuggestionItemViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int position, AdapterClickListener listener, Object object)
        {
            binding.tabFollow.setOnClickListener(v -> listener.onItemClick(v,position,object));
            binding.tabProfile.setOnClickListener(v -> listener.onItemClick(v,position,object));
            binding.tabRemove.setOnClickListener(v -> listener.onItemClick(v,position,object));
        }
    }
}
