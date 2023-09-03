package com.qboxus.tictic.activitesfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.qboxus.tictic.activitesfragments.profile.ProfileA;
import com.qboxus.tictic.adapters.CommentsAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.interfaces.FragmentDataSend;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.models.CommentModel;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.models.UsersModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.DebounceClickHandler;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentF extends BottomSheetDialogFragment {

    View view;
    Context context;

    RecyclerView recyclerView;
    CommentsAdapter adapter;
    ImageView send_btn;
    ProgressBar send_progress;
    ArrayList<CommentModel> dataList= new ArrayList<>();
    HomeModel item;
    String videoId;
    String userId;
    ProgressBar noDataLoader;
    TextView commentCountTxt, tvNoCommentData,tvComment;
    RelativeLayout send_btn_layout;
    boolean isSendAllow=true;
    String replyStatus = null;
    private static int commentCount = 0;
    CommentModel selectedComment=null;
    int selectedCommentPosition;
    CommentModel selectedReplyComment=null;
    int selectedReplyCommentPosition;

    int pageCount = 0;
    boolean ispostFinsh;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;


    private BottomSheetBehavior mBehavior;
    BottomSheetDialog dialog;

    public CommentF() {

    }


    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_comment, null);
        dialog.setContentView(view);

        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setHideable(false);
        mBehavior.setDraggable(false);
        mBehavior.setPeekHeight((int) view.getContext().getResources().getDimension(R.dimen._450sdp),true);
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState!=BottomSheetBehavior.STATE_EXPANDED)
                {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        return  dialog;
    }


    FragmentDataSend fragmentDataSend;
    RelativeLayout write_layout;


    @SuppressLint("ValidFragment")
    public CommentF(int count, FragmentDataSend fragmentDataSend ) {
        commentCount = count;
        this.fragmentDataSend = fragmentDataSend;


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_comment, container, false);
        context = view.getContext();
        write_layout = view.findViewById(R.id.write_layout);

        tvNoCommentData = view.findViewById(R.id.tvNoCommentData);
        send_btn=view.findViewById(R.id.send_btn);
        send_progress=view.findViewById(R.id.send_progress);
        tvComment=view.findViewById(R.id.tvComment);
        send_btn_layout=view.findViewById(R.id.send_btn_layout);
        tvComment.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyStatus=null;
                hitComment();
            }
        }));
        send_btn_layout.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyStatus=null;
                hitComment();
            }
        }));
        view.findViewById(R.id.goBack).setOnClickListener(new DebounceClickHandler(v -> {
            dismiss();
        }));


        Bundle bundle = getArguments();
        if (bundle != null) {
            videoId = bundle.getString("video_id");
            userId = bundle.getString("user_id");
            item = (HomeModel) bundle.getSerializable("data");
        }

        if (Functions.isShowContentPrivacy(context, item.apply_privacy_model.getVideo_comment(), item.follow_status_button.equalsIgnoreCase("friends")))
        {
            send_btn.setVisibility(View.VISIBLE);
            isSendAllow=true;
        } else
        {
            send_btn.setVisibility(View.GONE);
            isSendAllow=false;
        }

        commentCountTxt = view.findViewById(R.id.comment_count);


        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        recyclerView = view.findViewById(R.id.recylerview);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new CommentsAdapter(context, dataList, new CommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int positon, CommentModel itemUpdate, View view) {
                selectedCommentPosition = positon;
                selectedComment = dataList.get(selectedCommentPosition);


                switch (view.getId()) {

                    case R.id.tabUserPic:
                    case R.id.user_pic:
                    case R.id.username: {
                        openProfile(selectedComment);
                    }
                    break;

                    case R.id.tabMessageReply: {
                        if (Functions.checkLoginUser(getActivity())) {
                            replyStatus = "reply";
                            selectedReplyComment = null;
                            hitComment();
                        }

                    }
                    break;

                    case R.id.like_layout: {
                        if (Functions.checkLoginUser(getActivity())) {
                            likeComment(selectedCommentPosition, selectedComment);
                        }
                    }
                    break;
                    case R.id.reply_count: {
                        if (selectedComment.isExpand) {
                            selectedComment.isExpand = false;
                        } else {
                            selectedComment.isExpand = true;
                        }
                        dataList.set(selectedCommentPosition, selectedComment);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                    case R.id.show_less_txt: {
                        selectedComment.isExpand = false;
                        dataList.set(selectedCommentPosition, selectedComment);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                }
            }

            @Override
            public void onItemLongPress(int positon, CommentModel itemUpdate, View view) {
                selectedCommentPosition = positon;
                selectedComment = dataList.get(selectedCommentPosition);

                switch (view.getId()) {

                    case R.id.message_layout:
                        openCommentSetting(selectedComment, selectedCommentPosition);
                        break;

                }
            }
        }, new CommentsAdapter.onRelyItemCLickListener() {
            @Override
            public void onItemClick(ArrayList<CommentModel> arrayList, int postion, View view) {
                selectedReplyCommentPosition = postion;
                selectedReplyComment = arrayList.get(selectedReplyCommentPosition);

                switch (view.getId()) {

                    case R.id.user_pic:
                    case R.id.username:
                        openProfile(arrayList.get(selectedReplyCommentPosition));

                        break;

                    case R.id.tabMessageReply:
                        replyStatus = "commentReply";
                        hitComment();
                        break;


                    case R.id.like_layout:
                        if (Functions.checkLoginUser(getActivity())) {
                            likeCommentReply();
                        }
                        break;
                }
            }

            @Override
            public void onItemLongPress(ArrayList<CommentModel> arrayList, int postion, View view) {
                selectedReplyCommentPosition = postion;
                selectedReplyComment = arrayList.get(selectedReplyCommentPosition);
                switch (view.getId()) {

                    case R.id.reply_layout: {
                        Functions.copyCode(view.getContext(), selectedReplyComment.comment_reply);
                    }
                    break;

                }
            }
        }, new CommentsAdapter.LinkClickListener() {
            @Override
            public void onLinkClicked(SocialView view, String matchedText) {
                openProfileByUsername(matchedText);
            }
        }, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow"))
                {
                    openTagUser(bundle.getString("name"));
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = linearLayoutManager.findLastVisibleItemPosition();

                if (userScrolled && (scrollOutitems == dataList.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        getAllComments();
                    }
                }


            }
        });
        noDataLoader=view.findViewById(R.id.noDataLoader);




        if (item.apply_privacy_model.getVideo_comment().equalsIgnoreCase("everyone") ||
                (item.apply_privacy_model.getVideo_comment().equalsIgnoreCase("friend") &&
                        item.follow_status_button.equalsIgnoreCase("friends")))
        {
            write_layout.setVisibility(View.VISIBLE);
            getAllComments();
        }
        else
        {
            noDataLoader.setVisibility(View.GONE);
            write_layout.setVisibility(View.GONE);
            tvNoCommentData.setText(view.getContext().getString(R.string.comments_are_turned_off));
            commentCountTxt.setText("0 "+context.getString(R.string.comments));
            tvNoCommentData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }

        return view;
    }

    private void openTagUser(String tag) {
        Log.d(Constants.tag,"Tag: "+tag);
        Intent intent=new Intent(view.getContext(),ProfileA.class);
        intent.putExtra("user_name", tag);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    ArrayList<UsersModel> taggedUserList = new ArrayList<>();
    String commentType="OwnComment";
    private void hitComment() {
        String replyStr="";

        if (replyStatus==null)
        {
            commentType="OwnComment";
        }
        else
        if (replyStatus.equals("commentReply"))
        {
            replyStr=context.getString(R.string.reply_to)+" " + selectedReplyComment.replay_user_name;
            commentType="replyComment";
        }
        else
        {
            replyStr=context.getString(R.string.reply_to)+" " + selectedComment.user_name;
            commentType="replyComment";
        }

        EditTextSheetF fragment = new EditTextSheetF(commentType,taggedUserList, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    if (bundle.getString("action").equals("sendComment"))
                    {
                        taggedUserList= (ArrayList<UsersModel>) bundle.getSerializable("taggedUserList");
                        String message=bundle.getString("message");
                        tvComment.setText(message);
                        sendComment(""+message);
                    }
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("replyStr",replyStr);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "EditTextSheetF");
    }

    private void sendComment(String message) {
        if (!TextUtils.isEmpty(message)) {

            if (Functions.checkLoginUser(getActivity())) {

                if (replyStatus == null) {
                    sendComments(videoId, message);
                }
                else
                if (replyStatus.equals("commentReply"))
                {
                    message = context.getString(R.string.replied_to)+" " + "@" + selectedReplyComment.replay_user_name + " " + message;
                    sendCommentsReply(selectedReplyComment.parent_comment_id, message,videoId,selectedReplyComment.videoOwnerId);
                }
                else
                {
                    Log.d(Constants.tag,"HitAPI here comment_id "+selectedComment.comment_id);
                    sendCommentsReply(selectedComment.comment_id, message,videoId,selectedComment.videoOwnerId);
                }
                tvComment.setText(context.getString(R.string.leave_a_comment));

            }

        }
    }

    private void openCommentSetting(CommentModel item, int positon) {
        CommentSettingF fragment = new CommentSettingF(item, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    if (bundle.getString("action").equals("copyText"))
                    {
                        Functions.copyCode(view.getContext(),item.comments);
                    }
                    else
                    if (bundle.getString("action").equals("pinComment"))
                    {

                        if (Integer.valueOf(item.pin_comment_id)>0)
                        {
                            if (item.pin_comment_id.equals(item.comment_id))
                            {
                                hitApiPinComment(item,"unpin");
                            }
                            else
                            {
                                replacePreviousPinned(item,positon);
                            }
                        }
                        else
                        {
                            hitApiPinComment(item,"pin");
                        }
                    }
                    else
                    if (bundle.getString("action").equals("deleteComment"))
                    {
                        hitApiCommentDelete(item,positon);
                    }

                }
            }
        });
        fragment.show(getParentFragmentManager(), "CommentSettingF");
    }

    private void replacePreviousPinned(CommentModel item, int positon) {
        Functions.showDoubleButtonAlert(view.getContext(), view.getContext().getString(R.string.pin_this_comment),
                view.getContext().getString(R.string.pinning_description),
                view.getContext().getString(R.string.cancel_), view.getContext().getString(R.string.pin_and_replace),
                false, new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getBoolean("isShow",false))
                        {
                            hitApiPinComment(item,"pin");
                        }
                    }
                });
    }

    private void hitApiPinComment(CommentModel item,String pinHitStatus) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", item.video_id);
            String commentPin="";
            if (pinHitStatus.equals("unpin"))
            {
                commentPin="0";
            }
            else
            {
                commentPin=item.comment_id;
            }
            parameters.put("pin_comment_id", commentPin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.pinComment, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        if (pinHitStatus.equals("pin"))
                        {
                            JSONObject msgObj=response.getJSONObject("msg");
                            JSONObject videoObj=msgObj.getJSONObject("Video");
                            String pinnedCommentId=videoObj.optString("pin_comment_id");
                            for (CommentModel itemDataUpdate:dataList)
                            {
                                itemDataUpdate.pin_comment_id=pinnedCommentId;
                                dataList.set(dataList.indexOf(itemDataUpdate),itemDataUpdate);
                            }
                        }
                        else
                        {
                            for (CommentModel itemDataUpdate:dataList)
                            {
                                itemDataUpdate.pin_comment_id="0";
                                dataList.set(dataList.indexOf(itemDataUpdate),itemDataUpdate);
                            }
                        }

                        adapter.notifyDataSetChanged();

                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }

            }
        });
    }


    private void hitApiCommentDelete(CommentModel item,int position) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", item.comment_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.deleteVideoComment, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {


                        if (item.comment_id.equals(item.pin_comment_id))
                        {
                            for (CommentModel itemDataUpdate:dataList)
                            {
                                itemDataUpdate.pin_comment_id="0";
                                dataList.set(dataList.indexOf(itemDataUpdate),itemDataUpdate);
                            }
                            dataList.remove(position);
                        }
                        else
                        {
                            dataList.remove(position);
                        }
                        adapter.notifyDataSetChanged();
                        commentCount=dataList.size();
                        commentCountTxt.setText(commentCount + " "+context.getString(R.string.comments));
                        if (fragmentDataSend != null)
                            fragmentDataSend.onDataSent("" + commentCount);
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }

            }
        });
    }

    private void likeCommentReply() {


        CommentModel itemUpdate=dataList.get(selectedCommentPosition);
        ArrayList<CommentModel> replyList=itemUpdate.arrayList;

        CommentModel itemReplyUpdate=replyList.get(selectedReplyCommentPosition);

        String action = itemReplyUpdate.comment_reply_liked;
        if (action != null) {
            if (action.equals("1")) {
                action = "0";
                itemReplyUpdate.reply_liked_count = "" + (Functions.parseInterger(itemReplyUpdate.reply_liked_count) - 1);
            } else {
                action = "1";
                itemReplyUpdate.reply_liked_count = "" + (Functions.parseInterger(itemReplyUpdate.reply_liked_count) + 1);
            }
        }
        itemReplyUpdate.comment_reply_liked = action;


        Functions.callApiForLikeCommentReply(
                getActivity(), itemReplyUpdate.comment_reply_id,videoId, new

                        APICallBack() {
                            @Override
                            public void arrayData(ArrayList arrayList) {

                            }

                            @Override
                            public void onSuccess(String responce) {

                                try {
                                    JSONObject jsonObject = new JSONObject(responce);
                                    if (jsonObject.optString("code").equals("200")) {
                                        if (jsonObject.optString("msg").equals("unfavourite"))
                                        {
                                            itemReplyUpdate.isLikedByOwner = "0";

                                            replyList.set(selectedReplyCommentPosition,itemReplyUpdate);
                                            itemUpdate.arrayList=replyList;
                                            dataList.set(selectedCommentPosition, itemUpdate);
                                            adapter.notifyDataSetChanged();
                                        }
                                        else
                                        {
                                            JSONObject msgObj = jsonObject.getJSONObject("msg");
                                            JSONObject videoLikeComment = msgObj.getJSONObject("VideoCommentReplyLike");
                                            itemReplyUpdate.isLikedByOwner = videoLikeComment.optString("owner_like");

                                            replyList.set(selectedReplyCommentPosition,itemReplyUpdate);
                                            itemUpdate.arrayList=replyList;
                                            dataList.set(selectedCommentPosition, itemUpdate);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }
                                } catch (Exception e) {
                                    Log.d(Constants.tag, "Exception: " + e);
                                }
                            }

                            @Override
                            public void onFail(String responce) {

                            }
                        });
    }


    private void likeComment(int positon, CommentModel item) {

        String action = item.liked;

        if (action != null) {
            if (action.equals("1")) {
                action = "0";
                item.like_count = "" + (Functions.parseInterger(item.like_count) - 1);
            } else {
                action = "1";
                item.like_count = "" + (Functions.parseInterger(item.like_count) + 1);
            }
            Log.d(Constants.tag,"Check UserId and Owner Id"+item.userId+"      "+item.videoOwnerId);

            if (userId.equals(item.videoOwnerId))
            {
                if (item.userId.equals(item.videoOwnerId)) {
                    item.isLikedByOwner = "1";
                } else {
                    item.isLikedByOwner = "0";
                }
            }


            item.liked = action;


            Functions.callApiForLikeComment(getActivity(), item.comment_id, new APICallBack() {
                @Override
                public void arrayData(ArrayList arrayList) {
                    Log.d(Constants.tag, "DataCheck: " + arrayList.size());
                }

                @Override
                public void onSuccess(String responce) {
                    try {
                        JSONObject jsonObject = new JSONObject(responce);
                        if (jsonObject.optString("code").equals("200")) {
                            if (jsonObject.optString("msg").equals("unfavourite"))
                            {
                                if (userId.equals(item.videoOwnerId))
                                {
                                    item.isLikedByOwner = "0";
                                }


                                dataList.set(positon, item);
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                JSONObject msgObj = jsonObject.getJSONObject("msg");
                                JSONObject videoLikeComment = msgObj.getJSONObject("VideoCommentLike");
                                if (userId.equals(item.videoOwnerId))
                                {
                                    item.isLikedByOwner = videoLikeComment.optString("owner_like");
                                }


                                dataList.set(positon, item);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    } catch (Exception e) {
                        Log.d(Constants.tag, "Exception: " + e);
                    }


                }

                @Override
                public void onFail(String responce) {
                }
            });
        }
    }


    // this funtion will get all the comments against post
    public void getAllComments() {
        if (dataList.isEmpty())
        {
            noDataLoader.setVisibility(View.VISIBLE);
        }


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", videoId);
            if (Functions.getSharedPreference(view.getContext()).getBoolean(Variables.IS_LOGIN, false)) {
                parameters.put("user_id", Functions.getSharedPreference(view.getContext()).getString(Variables.U_ID, "0"));
            }
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showVideoComments, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                noDataLoader.setVisibility(View.GONE);

                CommentModel pinnedCommentModel=null;

                ArrayList<CommentModel> temp_list = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        JSONArray msgArray = response.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);

                            JSONObject videoComment = itemdata.optJSONObject("VideoComment");
                            JSONObject videoObj = itemdata.optJSONObject("Video");
                            UserModel userDetailModel= DataParsing.getUserDataModel(itemdata.optJSONObject("User"));

                            JSONArray videoCommentReply = itemdata.optJSONArray("VideoCommentReply");

                            ArrayList<CommentModel> replyList = new ArrayList<>();
                            if (videoCommentReply.length() > 0) {
                                for (int j = 0; j < videoCommentReply.length(); j++) {
                                    JSONObject jsonObject = videoCommentReply.getJSONObject(j);

                                    UserModel userDetailModelReply=DataParsing.getUserDataModel(jsonObject.optJSONObject("User"));
                                    CommentModel comment_model = new CommentModel();

                                    comment_model.comment_reply_id = jsonObject.optString("id");
                                    comment_model.reply_liked_count = jsonObject.optString("like_count");
                                    comment_model.comment_reply_liked = jsonObject.optString("like");
                                    comment_model.comment_reply = jsonObject.optString("comment");
                                    comment_model.created = jsonObject.optString("created");

                                    comment_model.videoOwnerId = videoObj.optString("user_id");
                                    comment_model.replay_user_name = userDetailModelReply.getUsername();
                                    comment_model.replay_user_url = userDetailModelReply.getProfilePic();
                                    comment_model.userId = userDetailModelReply.getId();
                                    comment_model.isVerified = userDetailModelReply.getVerified();
                                    comment_model.parent_comment_id = videoComment.optString("id");
                                    comment_model.isLikedByOwner = jsonObject.optString("owner_like");


                                    replyList.add(comment_model);
                                }
                            }

                            CommentModel item = new CommentModel();
                            item.isLikedByOwner=videoComment.optString("owner_like");
                            item.videoOwnerId = videoObj.optString("user_id");
                            item.pin_comment_id = videoObj.optString("pin_comment_id","0");
                            item.userId = userDetailModel.getId();
                            item.isVerified=userDetailModel.getVerified();
                            item.user_name = userDetailModel.getUsername();
                            item.first_name = userDetailModel.getFirstName();
                            item.last_name = userDetailModel.getLastName();
                            item.arraylist_size = String.valueOf(videoCommentReply.length());
                            item.setProfile_pic(userDetailModel.getProfilePic());

                            item.arrayList = replyList;
                            item.video_id = videoComment.optString("video_id");
                            item.comments = videoComment.optString("comment");
                            item.liked = videoComment.optString("like");
                            item.like_count = videoComment.optString("like_count");
                            item.comment_id = videoComment.optString("id");
                            item.created = videoComment.optString("created");

                            if (item.comment_id.equals(item.pin_comment_id))
                            {
                                pinnedCommentModel=item;
                            }
                            else
                            {
                                temp_list.add(item);
                            }


                        }



                        if (pageCount == 0) {
                            dataList.clear();
                            dataList.addAll(temp_list);
                        } else {
                            dataList.addAll(temp_list);
                        }



                        if (pinnedCommentModel!=null)
                        {
                            dataList.add(0,pinnedCommentModel);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    if (dataList.isEmpty()) {
                        tvNoCommentData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoCommentData.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: comment"+e);
                } finally {
                    loadMoreProgress.setVisibility(View.GONE);
                }
            }
        });
    }



    // this function will call an api to upload your comment reply
    private void sendCommentsReply(String commentId, String message,String videoId,String videoOwnerId) {
        Functions.callApiForSendCommentReply(getActivity(), commentId, message,videoId,videoOwnerId,taggedUserList, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

                tvComment.setText(context.getString(R.string.leave_a_comment));

                CommentModel itemUpdate=dataList.get(selectedCommentPosition);
                ArrayList<CommentModel> replyList =itemUpdate.arrayList;

                for (CommentModel itemReply:(ArrayList<CommentModel>)arrayList)
                {
                    replyList.add(0,itemReply);
                }
                itemUpdate.arrayList=replyList;
                itemUpdate.item_count_replies=""+itemUpdate.arrayList.size();
                dataList.set(selectedCommentPosition,itemUpdate);
                adapter.notifyDataSetChanged();
                replyStatus = null;
                selectedComment=null;
                selectedReplyComment = null;
            }

            @Override
            public void onSuccess(String responce) {
                // this will return a string responce
            }

            @Override
            public void onFail(String responce) {
                // this will return the failed responce
            }

        });

    }

    // this function will call an api to upload your comment
    public void sendComments(String video_id, final String comment) {

        send_btn.setVisibility(View.GONE);
        send_progress.setVisibility(View.VISIBLE);
        Functions.callApiForSendComment(getActivity(), video_id, comment,taggedUserList, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {

                send_btn.setVisibility(View.VISIBLE);
                send_progress.setVisibility(View.GONE);
                tvNoCommentData.setVisibility(View.GONE);

                for (CommentModel item : (ArrayList<CommentModel>)arrayList) {
                    dataList.add(0, item);
                    commentCount++;
                    commentCountTxt.setText(commentCount + " "+context.getString(R.string.comments));

                    if (fragmentDataSend != null)
                        fragmentDataSend.onDataSent("" + commentCount);

                }
                adapter.notifyDataSetChanged();
                selectedComment=null;
            }

            @Override
            public void onSuccess(String responce) {
                // this will return a string responce
                send_btn.setVisibility(View.VISIBLE);
                send_progress.setVisibility(View.GONE);
            }

            @Override
            public void onFail(String responce) {
                send_btn.setVisibility(View.VISIBLE);
                send_progress.setVisibility(View.GONE);
                // this will return the failed responce
            }

        });

    }


      // get the profile data by sending the username instead of id
    private void openProfileByUsername(String username) {

        if (Functions.getSharedPreference(context).getString(Variables.U_NAME, "0").equals(username)) {

            TabLayout.Tab profile = MainMenuActivity.tabLayout.getTabAt(4);
            profile.select();

        } else {

            Intent intent=new Intent(view.getContext(), ProfileA.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        }


    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void openProfile(CommentModel commentModel) {

        if (!Functions.getSharedPreference(context).getString(Variables.U_ID, "0").equals(commentModel.userId)) {


            Intent intent=new Intent(view.getContext(), ProfileA.class);
            intent.putExtra("user_id", commentModel.userId);
            intent.putExtra("user_name", commentModel.user_name);
            intent.putExtra("user_pic", commentModel.getProfile_pic());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

        }


    }








}
