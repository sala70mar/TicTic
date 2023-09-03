package com.qboxus.tictic.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qboxus.tictic.models.NotificationModel;
import com.qboxus.tictic.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;

/**
 * Created by qboxus on 3/20/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.CustomViewHolder> {
    public Context context;

    ArrayList<NotificationModel> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, NotificationModel item);
    }

    public NotificationAdapter.OnItemClickListener listener;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> arrayList, NotificationAdapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }

    @Override
    public NotificationAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification, viewGroup, false);
        return new CustomViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView userImage;
        RelativeLayout rightView;

        TextView username, message, watchBtn, followBtn,btnAcceptRequest,btnDeleteRequest,tvTime;

        public CustomViewHolder(View view) {
            super(view);
            rightView=view.findViewById(R.id.rightView);
            userImage = view.findViewById(R.id.user_image);
            username = view.findViewById(R.id.username);
            message = view.findViewById(R.id.message);
            watchBtn = view.findViewById(R.id.watch_btn);
            btnAcceptRequest = view.findViewById(R.id.btnAcceptRequest);
            btnDeleteRequest = view.findViewById(R.id.btnDeleteRequest);
            followBtn = view.findViewById(R.id.follow_btn);
            tvTime=view.findViewById(R.id.tvTime);

        }

        public void bind(final int pos, final NotificationModel item, final NotificationAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

            watchBtn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

            btnAcceptRequest.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

            btnDeleteRequest.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });


            followBtn.setOnClickListener(v -> {
                listener.onItemClick(v, pos, item);
            });

        }


    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        final NotificationModel item = datalist.get(i);
        holder.username.setText(item.username);
        holder.userImage.setController(Functions.frescoImageLoad(item.profile_pic,holder.userImage,false));

        if (Functions.getSharedPreference(holder.itemView.getContext())
                .getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE).equals("en"))
        {
            holder.message.setText(""+item.string);
        }
        else
        {
            String message=""+item.string;

            String messageVideoLikeEn=item.username+" "+Variables.liked_your_video_en;
            String messageVideoLikeAr=item.username+" "+Variables.liked_your_video_ar;
            message=message.replace(messageVideoLikeEn,messageVideoLikeAr);


            String messageVideoPostEn=item.username+" "+Variables.has_posted_a_video_en;
            String messageVideoPostAr=item.username+" "+Variables.has_posted_a_video_ar;
            message=message.replace(messageVideoPostEn,messageVideoPostAr);


            String messageEn=item.username+" "+Variables.started_following_you_en;
            String messageAr=item.username+" "+Variables.started_following_you_ar;
            message=message.replace(messageEn,messageAr);


            String messageFollowEn=item.username+" "+Variables.started_following_you_en;
            String messageFollowAr=item.username+" "+Variables.started_following_you_ar;
            message=message.replace(messageFollowEn,messageFollowAr);


            String messageLiveEn=item.username+" "+Variables.is_live_now_en;
            String messageLiveAr=item.username+" "+Variables.is_live_now_ar;
            message=message.replace(messageLiveEn,messageLiveAr);


            String messageTagEn=item.username+" "+Variables.mentioned_you_in_a_comment_en;
            String messageTagAr=item.username+" "+Variables.mentioned_you_in_a_comment_ar;
            message=message.replace(messageTagEn,messageTagAr);


            String messageRepliedEn=item.username+" "+Variables.replied_to_your_comment_en;
            String messageRepliedAr=item.username+" "+Variables.replied_to_your_comment_ar;
            message=message.replace(messageRepliedEn,messageRepliedAr);

            String messageCommentEn=item.username+" "+Variables.commented_en;
            String messageCommentAr=item.username+" "+Variables.commented_ar;
            message=message.replace(messageCommentEn,messageCommentAr);

            holder.message.setText(message);

        }

        if (item.type.equalsIgnoreCase("video_comment")) {
            holder.rightView.setVisibility(View.VISIBLE);
            holder.watchBtn.setVisibility(View.VISIBLE);
            holder.watchBtn.setText(context.getString(R.string.reply));
            holder.followBtn.setVisibility(View.GONE);
            holder.btnDeleteRequest.setVisibility(View.GONE);
            holder.btnAcceptRequest.setVisibility(View.GONE);
        } else if (item.type.equalsIgnoreCase("video_like")) {
            holder.rightView.setVisibility(View.VISIBLE);
            holder.watchBtn.setVisibility(View.VISIBLE);
            holder.watchBtn.setText(context.getString(R.string.watch));
            holder.followBtn.setVisibility(View.GONE);
            holder.btnDeleteRequest.setVisibility(View.GONE);
            holder.btnAcceptRequest.setVisibility(View.GONE);
        } else if (item.type.equalsIgnoreCase("group_invite") || item.type.equalsIgnoreCase("single") || item.type.equalsIgnoreCase("multiple")) {

            if (item.status.equalsIgnoreCase("0"))
            {
                holder.rightView.setVisibility(View.VISIBLE);
                holder.watchBtn.setVisibility(View.GONE);
                holder.followBtn.setVisibility(View.GONE);
                holder.btnDeleteRequest.setVisibility(View.VISIBLE);
                holder.btnAcceptRequest.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.rightView.setVisibility(View.GONE);
            }
        }
        else
        if (item.type.equalsIgnoreCase("following"))
        {
            holder.rightView.setVisibility(View.VISIBLE);
            holder.watchBtn.setVisibility(View.GONE);

            holder.btnDeleteRequest.setVisibility(View.GONE);
            holder.btnAcceptRequest.setVisibility(View.GONE);

            if (item.user_id.equals(item.effected_fb_id))
            {
                holder.followBtn.setVisibility(View.GONE);
            }
            else
            {
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setText(item.button);

                if (item.button != null &&
                        (item.button.equalsIgnoreCase("follow") || item.button.equalsIgnoreCase("follow back"))) {

                    holder.followBtn.setVisibility(View.VISIBLE);
                    holder.followBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_rounded_background));
                    holder.followBtn.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));

                } else if (item.button != null &&
                        (item.button.equalsIgnoreCase("following") || item.button.equalsIgnoreCase("friends"))) {
                    holder.followBtn.setVisibility(View.GONE);

                } else if (item.button != null && item.button.equalsIgnoreCase("0")) {
                    holder.followBtn.setVisibility(View.GONE);
                }
            }

        }
        else
        {
            holder.rightView.setVisibility(View.GONE);
        }
        String date=Functions.changeDateLatterFormat("yyyy-MM-dd HH:mm:ssZZ",context, item.created+"+0000");

        holder.tvTime.setText(""+date);


        holder.bind(i, datalist.get(i), listener);

    }


}