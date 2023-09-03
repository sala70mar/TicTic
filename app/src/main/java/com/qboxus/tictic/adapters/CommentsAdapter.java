package com.qboxus.tictic.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.CommentModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.FriendsTagHelper;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CustomViewHolder> {

    public Context context;
    FragmentCallBack callBack;
    public OnItemClickListener listener;
    public CommentsAdapter.onRelyItemCLickListener onRelyItemCLickListener;
    LinkClickListener linkClickListener;
    private ArrayList<CommentModel> dataList;
    public Comments_Reply_Adapter commentsReplyAdapter;


    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item

    public interface LinkClickListener {

        void onLinkClicked(SocialView view, String matchedText);
    }


    public interface OnItemClickListener {
        void onItemClick(int positon, CommentModel item, View view);
        void onItemLongPress(int positon, CommentModel item, View view);
    }

    public interface onRelyItemCLickListener {
        void onItemClick(ArrayList<CommentModel> arrayList, int postion, View view);
        void onItemLongPress(ArrayList<CommentModel> arrayList, int postion, View view);
    }


    public CommentsAdapter(Context context, ArrayList<CommentModel> dataList, OnItemClickListener listener, CommentsAdapter.onRelyItemCLickListener onRelyItemCLickListener, LinkClickListener linkClickListener,FragmentCallBack callBack) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
        this.linkClickListener = linkClickListener;
        this.onRelyItemCLickListener = onRelyItemCLickListener;
        this.callBack=callBack;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_layout, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    @Override
    public void onBindViewHolder( CustomViewHolder holder,  int i) {
         CommentModel item = dataList.get(i);

        holder.username.setText(item.user_name);


        holder.userPic.setController(Functions.frescoImageLoad(item.getProfile_pic(),holder.userPic,false));


        if (item.liked != null && !item.equals("")) {
            if (item.liked.equals("1")) {
                holder.likeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_like_fill));
            } else {
                holder.likeImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart_gray_out));
            }
        }

        if (item.isVerified.equals("1"))
        {
            holder.ivVarified.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivVarified.setVisibility(View.GONE);
        }

        holder.likeTxt.setText(Functions.getSuffix(item.like_count));
        String date=Functions.changeDateLatterFormat("yyyy-MM-dd hh:mm:ssZZ",context, item.created+"+0000");
        holder.tvMessageData.setText(""+date);
        holder.message.setText(""+item.comments);
        FriendsTagHelper.Creator.create(ContextCompat.getColor(holder.itemView.getContext(),R.color.whiteColor),ContextCompat.getColor(holder.itemView.getContext(),R.color.appColor), new FriendsTagHelper.OnFriendsTagClickListener() {
            @Override
            public void onFriendsTagClicked(String friendsTag) {
                if (friendsTag.contains("@"))
                {
                    Log.d(Constants.tag,"Friends "+friendsTag);
                    if (friendsTag.charAt(0)=='@')
                    {
                        friendsTag=friendsTag.substring(1);
                        openUserProfile(friendsTag);
                    }
                }

            }
        }).handle(holder.message);


        if (item.isExpand) {
            holder.lessLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.lessLayout.setVisibility(View.GONE);
        }

        if (item.arrayList != null && item.arrayList.size() > 0) {
            holder.replyCount.setVisibility(View.VISIBLE);
            holder.replyCount.setText(context.getString(R.string.view_replies)+" (" + item.arrayList.size() + ")");
        } else {
            holder.replyCount.setVisibility(View.GONE);
        }
        if (item.userId.equals(item.videoOwnerId))
        {
            holder.tabCreator.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.tabCreator.setVisibility(View.GONE);
        }

        if (item.pin_comment_id.equals(item.comment_id))
        {
            holder.tabPinned.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.tabPinned.setVisibility(View.GONE);
        }

        if (item.isLikedByOwner.equals("1"))
        {
            holder.tabLikedByCreator.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.tabLikedByCreator.setVisibility(View.GONE);
        }

        commentsReplyAdapter = new Comments_Reply_Adapter(context, item.arrayList);
        holder.replyRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.replyRecyclerView.setAdapter(commentsReplyAdapter);
        holder.replyRecyclerView.setHasFixedSize(false);
        holder.bind(i, item, listener);

    }

    private void openUserProfile(String friendsTag) {
        Bundle bundle=new Bundle();
        bundle.putBoolean("isShow",true);
        bundle.putString("name",friendsTag);
        callBack.onResponce(bundle);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView username, message, replyCount, likeTxt, showLessTxt,tvMessageData;
        SimpleDraweeView userPic;
        FrameLayout tabUserPic;
        ImageView likeImage,ivVarified;
        LinearLayout messageLayout, lessLayout, likeLayout,tabCreator,tabMessageReply,tabPinned,tabLikedByCreator;
        RecyclerView replyRecyclerView;

        public CustomViewHolder(View view) {
            super(view);
            ivVarified=view.findViewById(R.id.ivVarified);
            tabLikedByCreator=view.findViewById(R.id.tabLikedByCreator);
            tvMessageData=view.findViewById(R.id.tvMessageData);
            tabMessageReply=view.findViewById(R.id.tabMessageReply);
            tabPinned=view.findViewById(R.id.tabPinned);
            tabUserPic=view.findViewById(R.id.tabUserPic);
            username = view.findViewById(R.id.username);
            userPic = view.findViewById(R.id.user_pic);
            message = view.findViewById(R.id.message);
            replyCount = view.findViewById(R.id.reply_count);
            likeImage = view.findViewById(R.id.like_image);
            messageLayout = view.findViewById(R.id.message_layout);
            likeTxt = view.findViewById(R.id.like_txt);
            tabCreator=view.findViewById(R.id.tabCreator);
            replyRecyclerView = view.findViewById(R.id.reply_recycler_view);
            lessLayout = view.findViewById(R.id.less_layout);
            showLessTxt = view.findViewById(R.id.show_less_txt);
            likeLayout = view.findViewById(R.id.like_layout);
        }

        public void bind( int postion,  CommentModel item,  OnItemClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });
            tabUserPic.setOnClickListener(view -> {
                listener.onItemClick(postion, item, view);
            });
            userPic.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });
            username.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });

            messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongPress(postion,item,view);
                    return false;
                }
            });

            likeLayout.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });
            tabMessageReply.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });
            replyCount.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });
            showLessTxt.setOnClickListener(v -> {
                listener.onItemClick(postion, item, v);
            });



        }


    }


    public class Comments_Reply_Adapter extends RecyclerView.Adapter<Comments_Reply_Adapter.CustomViewHolder> {

        public Context context;
        private ArrayList<CommentModel> dataList;

        public Comments_Reply_Adapter(Context context, ArrayList<CommentModel> dataList) {
            this.context = context;
            this.dataList = dataList;

        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_reply_layout, viewGroup,false);
            return  new CustomViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }


        @Override
        public void onBindViewHolder( CustomViewHolder holder,  int i) {
             CommentModel item = dataList.get(i);
            holder.username.setText(item.replay_user_name);

            holder.user_pic.setController(Functions.frescoImageLoad(item.replay_user_url,holder.user_pic,false));

            holder.message.setText(item.comment_reply);
            holder.tvMessageData.setText(""+Functions.changeDateToTimebase(item.created));


            if (item.comment_reply_liked != null && !item.comment_reply_liked.equals("")) {
                if (item.comment_reply_liked.equals("1")) {
                    holder.reply_like_image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_like_fill));
                } else {
                    holder.reply_like_image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart_gray_out));
                }
            }


            if (item.userId.equals(item.videoOwnerId))
            {
                holder.tabCreator.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.tabCreator.setVisibility(View.GONE);
            }

            if (item.isLikedByOwner.equals("1"))
            {
                holder.tabLikedByCreator.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.tabLikedByCreator.setVisibility(View.GONE);
            }

            if (item.isVerified.equals("1"))
            {
                holder.ivVarified.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.ivVarified.setVisibility(View.GONE);
            }

            holder.like_txt.setText(Functions.getSuffix(item.reply_liked_count));

            holder.message.setOnMentionClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                    linkClickListener.onLinkClicked(view, text.toString());
                }
            });


            holder.bind(i, dataList, onRelyItemCLickListener);

        }


        class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView username, like_txt,tvMessageData;
            SocialTextView message;
            SimpleDraweeView user_pic;
            ImageView reply_like_image,ivVarified;
            LinearLayout reply_layout, like_layout,tabLikedByCreator,tabMessageReply,tabCreator;


            public CustomViewHolder(View view) {
                super(view);
                ivVarified=view.findViewById(R.id.ivVarified);
                tvMessageData=view.findViewById(R.id.tvMessageData);
                tabMessageReply=view.findViewById(R.id.tabMessageReply);
                tabLikedByCreator=view.findViewById(R.id.tabLikedByCreator);
                username = view.findViewById(R.id.username);
                user_pic = view.findViewById(R.id.user_pic);
                tabCreator=view.findViewById(R.id.tabCreator);
                message = view.findViewById(R.id.message);
                reply_layout = view.findViewById(R.id.reply_layout);
                reply_like_image = view.findViewById(R.id.reply_like_image);
                like_layout = view.findViewById(R.id.like_layout);
                like_txt = view.findViewById(R.id.like_txt);
            }

            public void bind( int postion, ArrayList<CommentModel> datalist,  CommentsAdapter.onRelyItemCLickListener listener) {

                itemView.setOnClickListener(v -> {
                    CommentsAdapter.this.onRelyItemCLickListener.onItemClick(datalist, postion, v);
                });

                user_pic.setOnClickListener(v -> {
                    CommentsAdapter.this.onRelyItemCLickListener.onItemClick(datalist, postion, v);
                });

                username.setOnClickListener(v -> {
                    CommentsAdapter.this.onRelyItemCLickListener.onItemClick(datalist, postion, v);
                });

                tabMessageReply.setOnClickListener(v -> {
                    CommentsAdapter.this.onRelyItemCLickListener.onItemClick(datalist, postion, v);
                });

                like_layout.setOnClickListener(v -> {
                    CommentsAdapter.this.onRelyItemCLickListener.onItemClick(datalist, postion, v);
                });
                reply_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        CommentsAdapter.this.onRelyItemCLickListener.onItemLongPress(datalist, postion, view);
                        return false;
                    }
                });
            }
        }
    }


}