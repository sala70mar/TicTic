package com.qboxus.tictic.activitesfragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.material.tabs.TabLayout;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.qboxus.tictic.activitesfragments.profile.ProfileA;
import com.qboxus.tictic.activitesfragments.profile.ReportTypeA;
import com.qboxus.tictic.activitesfragments.soundlists.VideoSoundA;
import com.qboxus.tictic.activitesfragments.profile.videopromotion.VideoPromoteStepsA;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderDuetA;
import com.qboxus.tictic.adapters.ViewPagerStatAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.CircleDivisionView;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.DebounceClickHandler;
import com.qboxus.tictic.simpleclasses.OnSwipeTouchListener;
import com.qboxus.tictic.simpleclasses.ShowMoreLess;
import com.qboxus.tictic.simpleclasses.VerticalViewPager;
import com.volley.plus.VPackages.VolleyRequest;
import com.qboxus.tictic.Constants;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.interfaces.FragmentDataSend;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.FriendsTagHelper;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.PermissionUtils;
import com.qboxus.tictic.simpleclasses.Variables;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.facebook.FacebookSdk.getApplicationContext;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */

// this is the main view which is show all  the video in list
public class VideosListF extends Fragment implements Player.Listener, FragmentDataSend {

    View view;
    Context context;
    LinearLayout sideMenu,videoInfoLayout;
    VerticalViewPager menuPager;
    HomeModel item;
    FragmentCallBack fragmentCallBack;
    boolean showad;
    int fragmentContainerId;
    PermissionUtils takePermissionUtils;
    FrameLayout tabBlockVideo;
    TextView tvBlockVideoMessage;
    CircleDivisionView circleStatusBar;

    public VideosListF(boolean showad, HomeModel item, VerticalViewPager menuPager, FragmentCallBack fragmentCallBack,int fragmentContainerId) {
        this.showad=showad;
        this.item = item;
        this.menuPager=menuPager;
        this.fragmentCallBack = fragmentCallBack;
        this.fragmentContainerId=fragmentContainerId;
    }


