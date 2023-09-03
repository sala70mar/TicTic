package com.qboxus.tictic.activitesfragments.spaces.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.activitesfragments.chat.ChatModel;
import com.qboxus.tictic.databinding.ItemRoomChatBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener2;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RoomChatAdapter extends RecyclerView.Adapter<RoomChatAdapter.ViewHolder> {
    private List<ChatModel> mDataSet;
    String myID;
    Integer todayDay = 0;

    AdapterClickListener2 adapterClickListener;

    public RoomChatAdapter(List<ChatModel> dataSet, String id, AdapterClickListener2 adapterClickListener) {
        mDataSet = dataSet;
        this.myID = id;
        this.adapterClickListener = adapterClickListener;
        Calendar cal = Calendar.getInstance();
        todayDay = cal.get(Calendar.DAY_OF_MONTH);

    }


    // this is the all types of view that is used in the chat
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {

        ItemRoomChatBinding binding= ItemRoomChatBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatModel chat = mDataSet.get(position);

        if (chat.getType().equals("text")) {
            holder.binding.ivProfile.setController(Functions.frescoImageLoad(holder.binding.getRoot().getContext(),
                    chat.getSender_name(),chat.getPic_url(),holder.binding.ivProfile));
            holder.binding.tvmessage.setText(chat.getText());
            holder.binding.datetxt.setText(changeDate(chat.getTimestamp()));


            holder.bind(position, adapterClickListener,chat);

        }


    }


    @Override
    public int getItemViewType(int position) {
        return  0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemRoomChatBinding binding;
        public ViewHolder(@NonNull ItemRoomChatBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int position, AdapterClickListener2 listener, Object object)
        {

            binding.mainTab.setOnClickListener(v -> listener.onItemClick(v,position,object));

        }
    }


    // change the date into (today ,yesterday and date)
    private String changeDate(String date) {

        try {
            long currenttime = System.currentTimeMillis();
            long databasedate = 0;
            Date d = null;
            try {
                d = Variables.df.parse(date);
                databasedate = d.getTime();

            } catch (Exception e) {
                e.printStackTrace();
            }
            long difference = currenttime - databasedate;
            if (difference < 86400000) {
                int chatday = Functions.parseInterger(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                if (todayDay == chatday)
                    return sdf.format(d);
                else if ((todayDay - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            } else if (difference < 172800000) {
                int chatday = Functions.parseInterger(date.substring(0, 2));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
                if ((todayDay - chatday) == 1)
                    return "Yesterday " + sdf.format(d);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy hh:mm a",Locale.ENGLISH);
            return sdf.format(d);
        } catch (Exception e) {
            return date;
        }

    }



}
