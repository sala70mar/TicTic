package com.qboxus.tictic.activitesfragments.spaces.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.activitesfragments.spaces.models.RoomModel;
import com.qboxus.tictic.databinding.ItemRoomLayoutBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;

public class MainHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Object> datalist;
    AdapterClickListener mainlistener;
     private static final int typeRoom = 1;

     Context context;

    public MainHomeAdapter(Context context,ArrayList<Object> datalist, AdapterClickListener mainlistener) {
        this.context=context;
        this.datalist = datalist;
        this.mainlistener = mainlistener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = null;
        switch (viewType) {
            case typeRoom:
                ItemRoomLayoutBinding binding = ItemRoomLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ViewHolder(binding);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            ViewHolder mainHomeModel = (ViewHolder) holder;
            RoomModel item = (RoomModel) datalist.get(position);

            mainHomeModel.binding.tvDescription.setText("" + item.getTitle());

         //   mainHomeModel.binding.liveanimation.setController(Functions.frescoImageLoad(context.getDrawable(R.drawable.ic_live_gif), mainHomeModel.binding.liveanimation,true));


            mainHomeModel.binding.topicTxt.setText(item.getTopicModels().get(0).getTitle());

            int moderatorCount = 0;
            for (HomeUserModel userModel : item.getUserList()) {
                if (userModel.getUserRoleType().equals("1")) {
                    moderatorCount = moderatorCount + 1;
                    mainHomeModel.binding.adminimage.setController(Functions.frescoImageLoad(mainHomeModel.binding.getRoot().getContext(),
                            Functions.getUserName(userModel.getUserModel()),
                            userModel.getUserModel().getProfilePic(),
                            mainHomeModel.binding.adminimage));
                }
            }

            mainHomeModel.binding.tvModeratorCount.setText("" + moderatorCount);
            mainHomeModel.binding.tvMemberCount.setText("" + item.getUserList().size());

            if (item.getUserList().size() > 1) {
                mainHomeModel.binding.ivProfileOne.setVisibility(View.VISIBLE);
                mainHomeModel.binding.ivProfileSecond.setVisibility(View.VISIBLE);

                mainHomeModel.binding.ivProfileOne.setController(Functions.frescoImageLoad(mainHomeModel.binding.getRoot().getContext(),
                        Functions.getUserName(item.getUserList().get(0).getUserModel()),
                        item.getUserList().get(0).getUserModel().getProfilePic(),
                        mainHomeModel.binding.ivProfileOne));

                mainHomeModel.binding.ivProfileSecond.setController(Functions.frescoImageLoad(mainHomeModel.binding.getRoot().getContext(),
                        Functions.getUserName(item.getUserList().get(1).getUserModel()),
                        item.getUserList().get(1).getUserModel().getProfilePic(),
                        mainHomeModel.binding.ivProfileSecond));

            }

            else if (item.getUserList().size() > 0) {
                mainHomeModel.binding.ivProfileOne.setVisibility(View.VISIBLE);
                mainHomeModel.binding.ivProfileSecond.setVisibility(View.GONE);

                mainHomeModel.binding.ivProfileOne.setController(Functions.frescoImageLoad(mainHomeModel.binding.getRoot().getContext(),
                        Functions.getUserName(item.getUserList().get(0).getUserModel()),
                        item.getUserList().get(0).getUserModel().getProfilePic(),
                        mainHomeModel.binding.ivProfileOne));
            }

            else {
                mainHomeModel.binding.ivProfileOne.setVisibility(View.GONE);
                mainHomeModel.binding.ivProfileSecond.setVisibility(View.GONE);
            }

            mainHomeModel.bind(position,mainlistener,item);
        }


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (datalist.get(position) instanceof RoomModel) {
            return   typeRoom;
        }
        return   typeRoom;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemRoomLayoutBinding binding;
        public ViewHolder(@NonNull ItemRoomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int position, AdapterClickListener listener, Object object) {
            binding.tabView.setOnClickListener(v -> listener.onItemClick(v,position,object));

            binding.menuBtn.setOnClickListener(v -> listener.onItemClick(v,position,object));
        }

    }


}