    public VideosListF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (fragmentContainerId==R.id.mainMenuFragment)
        {
            // Inflate the layout for this fragment adjuct 32dp height for transparent layout
            view = inflater.inflate(R.layout.item_home_heighted_layout, container, false);
            updateImmediateViewChange();
        }
        else {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.item_home_layout, container, false);
            updateImmediateViewChange();
        }
        context = view.getContext();

        initializePlayer();
        initalize_views();

        return view;
    }

    private void updateImmediateViewChange() {
        try {
            if (item.playlistId.equals("0"))
            {
                view.findViewById(R.id.ViewForPlaylist).setVisibility(View.GONE);
            }
            else
            {
                view.findViewById(R.id.ViewForPlaylist).setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e){
            view.findViewById(R.id.ViewForPlaylist).setVisibility(View.GONE);
        }

        try {

            if (item.getPromotionModel()!=null && item.getPromotionModel().getId()!=null)
            {
                String destination=item.getPromotionModel().getDestination();
                view.findViewById(R.id.tabPromotionText).setVisibility(View.VISIBLE);
                if (destination.equals("website"))
                {
                    updatePromotionSiteAction(((Button) view.findViewById(R.id.btnWebsiteMove)));
                }
                else
                if (destination.equals("follower"))
                {
                    updatePromotionFollowAction(((Button) view.findViewById(R.id.btnWebsiteMove)));
                }
                else
                {
                    updatePromotionVideoViewAction(((Button) view.findViewById(R.id.btnWebsiteMove)));
                }
            }
            else
            {
                view.findViewById(R.id.tabPromotionText).setVisibility(View.GONE);
                view.findViewById(R.id.btnWebsiteMove).setVisibility(View.GONE);
            }

        }
        catch (Exception e)
        {
            view.findViewById(R.id.tabPromotionText).setVisibility(View.GONE);
            view.findViewById(R.id.btnWebsiteMove).setVisibility(View.GONE);
        }
    }

    private void updatePromotionVideoViewAction(Button btnPromote) {
        btnPromote.setVisibility(View.GONE);
        btnPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void updatePromotionSiteAction(Button btnPromote) {
        btnPromote.setVisibility(View.VISIBLE);
        String actionButton=""+item.getPromotionModel().getAction_button();
        if (actionButton.equalsIgnoreCase("Shop now"))
        {
            btnPromote.setText(view.getContext().getString(R.string.shop_now));
        }
        else
        if (actionButton.equalsIgnoreCase("Sign up"))
        {
            btnPromote.setText(view.getContext().getString(R.string.sign_up));
        }
        else
        if (actionButton.equalsIgnoreCase("Contact us"))
        {
            btnPromote.setText(view.getContext().getString(R.string.contact_us));
        }
        else
        if (actionButton.equalsIgnoreCase("Apply now"))
        {
            btnPromote.setText(view.getContext().getString(R.string.apply_now));
        }
        else
        if (actionButton.equalsIgnoreCase("Book now"))
        {
            btnPromote.setText(view.getContext().getString(R.string.book_now));
        }
        else
        {
            btnPromote.setText(view.getContext().getString(R.string.learn_more));
        }

        btnPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitPromotedVideoWebsite();
            }
        });
    }

    private void hitPromotedVideoWebsite() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("promotion_id", item.getPromotionModel().getId());
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.destinationTap, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        openWebUrl(""+item.getPromotionModel().getAction_button(),
                                item.getPromotionModel().getWebsite_url());
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }

            }
        });
    }

    public void openWebUrl(String title, String url) {
        Intent intent=new Intent(view.getContext(), WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void updatePromotionFollowAction(Button btnPromote) {
        String follow_status=item.follow_status_button;
        if (follow_status==null)
        {
            follow_status="";
        }

        if (follow_status.equalsIgnoreCase("following")) {
            btnPromote.setVisibility(View.GONE);

        }
        else
        if (follow_status.equalsIgnoreCase("friends")) {
            btnPromote.setVisibility(View.GONE);
        }
        else
        if (follow_status.equalsIgnoreCase("follow back")) {
            if (Variables.followMapList.containsKey(item.user_id))
            {
                btnPromote.setVisibility(View.GONE);
            }
            else
            {
                btnPromote.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            if (Variables.followMapList.containsKey(item.user_id))
            {
                btnPromote.setVisibility(View.GONE);
            }
            else
            {
                btnPromote.setVisibility(View.VISIBLE);
            }
        }

        btnPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    TextView descTxt;
    TextView username, soundName, skipBtn;
    SimpleDraweeView userPic, soundImage,thumb_image;
    ImageView varifiedBtn,ivAddFollow;
    RelativeLayout duetLayoutUsername, animateRlt, mainlayout;
    LinearLayout duetOpenVideo;
    LinearLayout likeLayout, commentLayout, sharedLayout, soundImageLayout;
    LikeButton likeImage,tabFavourite;
    ImageView commentImage;
    TextView likeTxt,tvFavourite,tvShare, commentTxt, duetUsername;
    StyledPlayerView  playerView;
    Handler handler;
    Runnable runnable;
    Boolean animationRunning = false;
    ProgressBar pbar;
    LinearLayout tabRepost;
    SimpleDraweeView ivRepostUser;


    public void initalize_views() {
        circleStatusBar=view.findViewById(R.id.circleStatusBar);
        sideMenu=view.findViewById(R.id.side_menu);
        videoInfoLayout=view.findViewById(R.id.video_info_layout);
        mainlayout = view.findViewById(R.id.mainlayout);
        playerView = view.findViewById(R.id.playerview);
        tabRepost=view.findViewById(R.id.tabRepost);
        ivRepostUser=view.findViewById(R.id.ivRepostUser);
        tabBlockVideo=view.findViewById(R.id.tabBlockVideo);
        tvBlockVideoMessage=view.findViewById(R.id.tvBlockVideoMessage);
        duetLayoutUsername = view.findViewById(R.id.duet_layout_username);
        duetUsername = view.findViewById(R.id.duet_username);
        duetOpenVideo = view.findViewById(R.id.duet_open_video);
        username = view.findViewById(R.id.username);
        userPic = view.findViewById(R.id.user_pic);
        thumb_image=view.findViewById(R.id.thumb_image);
        soundName = view.findViewById(R.id.sound_name);
        soundImage = view.findViewById(R.id.sound_image);
        varifiedBtn = view.findViewById(R.id.varified_btn);
        likeLayout = view.findViewById(R.id.like_layout);
        likeImage = view.findViewById(R.id.likebtn);
        tabFavourite=view.findViewById(R.id.tabFavourite);
        likeTxt = view.findViewById(R.id.like_txt);
        tvFavourite=view.findViewById(R.id.tvFavourite);
        tvShare=view.findViewById(R.id.tvShare);
        animateRlt = view.findViewById(R.id.animate_rlt);
        skipBtn = view.findViewById(R.id.skip_btn);
        descTxt = view.findViewById(R.id.desc_txt);
        commentLayout = view.findViewById(R.id.comment_layout);
        commentImage = view.findViewById(R.id.comment_image);
        commentTxt = view.findViewById(R.id.comment_txt);
        soundImageLayout = view.findViewById(R.id.sound_image_layout);
        sharedLayout = view.findViewById(R.id.shared_layout);
        pbar = view.findViewById(R.id.p_bar);
        ivAddFollow=view.findViewById(R.id.ivAddFollow);
        ivAddFollow.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Functions.checkLoginUser(getActivity())) {
                    followUnFollowUser();
                }
            }
        }));
        duetOpenVideo.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDuetVideo(item);
            }
        }));
        userPic.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPause();
                openProfile(item, false);
            }
        }));
        animateRlt.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Functions.checkLoginUser(getActivity())){
                    animateRlt.setVisibility(View.GONE);
                    likeVideo(item);
                }
            }
        }));
        username.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPause();
                openProfile(item, false);
            }
        }));
        commentLayout.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Functions.checkLoginUser(getActivity()))
                {
                    openComment(item);
                }
            }
        }));
        sharedLayout.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareVideoView();
            }
        }));
        soundImageLayout.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item==null || item.user_id==null)
                {
                    return;
                }
                if (item.getPromotionModel()!=null && item.getPromotionModel().getId()!=null)
                {
                    Functions.showToastOnTop(getActivity(),view,context.getString(R.string.video_ads_do_not_support_this_feature));
                    return;
                }
                takePermissionUtils=new PermissionUtils(getActivity(),mPermissionResult);
                if (takePermissionUtils.isCameraRecordingPermissionGranted()) {
                    openSoundByScreen();
                }
                else
                {
                    takePermissionUtils.showCameraRecordingPermissionDailog(view.getContext().getString(R.string.we_need_camera_and_recording_permission_for_make_video_on_sound));
                }
            }
        }));
        skipBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAd();
            }
        }));


        likeImage.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeVideo(item);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeVideo(item);
            }
        });

        tabFavourite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                favouriteVideo(item);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                favouriteVideo(item);
            }
        });




        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setData();
            }
        },200);

    }


    private void followUnFollowUser() {
        Functions.callApiForFollowUnFollow(getActivity(),
                Functions.getSharedPreference(context).getString(Variables.U_ID, ""),
                item.user_id,
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {
                    }

                    @Override
                    public void onSuccess(String responce) {
                        try {
                            JSONObject object = new JSONObject(responce);
                            if (object.optString("code").equals("200")) {
                                JSONObject msg = object.optJSONObject("msg");
                                UserModel receiverDetailModel = DataParsing.getUserDataModel(msg.optJSONObject("User"));

                                String follow_status = receiverDetailModel.getButton().toLowerCase();
                                item.follow_status_button = receiverDetailModel.getButton().toLowerCase();
                                setFollowBtnStatus(receiverDetailModel.getId(), follow_status);


                            }
                        } catch (Exception e) {
                            Functions.printLog(Constants.tag, "Exception : " + e);
                        }
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });

    }

    private void setFollowBtnStatus(String id, String follow_status) {
        if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false)) {
            if (!id.equalsIgnoreCase(Functions.getSharedPreference(context)
                    .getString(Variables.U_ID, ""))) {

                if (follow_status==null)
                {
                    follow_status="";
                }

                if (follow_status.equalsIgnoreCase("following")) {
                    ivAddFollow.setVisibility(View.GONE);

                } else if (follow_status.equalsIgnoreCase("friends")) {
                    ivAddFollow.setVisibility(View.GONE);
                } else if (follow_status.equalsIgnoreCase("follow back")) {
                    if (Variables.followMapList.containsKey(item.user_id))
                    {
                        ivAddFollow.setVisibility(View.GONE);
                    }
                    else
                    {
                        ivAddFollow.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (Variables.followMapList.containsKey(item.user_id))
                    {
                        ivAddFollow.setVisibility(View.GONE);
                    }
                    else
                    {
                        ivAddFollow.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    }


    private void openShareVideoView() {
        if (item==null || item.user_id==null)
        {
            return;
        }


        final VideoActionF fragment = new VideoActionF(item.video_id, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getString("action").equals("save")) {

                    Functions.createAppNameVideoDirectory(context);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    saveVideo(item);
                                }
                            });
                        }
                    },500);

                } else if (bundle.getString("action").equals("repost")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        repostVideo(item);
                    }
                }else if (bundle.getString("action").equals("duet")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        duetVideo(item);
                    }
                } else if (bundle.getString("action").equals("privacy")) {
                    onPause();
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        openVideoSetting(item);
                    }

                } else if (bundle.getString("action").equals("delete")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        deleteListVideo(item);
                    }
                } else if (bundle.getString("action").equals("favourite")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        favouriteVideo(item);
                    }
                } else if (bundle.getString("action").equals("not_intrested")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        notInterestVideo(item);
                    }
                } else if (bundle.getString("action").equals("report")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        openVideoReport(item);
                    }
                }else if (bundle.getString("action").equals("promotion")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        openVideoPromotion(item);
                    }
                } else if (bundle.getString("action").equals("pinned")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        HashMap<String,HomeModel> pinnedVideo=new HashMap<>();
                        pinnedVideo= Paper.book("PinnedVideo").read("pinnedVideo");
                        if (pinnedVideo.containsKey(item.video_id))
                        {
                            hitPinedVideo(item);
                        }
                        else
                        {
                            if(pinnedVideo.keySet().size()<3)
                            {
                                hitPinedVideo(item);
                            }
                            else
                            {
                                Toast.makeText(context, context.getString(R.string.only_three_video_pinned_is_allow), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
                else if (bundle.getString("action").equals("videoShare")) {
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        updateVideoView();
                    }
                }


            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("videoId", item.video_id);
        bundle.putString("userId", item.user_id);
        bundle.putString("userName", item.username);
        bundle.putString("userPic", item.getProfile_pic());
        bundle.putString("fullName", item.first_name+" "+item.last_name);
        bundle.putSerializable("data", item);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "VideoActionF");
    }


    public void setData() {

        if(view==null )
            return;
        else {

            if (item!=null)
            {


                thumb_image.setController(Functions.frescoImageLoad(item.getThum(),thumb_image,false));

                username.setText(Functions.showUsernameOnVideoSection(item));

                userPic.setController(Functions.frescoImageLoad(item.getProfile_pic(),userPic,false));

                if (item.repost.equals("1"))
                {
                    tabRepost.setVisibility(View.VISIBLE);
                    ivRepostUser.setController(
                            Functions.frescoImageLoad(Functions.getSharedPreference(context).getString(Variables.U_PIC, "null")
                            ,ivRepostUser,false));
                }
                else
                {
                    tabRepost.setVisibility(View.GONE);
                }

                if (item.getPromotionModel()!=null && item.getPromotionModel().getId()!=null)
                {
                    soundName.setText(view.getContext().getString(R.string.promoted_music));
                    soundName.setSelected(false);
                }
                else
                {
                    if ((item.sound_name == null || item.sound_name.equals("") || item.sound_name.equals("null"))) {
                        soundName.setText(context.getString(R.string.orignal_sound_)+" " + item.username);
                        item.setSound_pic(item.getProfile_pic());
                    }
                    else {
                        soundName.setText(item.sound_name);
                    }
                    soundName.setSelected(true);
                }

                setFollowBtnStatus(item.user_id, item.follow_status_button);
                if (item.getSound_pic().equals(Constants.BASE_URL))
                {
                    soundImage.setController(Functions.frescoImageLoad(item.getProfile_pic(),R.drawable.ic_round_music,soundImage,false));
                }
                else
                {
                    soundImage.setController(Functions.frescoImageLoad(item.getSound_pic(),R.drawable.ic_round_music,soundImage,false));
                }


              //  descTxt.setText(item.video_description);
                FriendsTagHelper.Creator.create(ContextCompat.getColor(view.getContext(),R.color.whiteColor),ContextCompat.getColor(view.getContext(),R.color.whiteColor), new FriendsTagHelper.OnFriendsTagClickListener() {
                    @Override
                    public void onFriendsTagClicked(String friendsTag) {
                        onPause();
                        if (friendsTag.contains("#"))
                        {
                            Log.d(Constants.tag,"Hash "+friendsTag);
                            if (friendsTag.charAt(0)=='#')
                            {
                                friendsTag=friendsTag.substring(1);
                                openHashtag(friendsTag);
                            }
                        }
                        else
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
                }).handle(descTxt);

              ShowMoreLess builder =  new ShowMoreLess.Builder(context)
                        .textLengthAndLengthType(2, ShowMoreLess.TYPE_LINE)
                        .showMoreLabel(context.getString(R.string.show_more))
                    .showLessLabel(context.getString(R.string.show_less))
                    .showMoreLabelColor(Color.parseColor("#ffffff"))
                    .showLessLabelColor(Color.parseColor("#ffffff"))
                    .labelUnderLine(false)
                        .expandAnimation(true)
                      .enableLinkify(true)
                      .textClickable(false, false).build();
              builder.addShowMoreLess(descTxt,item.video_description,false);
              descTxt.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if(builder.getContentExpandStatus())
                      builder.addShowMoreLess(descTxt,item.video_description,false);
                      else
                          builder.addShowMoreLess(descTxt,item.video_description,true);
                  }
              });


                setLikeData();
                setFavouriteData();
                tvShare.setText(Functions.getSuffix(item.share));

                if (item.allow_comments != null && item.allow_comments.equalsIgnoreCase("false")) {
                    commentLayout.setVisibility(View.GONE);
                } else {
                    commentLayout.setVisibility(View.VISIBLE);
                }
                commentTxt.setText(Functions.getSuffix(item.video_comment_count));


                if (item.verified != null && item.verified.equalsIgnoreCase("1")) {
                    varifiedBtn.setVisibility(View.VISIBLE);
                } else {
                    varifiedBtn.setVisibility(View.GONE);
                }


                if (item.duet_video_id != null && !item.duet_video_id.equals("") && !item.duet_video_id.equals("0")) {
                    duetLayoutUsername.setVisibility(View.VISIBLE);
                    duetUsername.setText(item.duet_username);
                }


                if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false)) {
                    animateRlt.setVisibility(View.GONE);
                }

            }
        }
    }

    public void setLikeData() {
        try {
            if (item.liked.equals("1")) {
                likeImage.animate().start();
                likeImage.setLikeDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart_gradient));
                likeImage.setLiked(true);
            } else {
                likeImage.setLikeDrawable(ContextCompat.getDrawable(context,R.drawable.ic_unliked));
                likeImage.setLiked(false);
                likeImage.animate().cancel();
            }
        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: "+e);
        }

        likeTxt.setText(Functions.getSuffix(item.like_count));
    }


    public void setFavouriteData() {
        try {
            if (item.favourite.equals("1")) {
                tabFavourite.animate().start();
                tabFavourite.setLikeDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favourite));
                tabFavourite.setLiked(true);
            } else {
                tabFavourite.setLikeDrawable(ContextCompat.getDrawable(context,R.drawable.ic_unfavourite));
                tabFavourite.setLiked(false);
                tabFavourite.animate().cancel();
            }
        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: "+e);
        }

        tvFavourite.setText(Functions.getSuffix(item.favourite_count));

    }




    private void openVideoPromotion(HomeModel item) {
        onPause();
        Intent intent=new Intent(view.getContext(), VideoPromoteStepsA.class);
        intent.putExtra("modelData",item);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    String currentPinStatus="0";
    private void hitPinedVideo(HomeModel item) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", item.video_id);
            if (item.pin.equals("1"))
            {
                currentPinStatus="0";
            }
            else
            {
                currentPinStatus="1";
            }
            parameters.put("pin", currentPinStatus);

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.pinVideo, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        item.pin=currentPinStatus;
                        Bundle bundle = new Bundle();
                        bundle.putString("action", "pinned");
                        bundle.putInt("position", menuPager.getCurrentItem());
                        bundle.putString("pin",currentPinStatus);
                        fragmentCallBack.onResponce(bundle);

                        HashMap<String,HomeModel> pinnedVideo=new HashMap<>();
                        pinnedVideo=Paper.book("PinnedVideo").read("pinnedVideo");
                        HomeModel itemUpdate=pinnedVideo.get(item.video_id);
                        pinnedVideo.put(itemUpdate.video_id,itemUpdate);
                        Paper.book("PinnedVideo").write("pinnedVideo",pinnedVideo);


                        ViewPagerStatAdapter pagerAdapter= (ViewPagerStatAdapter) menuPager.getAdapter();
                        pagerAdapter.refreshStateSet(true);
                        pagerAdapter.notifyDataSetChanged();
                        pagerAdapter.refreshStateSet(false);
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }

            }
        });
    }




    private void openSoundByScreen() {
        if (item.sound_id==null || item.sound_id.equals("0") || item.sound_id.equals("null"))
        {
            return;
        }
        Intent intent = new Intent(view.getContext(), VideoSoundA.class);
        intent.putExtra("data", item);
        startActivity(intent);
    }

    private void deleteListVideo(HomeModel item) {
        Functions.showLoader(getActivity(), false, false);
        Functions.callApiForDeleteVideo(getActivity(), item.video_id, new APICallBack() {
            @Override
            public void arrayData(ArrayList arrayList) {
                //return data in case of array list
            }

            @Override
            public void onSuccess(String responce) {
                ViewPagerStatAdapter pagerAdapter= (ViewPagerStatAdapter) menuPager.getAdapter();
                Bundle bundle = new Bundle();
                bundle.putString("action", "deleteVideo");
                bundle.putInt("position", menuPager.getCurrentItem());
                fragmentCallBack.onResponce(bundle);
                pagerAdapter.refreshStateSet(true);
                pagerAdapter.removeFragment(menuPager.getCurrentItem());
                pagerAdapter.refreshStateSet(false);
            }

            @Override
            public void onFail(String responce) {
            }
        });
    }

    private void openVideoSetting(HomeModel item) {
        Intent intent=new Intent(view.getContext(),PrivacyVideoSettingA.class);
        intent.putExtra("video_id", item.video_id);
        intent.putExtra("privacy_value", item.privacy_type);
        intent.putExtra("duet_value", item.allow_duet);
        intent.putExtra("comment_value", item.allow_comments);
        intent.putExtra("duet_video_id", item.duet_video_id);
        resultVideoSettingCallback.launch(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    ActivityResultLauncher<Intent> resultVideoSettingCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            callApiForSinglevideos();
                        }

                    }
                }
            });

    ExoPlayer exoplayer;
    // initlize the player for play video
    private void initializePlayer() {
        if(exoplayer==null && item!=null){
            try {
                exoplayer =new ExoPlayer.Builder(context).
                        setTrackSelector(new DefaultTrackSelector(context)).
                        setLoadControl(Functions.getExoControler()).
                        build();
                Uri videoURI = Uri.parse(item.getVideo_url());
                MediaItem mediaItem = MediaItem.fromUri(videoURI);
                exoplayer.setMediaItem(mediaItem);
                exoplayer.prepare();
                exoplayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build();
                exoplayer.setAudioAttributes(audioAttributes, true);
                exoplayer.addListener(VideosListF.this);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerView = view.findViewById(R.id.playerview);
                        playerView.findViewById(R.id.exo_play).setVisibility(View.GONE);
                        if(exoplayer!=null) {
                            playerView.setPlayer(exoplayer);
                        }
                    }
                });

            }
            catch (Exception e)
            {
                Log.d(Constants.tag,"Exception : "+e);
            }

        }

    }

    public void setPlayer(boolean isVisibleToUser) {

        if (exoplayer != null) {

            if(exoplayer!=null) {
                if (isVisibleToUser)
                {
                    exoplayer.setPlayWhenReady(true);
                }
                else
                {
                    exoplayer.setPlayWhenReady(false);
                    playerView.findViewById(R.id.exo_play).setAlpha(1);
                }


            }
            playerView.setOnTouchListener(new OnSwipeTouchListener(context) {

                public void onSwipeLeft() {
                    openProfile(item, true);
                }

                @Override
                public void onLongClick() {
                    if(isVisibleToUser)
                    {
                        showVideoOption(item);
                    }
                }

                @Override
                public void onSingleClick() {
                    if (!exoplayer.getPlayWhenReady()) {
                        exoplayer.setPlayWhenReady(true);
                        playerView.findViewById(R.id.exo_play).setAlpha(0);
                    } else {
                        exoplayer.setPlayWhenReady(false);
                        playerView.findViewById(R.id.exo_play).setAlpha(1);
                    }
                }

                @Override
                public void onDoubleClick(MotionEvent e) {
                    if (!exoplayer.getPlayWhenReady()) {
                        exoplayer.setPlayWhenReady(true);
                    }
                    if (Functions.checkLoginUser(getActivity()))
                    {
                        if (!animationRunning) {

                            if (handler != null && runnable != null) {
                                handler.removeCallbacks(runnable);

                            }
                            handler = new Handler(Looper.getMainLooper());
                            runnable = new Runnable() {
                                public void run() {
                                    if (!(item.liked.equalsIgnoreCase("1")))
                                    {
                                        likeVideo(item);
                                    }
                                    showHeartOnDoubleTap(item, mainlayout, e);

                                }
                            };
                            handler.postDelayed(runnable, 200);


                        }
                    }
                }

            });

            if ((item.promote != null && item.promote.equals("1")) && showad)
            {
                item.promote="0";
                showAd();
            }
            else
            {
                hideAd();
            }

        }

    }


    public void updateVideoView(){
        if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false))
        {
            Functions.callApiForUpdateView(getActivity(), item.video_id, new Callback() {
                @Override
                public void onResponce(String resp) {
                    Functions.checkStatus(getActivity(),resp);
                    try {
                        JSONObject jsonObject=new JSONObject(resp);
                        String code=jsonObject.optString("code");
                        if(code!=null && code.equals("200")){
                            JSONObject msgObj=jsonObject.getJSONObject("msg");
                            JSONObject videoObj=msgObj.getJSONObject("Video");
                            String share="0" + videoObj.optInt("share");

                            item.share = share;

                            setData();
                        }
                    } catch (Exception e) {
                        Log.d(Constants.tag,"Exception: "+e);
                    }
                }
            });
        }
