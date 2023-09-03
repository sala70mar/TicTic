package com.qboxus.tictic.adapters;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.models.InboxModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by qboxus on 3/20/2018.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.CustomViewHolder> {
    public Context context;
    ArrayList<InboxModel> inboxDataList = new ArrayList<>();
    ArrayList<InboxModel> inboxDataListFilter = new ArrayList<>();
    private OnItemClickListener listener;
    private OnLongItemClickListener longlistener;

    Integer today_day = 0;

    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(InboxModel item);
    }

    public interface OnLongItemClickListener {
        void onLongItemClick(InboxModel item);
    }

    public InboxAdapter(Context context, ArrayList<InboxModel> user_dataList, OnItemClickListener listener, InboxAdapter.OnLongItemClickListener longlistener) {
        this.context = context;
        this.inboxDataList = user_dataList;
        this.inboxDataListFilter = user_dataList;
        this.listener = listener;
        this.longlistener = longlistener;

        // get the today as a integer number to make the dicision the chat date is today or yesterday
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inbox_list, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return inboxDataListFilter.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView username, lastMessage, dateCreated;
        SimpleDraweeView userImage;

        public CustomViewHolder(View view) {
            super(view);
            userImage = itemView.findViewById(R.id.user_image);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.message);
            dateCreated = itemView.findViewById(R.id.datetxt);
        }

        public void bind(final InboxModel item, final InboxAdapter.OnItemClickListener listener, final InboxAdapter.OnLongItemClickListener longItemClickListener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(item);

            });


        }

    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {

        final InboxModel item = inboxDataListFilter.get(i);
        holder.username.setText(item.getName());
        holder.lastMessage.setText(item.getMsg());
        holder.dateCreated.setText(Functions.changeDateTodayYesterday(context, item.getDate()));

        if (item.getPic() != null && !item.getPic().equals("")) {

            holder.userImage.setController(Functions.frescoImageLoad(item.getPic(),holder.userImage,false));

        }

        String status = "" + item.getStatus();
        if (status.equals("0")) {
            holder.lastMessage.setTypeface(null, Typeface.BOLD);
            holder.lastMessage.setTextColor(ContextCompat.getColor(context,R.color.black));
        } else {
            holder.lastMessage.setTypeface(null, Typeface.NORMAL);
            holder.lastMessage.setTextColor(ContextCompat.getColor(context,R.color.darkgray));
        }


        holder.bind(item, listener, longlistener);

    }




}