//        callApiForSinglevideos();
    }
    // show a video as a ad
    boolean  isAddAlreadyShow;
    public void showAd() {
        soundImageLayout.setAnimation(null);
        sideMenu.setVisibility(View.GONE);
        videoInfoLayout.setVisibility(View.GONE);
        soundImageLayout.setVisibility(View.GONE);
        skipBtn.setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("action", "showad");
        fragmentCallBack.onResponce(bundle);

        countdownTimer(true);

    }



    CountDownTimer countDownTimer;
    public void countdownTimer(boolean isStart) {
        if (isStart)
        {
            if (countDownTimer==null)
            {
                countDownTimer = new CountDownTimer((7*1000), 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (getActivity()!=null)
                        {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideAd();
                                    countdownTimer(false);
                                }
                            });

                        }

                    }
                };
                countDownTimer.start();
            }

        }
        else
        {
            if (countDownTimer!=null)
            {
                countDownTimer.cancel();
                countDownTimer=null;
            }
        }
    }

    // hide the ad of video after some time
    public void hideAd() {
        isAddAlreadyShow = true;
        sideMenu.setVisibility(View.VISIBLE);
        videoInfoLayout.setVisibility(View.VISIBLE);
        soundImageLayout.setVisibility(View.VISIBLE);
        Animation aniRotate = AnimationUtils.loadAnimation(context, R.anim.d_clockwise_rotation);
        soundImageLayout.startAnimation(aniRotate);

        skipBtn.setVisibility(View.GONE);

        Bundle bundle = new Bundle();
        bundle.putString("action", "hidead");
        fragmentCallBack.onResponce(bundle);
    }


    boolean isVisibleToUser;
    @Override
    public void setMenuVisibility(final boolean visible) {
        isVisibleToUser = visible;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (exoplayer != null && visible) {
                    setPlayer(isVisibleToUser);
                    updateVideoView();
                }

            }
        },200);

        if (visible)
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (view!=null && getActivity()!=null)
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(item!=null && item.user_id!=null)
                                {
                                    setLikeData();
                                    setFavouriteData();
                                    if (item.block.equals("1"))
                                    {
                                        tvBlockVideoMessage.setText(""+item.aws_label);
                                        tabBlockVideo.setVisibility(View.VISIBLE);
                                        onStop();
                                    }
                                    else
                                    {
                                        tabBlockVideo.setVisibility(View.GONE);
                                    }



                                    try {
                                      if (item.storyDataList!=null && item.storyDataList.size()>0)
                                      {
                                          circleStatusBar.setCounts(item.storyDataList.size());
                                          circleStatusBar.setVisibility(View.VISIBLE);
                                      }
                                      else
                                      {
                                          circleStatusBar.setVisibility(View.GONE);
                                      }
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(Constants.tag,"Exception: "+e);
                                    }
                                }

                            }
                        });

                    }
                }
            },200);
        }
    }



    public void mainMenuVisibility(boolean isvisible) {

        if (exoplayer != null && isvisible) {
            exoplayer.setPlayWhenReady(true);
        }

        else if (exoplayer != null && !isvisible) {
            exoplayer.setPlayWhenReady(false);
            playerView.findViewById(R.id.exo_play).setAlpha(1);
        }


    }


    // when we swipe for another video this will relaese the privious player

    public void releasePriviousPlayer() {
        if (exoplayer != null) {
            exoplayer.removeListener(this);
            exoplayer.release();
            exoplayer = null;
        }
    }


    @Override
    public void onDestroy() {
        releasePriviousPlayer();
        super.onDestroy();

    }


    private void openDuetVideo(HomeModel item) {
        Intent intent = new Intent(view.getContext(), WatchVideosA.class);
        intent.putExtra("video_id", item.duet_video_id);
        intent.putExtra("position", 0);
        intent.putExtra("pageCount", 0);
        intent.putExtra("userId",Functions.getSharedPreference(view.getContext()).getString(Variables.U_ID,""));
        intent.putExtra("whereFrom","IdVideo");
        startActivity(intent);
    }

    // this will open the profile of user which have uploaded the currenlty running video
    private void openHashtag(String tag) {

        Intent intent=new Intent(view.getContext(),TagedVideosA.class);
        intent.putExtra("tag", tag);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void openUserProfile(String tag) {

        Log.d(Constants.tag,"Tag: "+tag);

        Intent intent=new Intent(view.getContext(),ProfileA.class);
        intent.putExtra("user_name", tag);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            playerView.findViewById(R.id.exo_play).setAlpha(1);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            playerView.findViewById(R.id.exo_play).setAlpha(1);
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        Player.Listener.super.onPlayerError(error);
        Log.d(Constants.tag,"Exception player: "+error.getMessage());
        Log.d(Constants.tag,"Exception player12: "+error);
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        Player.Listener.super.onVideoSizeChanged(videoSize);
        if (videoSize.width>videoSize.height)
        {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        }
        else
        {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            pbar.setVisibility(View.VISIBLE);
        }
        else if (playbackState == Player.STATE_READY) {
            thumb_image.setVisibility(View.GONE);

            pbar.setVisibility(View.GONE);
        }
    }


    // show a heart animation on double tap
    public boolean showHeartOnDoubleTap(HomeModel item, final RelativeLayout mainlayout, MotionEvent e) {
        try {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int x = (int) e.getX() ;
                    int y = (int) e.getY() ;
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    final ImageView iv = new ImageView(getApplicationContext());
                    lp.setMargins(x, y, 0, 0);
                    iv.setLayoutParams(lp);
                    if (item.liked.equals("1")){
                        iv.setImageDrawable(ContextCompat.getDrawable(context,
                                R.drawable.ic_heart_gradient));
                    }
                    mainlayout.addView(iv);
                    iv.animate().alpha(0).translationY(-200).setDuration(500).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (iv!=null && mainlayout!=null)
                            {
                                mainlayout.removeView(iv);
                            }
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            if (iv!=null && mainlayout!=null)
                            {
                                mainlayout.removeView(iv);
                            }
                        }
                    }).start();
                }
            });

        }
        catch (Exception excep)
        {
            Functions.printLog(Constants.tag,"Exception : "+excep);
        }

        return true;
    }

    // this function will call for like the video and Call an Api for like the video
    public void likeVideo(final HomeModel home_model) {
        if (home_model==null || home_model.liked==null || home_model.like_count==null
                || home_model.liked.equals("null") || home_model.like_count.equals("null"))
        {
            return;
        }
        String action = home_model.liked;

        if (action.equals("1")) {
            action = "0";
            home_model.like_count = "" + (Functions.parseInterger(home_model.like_count) - 1);
        } else {
            action = "1";
            home_model.like_count = "" + (Functions.parseInterger(home_model.like_count) + 1);
        }

        home_model.liked = action;

        setLikeData();

        Functions.callApiForLikeVideo(getActivity(), home_model.video_id, action, null);

    }

    // this will open the comment screen
    public void openComment(HomeModel item) {
        if (item==null || item.user_id==null)
        {
            return;
        }
        int comment_counnt = Functions.parseInterger(item.video_comment_count);
        FragmentDataSend fragment_data_send = this;
        CommentF fragment = new CommentF(comment_counnt, fragment_data_send);
        Bundle args=new Bundle();
        args.putString("video_id", item.video_id);
        args.putString("user_id", item.user_id);
        args.putSerializable("data", item);
        fragment.setArguments(args);
        fragment.show(getChildFragmentManager(), "CommentF");
    }



    public static FragmentCallBack videoListCallback;
    // this will open the profile of user which have uploaded the currenlty running video
    private void openProfile(HomeModel item, boolean from_right_to_left) {

        if (item==null || item.user_id==null)
        {
            return;
        }

        if (Variables.sharedPreferences.getString(Variables.U_ID, "0").equals(item.user_id)) {

            TabLayout.Tab profile = MainMenuActivity.tabLayout.getTabAt(4);
            profile.select();

        }
        else {
            videoListCallback=new FragmentCallBack() {
                @Override
                public void onResponce(Bundle bundle) {
                    if (bundle.getBoolean("isShow"))
                    {
                        callApiForSinglevideos();
                    }
                }
            };
            Intent intent=new Intent(view.getContext(), ProfileA.class);
            intent.putExtra("user_id", item.user_id);
            intent.putExtra("user_name", item.username);
            intent.putExtra("user_pic", item.getProfile_pic());
            resultCallback.launch(intent);
            if (from_right_to_left)
            {
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
            else
            {
                getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            }
        }
    }


    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            callApiForSinglevideos();
                        }
                    }
                }
            });


    // show the diolge of video options
   private void showVideoOption(final HomeModel homeModel) {


        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_label_editor);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.d_round_white_background));

        RelativeLayout btn_add_to_fav = alertDialog.findViewById(R.id.btn_add_to_fav);
        RelativeLayout btn_not_insterested = alertDialog.findViewById(R.id.btn_not_insterested);
        RelativeLayout btn_report = alertDialog.findViewById(R.id.btn_report);
        RelativeLayout btnDelete=alertDialog.findViewById(R.id.btnDelete);

        TextView fav_unfav_txt = alertDialog.findViewById(R.id.fav_unfav_txt);


        if (homeModel.favourite != null && homeModel.favourite.equals("1"))
            fav_unfav_txt.setText(context.getString(R.string.added_to_favourite));
        else
            fav_unfav_txt.setText(context.getString(R.string.add_to_favourite));


        if (homeModel.user_id.equalsIgnoreCase(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))) {
            btn_report.setVisibility(View.GONE);
            btn_not_insterested.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        }


        btn_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (Functions.checkLoginUser(getActivity())) {
                    favouriteVideo(item);
                }
            }
        });


        btn_not_insterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                if (Functions.checkLoginUser(getActivity()))
                {
                    notInterestVideo(item);
                }
            }
        });


        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (Functions.checkLoginUser(getActivity()))
                {
                    openVideoReport(item);
                }
            }
        });

       btnDelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               alertDialog.dismiss();
               if (Functions.checkLoginUser(getActivity()))
               {
                   deleteListVideo(item);
               }
           }
       });

        alertDialog.show();
    }



    // this method will be favourite the video
    public void favouriteVideo(final HomeModel item) {

        if (item==null || item.favourite==null || item.favourite_count==null
                || item.favourite.equals("null") || item.favourite_count.equals("null"))
        {
            return;
        }
        String action = item.favourite;

        if (action.equals("1")) {
            action = "0";
            item.favourite_count = "" + (Functions.parseInterger(item.favourite_count) - 1);
        } else {
            action = "1";
            item.favourite_count = "" + (Functions.parseInterger(item.favourite_count) + 1);
        }

        item.favourite = action;

        setFavouriteData();

        Functions.callApiForFavouriteVideo(getActivity(), item.video_id, action, null);
    }

    // call the api if a user is not intersted the video then the video will not show again to him/her
    public void notInterestVideo(final HomeModel item) {

        JSONObject params = new JSONObject();
        try {
            params.put("video_id", item.video_id);
            params.put("user_id", Variables.sharedPreferences.getString(Variables.U_ID, ""));

        } catch (Exception e) {
            e.printStackTrace();
        }


        Functions.showLoader(getActivity(), false, false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.notInterestedVideo, params,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        ViewPagerStatAdapter pagerAdapter= (ViewPagerStatAdapter) menuPager.getAdapter();
                        Bundle bundle = new Bundle();
                        bundle.putString("action", "removeList");
                        fragmentCallBack.onResponce(bundle);
                        pagerAdapter.refreshStateSet(true);
                        pagerAdapter.removeFragment(menuPager.getCurrentItem());
                        pagerAdapter.refreshStateSet(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    public void openVideoReport(HomeModel home_model) {
        onPause();
        Intent intent=new Intent(view.getContext(), ReportTypeA.class);
        intent.putExtra("video_id", home_model.video_id);
        intent.putExtra("isFrom",false);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    // save the video in to local directory
    public void saveVideo(final HomeModel item) {

        JSONObject params = new JSONObject();
        try {
            params.put("video_id", item.video_id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(), false, false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.downloadVideo, params,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject responce = new JSONObject(resp);
                    String code = responce.optString("code");
                    if (code.equals("200")) {
                        final String download_url = responce.optString("msg");
                        if (download_url != null) {

                            String downloadDirectory="";
                            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.P)
                            {
                                downloadDirectory=Functions.getAppFolder(view.getContext());
                            }
                            else
                            {
                                downloadDirectory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+File.separator+context.getString(R.string.app_name)+File.separator+Variables.VideoDirectory+File.separator;
                            }


                            Functions.showDeterminentLoader(getActivity(), false, false,true,context.getString(R.string.download_));
                            PRDownloader.initialize(getActivity().getApplicationContext());
                            DownloadRequest prDownloader = PRDownloader.download(Constants.BASE_URL + download_url, downloadDirectory, item.video_id + ".mp4")
                                    .build()

                                    .setOnProgressListener(new OnProgressListener() {
                                        @Override
                                        public void onProgress(Progress progress) {

                                            int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                            Functions.showLoadingProgress(prog);

                                        }
                                    });


                            String finalDownloadDirectory = downloadDirectory;
                            prDownloader.start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Functions.cancelDeterminentLoader();
                                    if (Build.VERSION.SDK_INT>Build.VERSION_CODES.P)
                                    {
                                        downloadAEVideo(finalDownloadDirectory,item.video_id + ".mp4");
                                    }
                                    else
                                    {
                                        deleteWaterMarkeVideo(download_url);
                                        scanFile(finalDownloadDirectory);
                                    }
                                }

                                @Override
                                public void onError(Error error) {

                                    Functions.printLog(Constants.tag, "Error : "+error.getConnectionException());
                                    Functions.cancelDeterminentLoader();
                                }


                            });



                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }


    public void downloadAEVideo(String path, String videoName) {
        ContentValues valuesvideos;
        valuesvideos = new ContentValues();
        valuesvideos.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+File.separator+context.getString(R.string.app_name)+File.separator+Variables.VideoDirectory);
        valuesvideos.put(MediaStore.MediaColumns.TITLE, videoName);
        valuesvideos.put(MediaStore.MediaColumns.DISPLAY_NAME, videoName);
        valuesvideos.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        valuesvideos.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        valuesvideos.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
        valuesvideos.put(MediaStore.MediaColumns.IS_PENDING, 1);
        ContentResolver resolver = getActivity().getContentResolver();
        Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uriSavedVideo = resolver.insert(collection, valuesvideos);

        ParcelFileDescriptor pfd;

        try {
            pfd = getActivity().getContentResolver().openFileDescriptor(uriSavedVideo, "w");

            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());

            File imageFile = new File(path+videoName);

            FileInputStream in = new FileInputStream(imageFile);


            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);
            }


            out.close();
            in.close();
            pfd.close();



        } catch (Exception e) {

            e.printStackTrace();
        }


        valuesvideos.clear();
        valuesvideos.put(MediaStore.MediaColumns.IS_PENDING, 0);
        getActivity().getContentResolver().update(uriSavedVideo, valuesvideos, null, null);
    }




    public void deleteWaterMarkeVideo(String video_url) {

        JSONObject params = new JSONObject();
        try {
            params.put("video_url", video_url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.deleteWaterMarkVideo, params, Functions.getHeaders(getActivity()),null);


    }


    public void scanFile(String downloadDirectory) {

        MediaScannerConnection.scanFile(getActivity(),
                new String[]{downloadDirectory+item.video_id + ".mp4"},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }



    // download the video for duet with
    public void duetVideo(final HomeModel item) {

        Functions.printLog(Constants.tag, item.getVideo_url());
        if (item.getVideo_url() != null) {

            String downloadedFile=item.getVideo_url();
            if (downloadedFile.contains("file://"))
            {
                downloadedFile=item.getVideo_url().replace("file://","");
                File file=new File(downloadedFile);
                if (file.exists())
                {
                    String outputPath=Functions.getAppFolder(getActivity())+item.video_id + ".mp4";
                    copyDirectoryOneLocationToAnotherLocation(file,new File(outputPath));
                }
            }

            String deletePath=Functions.getAppFolder(getActivity())+item.video_id + ".mp4";
            File deleteFile=new File(deletePath);
            if (deleteFile.exists())
            {
                openDuetRecording(item);
                return;
            }
            Functions.showDeterminentLoader(getActivity(), false, false,true,context.getString(R.string.download_));
            PRDownloader.initialize(getActivity().getApplicationContext());
            DownloadRequest prDownloader = PRDownloader.download(item.getVideo_url(), Functions.getAppFolder(getActivity()), item.video_id + ".mp4")
                    .build()
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                            Functions.showLoadingProgress(prog);

                        }
                    });


            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Functions.cancelDeterminentLoader();

                    openDuetRecording(item);

                }

                @Override
                public void onError(Error error) {

                    Functions.printLog(Constants.tag, "Error : "+error.getConnectionException());
                    Functions.cancelDeterminentLoader();
                }


            });

        }

    }


    private void repostVideo(HomeModel item) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("repost_user_id", Functions.getSharedPreference(context).getString(Variables.U_ID,""));
            parameters.put("video_id", item.video_id);
            parameters.put("repost_comment", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.repostVideo, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        Functions.showToast(context, "Successfully repost video!");
                        if (item.repost != null && item.repost.equals("0"))
                        {
                            item.repost = "1";
                            try {
                                JSONObject msg=response.getJSONObject("msg");
                                JSONObject video=msg.getJSONObject("Video");
                                item.repost_video_id=video.optString("repost_video_id","0");
                                item.repost_user_id=video.optString("repost_user_id","0");
                            }catch (Exception e){Log.d(Constants.tag,"Exception: "+e);}
                        }
                        else
                        {
                            item.repost = "0";
                        }

                        setData();
                    }

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });
    }

    private void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
    {

        try {

            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdir();
                }

                String[] children = sourceLocation.list();
                for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                    copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                            new File(targetLocation, children[i]));
                }
            } else {

                InputStream in = new FileInputStream(sourceLocation);

                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }

        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: "+e);
        }

    }






    public void openDuetRecording(HomeModel item) {
        boolean isOpenGLSupported = Functions.isOpenGLVersionSupported(context, 0x00030001);
        if (isOpenGLSupported) {
            Intent intent = new Intent(getActivity(), VideoRecoderDuetA.class);
            intent.putExtra("data", item);
            startActivity(intent);
        } else {
            Toast.makeText(context, view.getContext().getString(R.string.your_device_opengl_verison_is_not_compatible_to_use_this_feature), Toast.LENGTH_SHORT).show();
        }
    }


    // call api for refersh the video details
    private void callApiForSinglevideos() {

        JSONObject parameters = new JSONObject();
        try {
            if (Variables.sharedPreferences.getString(Variables.U_ID, null) != null)
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.U_ID, "0"));

            parameters.put("video_id", item.video_id);

        }

        catch (Exception e) {
            e.printStackTrace();
        }


        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showVideoDetail, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                singalVideoParseData(resp);
            }
        });

    }

    // parse the data for a video
    public void singalVideoParseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");

                JSONObject video = msg.optJSONObject("Video");
                JSONObject user = msg.optJSONObject("User");
                JSONObject sound = msg.optJSONObject("Sound");
                JSONObject userprivacy = user.optJSONObject("PrivacySetting");
                JSONObject userPushNotification = user.optJSONObject("PushNotification");

                item = Functions.parseVideoData(user, sound, video, userprivacy, userPushNotification);
                setData();

            } else {
                Functions.showToast(getActivity(), jsonObject.optString("msg"));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    @Override
    public void onDataSent(String yourData) {
        int comment_count = Functions.parseInterger(yourData);
        item.video_comment_count = "" + comment_count;
        commentTxt.setText(Functions.getSuffix(item.video_comment_count));
    }



    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(view.getContext(),view.getContext().getString(R.string.we_need_camera_and_recording_permission_for_make_video_on_sound));
                    }
                    else
                    if (allPermissionClear)
                    {
                        openSoundByScreen();
                    }

                }
            });



    @Override
    public void onDetach() {
        super.onDetach();
        mPermissionResult.unregister();
    }
}
