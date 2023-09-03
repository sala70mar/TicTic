package com.qboxus.tictic.activitesfragments.spaces;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.profile.FollowsMainTabA;
import com.qboxus.tictic.activitesfragments.profile.ProfileA;
import com.qboxus.tictic.activitesfragments.profile.ReportTypeA;
import com.qboxus.tictic.activitesfragments.profile.ShareUserProfileF;
import com.qboxus.tictic.activitesfragments.profile.settings.ShowLocationPermissionF;
import com.qboxus.tictic.activitesfragments.spaces.adapters.ProfileSuggestionAdapter;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.activitesfragments.spaces.models.UserSuggestionModel;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.databinding.FragmentOtherUserProfileBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.InviteForSpeakModel;
import com.qboxus.tictic.models.PrivacyPolicySettingModel;
import com.qboxus.tictic.models.PushNotificationSettingModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class OtherUserProfileF extends BottomSheetDialogFragment implements View.OnClickListener {


    FragmentOtherUserProfileBinding binding;
    HomeUserModel myUserModel;
    UserModel selectedModel;
    FragmentCallBack callBack;
    DatabaseReference reference;
    ArrayList<UserSuggestionModel> dataList = new ArrayList<>();
    ProfileSuggestionAdapter adapter;
    public boolean isDirectMessage=false;

    boolean isSuggested=false;
    boolean isInvitedAsSpeaker=false;



    String roomId;
    ArrayList<HomeUserModel> currentUserList;
    String roleType;
    String userId;

    String isUserAlreadyBlock = "0";
    String blockByUserId = "0";

    public OtherUserProfileF(UserModel userModel, String roomId, String roleType, ArrayList<HomeUserModel> currentUserList, FragmentCallBack callBack) {
        this.userId=userModel.getId();
        this.selectedModel = userModel;
        this.callBack = callBack;
        this.roomId=roomId;
        this.roleType=roleType;
        this.currentUserList=currentUserList;
    }


    public OtherUserProfileF() {
        // Required empty public constructor
    }

    public static OtherUserProfileF newInstance(UserModel userModel, String roomId, String roleType, ArrayList<HomeUserModel> currentUserList, FragmentCallBack callBack) {
        OtherUserProfileF fragment = new OtherUserProfileF(userModel,roomId,roleType,currentUserList,callBack);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_user_profile, container, false);
        initControl();
        return binding.getRoot();
    }

    private void initControl() {
        binding.tabProfile.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabProfile);
        binding.tabFollow.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabFollow);
        binding.tabSuggestion.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabSuggestion);
        binding.ivMenu.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.ivMenu);
        binding.ivClose.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.ivClose);
        binding.tabChat.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabChat);
        binding.tabViewProfile.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabViewProfile);
        binding.tabFollowers.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabFollowers);
        binding.tabFollowerings.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabFollowerings);
        binding.tabMoveToAduiance.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabMoveToAduiance);
        binding.tabInviteToSpeak.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabInviteToSpeak);
        binding.tabMakeAModerator.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabMakeAModerator);
        binding.tabWave.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabWave);

        reference= FirebaseDatabase.getInstance().getReference();


        setupSuggestionList();
        getSuggestedFollowers();

        if(selectedModel!=null)
        {
            setUpScreenData();
        }

        hitUserprofileDetail();

    }



    private void setupSuggestionList() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(binding.getRoot().getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerview.setLayoutManager(layoutManager);
        adapter=new ProfileSuggestionAdapter(dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                UserSuggestionModel itemUpdate=dataList.get(pos);
                switch (view.getId())
                {
                    case R.id.tabFollow:
                    {
                        followUnFollowUser();
                    }
                    break;
                    case R.id.tabProfile:
                    {
                        openProfile(itemUpdate.getUserModel().getId());
                    }
                    break;
                    case R.id.tabRemove:
                    {
                        dataList.remove(pos);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        });
        binding.recyclerview.setAdapter(adapter);
    }





    public void followUnFollowUser() {
        Functions.callApiForFollowUnFollow(getActivity(),
                Functions.getSharedPreference(getActivity()).getString(Variables.U_ID, ""),
                userId,
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {
                    }

                    @Override
                    public void onSuccess(String responce) {

                        hitUserprofileDetail();
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });

    }


    private void openProfile(String id) {
        dismiss();
        Intent intent=new Intent(getActivity(), ProfileA.class);
        intent.putExtra("user_id", selectedModel.getId());
        intent.putExtra("user_name", selectedModel.getUsername());
        intent.putExtra("user_pic", selectedModel.getProfilePic());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }




    private void getSuggestedFollowers() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id",  Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""));
            parameters.put("starting_point", "0");
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showSuggestedUsers, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                parseResponseData(resp);
            }
        });


    }

    private void parseResponseData(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray jsonObj = jsonObject.getJSONArray("msg");
                dataList.clear();

                for (int i=0;i<jsonObj.length();i++)
                {
                    JSONObject innerObject=jsonObj.getJSONObject(i);
                    UserModel userDetailModel= DataParsing.getUserDataModel(innerObject.getJSONObject("User"));
                    UserSuggestionModel model=new UserSuggestionModel();
                    model.setUserModel(userDetailModel);
                    dataList.add(model);
                }
                adapter.notifyDataSetChanged();

            }

            if (dataList.isEmpty()) {
                binding.tabNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(binding.getRoot().getContext().getString(R.string.no_user_suggestion_available));
            } else {
                binding.tabNoData.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }
    }


    private void setUpScreenData() {

        if(roomId!=null) {
            setupButtonLogic();

            if (roleType.equals("1"))
            {
                binding.tabModerator.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.tabModerator.setVisibility(View.GONE);
            }
        }

        binding.ivProfile.setController(Functions.frescoImageLoad(binding.getRoot().getContext(),
                Functions.getUserName(selectedModel),selectedModel.getProfilePic(),binding.ivProfile));

        if ( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"").equals(selectedModel.getId()))
        {
            binding.ivMenu.setVisibility(View.GONE);
            binding.tabFollowSuggestion.setVisibility(View.INVISIBLE);
        }
        else
        {
            binding.ivMenu.setVisibility(View.VISIBLE);
            binding.tabFollowSuggestion.setVisibility(View.VISIBLE);
        }

        binding.tvFullName.setText(selectedModel.getFirstName()+" "+selectedModel.getLastName());
        binding.tvUsername.setText(Functions.showUsername(selectedModel.getUsername()));







        binding.tvFollowersCount.setText(""+selectedModel.getFollowersCount());
        binding.tvFolloweringsCount.setText(""+selectedModel.getFollowingCount());

        binding.tvJoinDate.setText(binding.getRoot().getContext().getString(R.string.joined)+" "+Functions.ChangeDateFormat("yyyy-MM-dd HH:mm:ss","EEE MM, yyyy",selectedModel.getCreated()));


        isUserAlreadyBlock = selectedModel.getBlock();
        blockByUserId = selectedModel.getBlockByUser();



        updateFollowButtonStatus();

    }

    private void setupButtonLogic() {
        for(HomeUserModel myModel:currentUserList)
        {
            if (myModel.getUserModel().getId().equals( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""))) {
                myUserModel=myModel;
            }
        }

        //moderator
        if (myUserModel!=null && myUserModel.getUserRoleType().equals("1"))
        {
            if (selectedModel.getId().equals( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.VISIBLE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);
            }
            else
            if (roleType.equals("1"))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.VISIBLE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);
            }
            else
            if (roleType.equals("2"))
            {
                binding.tabMakeAModerator.setVisibility(View.VISIBLE);
                binding.tabMoveToAduiance.setVisibility(View.VISIBLE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);
                binding.tabViewProfile.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.VISIBLE);
            }
        }
        else
            //speaker
        if (myUserModel!=null && myUserModel.getUserRoleType().equals("2"))
        {
            if (selectedModel.getId().equals( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.VISIBLE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);

            }
            else
            if (roleType.equals("1"))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);

            }
            else
            if (roleType.equals("2"))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);

            }
            else
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);

            }
        }

        else
            //user
        {
            if (selectedModel.getId().equals( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);
                binding.tabViewProfile.setVisibility(View.VISIBLE);
                checkModeratorInvitation();
            }
            else
            if (roleType.equals("1"))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);

            }
            else
            if (roleType.equals("2"))
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);

            }
            else
            {
                binding.tabMakeAModerator.setVisibility(View.GONE);
                binding.tabMoveToAduiance.setVisibility(View.GONE);
                binding.tabInviteToSpeak.setVisibility(View.GONE);

            }
        }
    }

    private void checkModeratorInvitation() {
        reference.child(Variables.roomKey).child(roomId).
                child(Variables.roomInvitation)
                .child( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    InviteForSpeakModel invitation=snapshot.getValue(InviteForSpeakModel.class);
                    if (invitation.getInvite().equals("1"))
                    {
                        registerSpeakInvitationListener();
                        isInvitedAsSpeaker=true;
                        binding.tabInviteToSpeak.setVisibility(View.VISIBLE);
                        binding.tvInviteToSpeak.setText(binding.getRoot().getContext().getString(R.string.accept_speaking_invitation));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivMenu:
            {
                showSettingMenu();
            }
            break;
            case R.id.ivClose:
            {
                dismiss();
            }
            break;
            case R.id.tabChat:
            {
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                bundle.putString("action","openChat");
                bundle.putSerializable("data",selectedModel);
                callBack.onResponce(bundle);
                dismiss();
            }
            break;
            case R.id.tabMoveToAduiance:
            {
                if (checkIAmTheSingleModerator())
                {
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("isShow",true);
                    bundle.putString("action","moveToAudience");
                    bundle.putSerializable("data",selectedModel);
                    callBack.onResponce(bundle);
                    dismiss();
                }
            }
            break;
            case R.id.tabInviteToSpeak:
            {
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                if (isInvitedAsSpeaker)
                {
                    bundle.putString("action","acceptInviteToSpeaker");
                }
                else
                {
                    bundle.putString("action","inviteToSpeaker");
                }
                bundle.putSerializable("data",selectedModel);
                callBack.onResponce(bundle);

                dismiss();
            }
            break;
            case R.id.tabMakeAModerator:
            {
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                bundle.putString("action","makeToModerator");
                bundle.putSerializable("data",selectedModel);
                callBack.onResponce(bundle);
                dismiss();
            }
            break;
            case R.id.tabWave:
            {
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                bundle.putString("action","wave");
                bundle.putSerializable("data",selectedModel);
                callBack.onResponce(bundle);
                dismiss();
            }
            break;
            case R.id.tabViewProfile:
            {
                openProfile(selectedModel.getId());
            }
            break;

            case R.id.tabSuggestion:
            {
                if (binding.tabSuggestionUser.getVisibility()==View.VISIBLE)
                {
                    binding.tabSuggestionUser.setVisibility(View.GONE);
                    binding.tabSuggestion.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.button_rounded_background));
                    binding.ivSuggestion.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.ivSuggestion.setRotation(0);
                }
                else
                {
                    binding.tabSuggestionUser.setVisibility(View.VISIBLE);
                    binding.tabSuggestion.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.ractengle_gray_on_black));
                    binding.ivSuggestion.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(),R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.ivSuggestion.setRotation(180);
                }
            }
            break;

            case R.id.tabFollowers:
            {

                openFollowers();
                dismiss();
            }
            break;
            case R.id.tabFollowerings:
            {
                openFollowings();
                dismiss();
            }
            break;
            case R.id.tabFollow:
            {
                followUnFollowUser();
            }
            break;

            default:
                break;
        }
    }


    private void updateFollowButtonStatus() {
        if (selectedModel.getButton().toLowerCase().equals("follow") || selectedModel.getButton().toLowerCase().equals("follow back"))
        {
            binding.tabFollow.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.button_rounded_solid_primary));
            binding.tvFollow.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.whiteColor));
            binding.tvFollow.setText("Follow");

        }
        else {
            binding.tabFollow.setBackground(ContextCompat.getDrawable(binding.getRoot().getContext(),R.drawable.button_rounded_gray_strok_background));
            binding.tvFollow.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.appColor));
            binding.tvFollow.setText("Following");
        }


        if (selectedModel.getButton().toLowerCase().equals("friends") || selectedModel.getButton().toLowerCase().equals("follow back")) {
            binding.tabWave.setVisibility(View.VISIBLE);
        }

        else {
            binding.tabWave.setVisibility(View.GONE);
        }



    }


    private void increateFollowingCount() {
        int followCount=Integer.valueOf(selectedModel.getFollowingCount());
        followCount=followCount+1;
        selectedModel.setFollowingCount(""+followCount);
        binding.tvFolloweringsCount.setText(""+selectedModel.getFollowingCount());
    }


    private void decreateFollowingCount() {
        int followCount=Integer.valueOf(selectedModel.getFollowingCount());
        followCount=followCount-1;
        selectedModel.setFollowingCount(""+followCount);
        binding.tvFolloweringsCount.setText(""+selectedModel.getFollowingCount());
    }


    private boolean checkIAmTheSingleModerator() {
        int countModerator=0;
        int countSpeaker=0;

        HomeUserModel speakerModel=null;

        for (HomeUserModel moderatorModel:currentUserList)
        {
            if (moderatorModel.getUserRoleType().equals("1"))
            {
                countModerator=countModerator+1;
            }
            if (moderatorModel.getUserRoleType().equals("2"))
            {
                countSpeaker=countSpeaker+1;

                if (countSpeaker==1)
                {
                    speakerModel=moderatorModel;
                }
            }
        }

        if (countModerator<2)
        {
            if (countSpeaker<1)
            {
                Functions.showError(getActivity(),binding.getRoot().getContext().getString(R.string.you_are_the_only_speaker_so_you_cant_go_to_the_audience));
                dismiss();
                return false;
            }
            else
            {
//                moderation assign to top speaker
                if (speakerModel!=null)
                {
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("isShow",true);
                    bundle.putString("action","makeModeratorToSpeakerAndLeave");
                    bundle.putSerializable("speakerModel",speakerModel);
                    callBack.onResponce(bundle);
                    dismiss();
                    return false;
                }

            }

        }


        return true;
    }


    TextView tvBlockUser;
    private void showSettingMenu() {
        final Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.item_report_user_dialog);

        RelativeLayout tabReportUser = alertDialog.findViewById(R.id.tabReportUser);
        RelativeLayout tabBlockUser = alertDialog.findViewById(R.id.tabBlockUser);
        RelativeLayout tabShareProfile=alertDialog.findViewById(R.id.tabShareProfile);
        tvBlockUser = alertDialog.findViewById(R.id.tvBlockUser);

        Log.d(Constants.tag,"blockObj: "+blockByUserId);
        Log.d(Constants.tag,"isUserAlreadyBlock: "+isUserAlreadyBlock);

        if (blockByUserId.equals(Functions.getSharedPreference(getActivity()).getString(Variables.U_ID,"")))
        {
            tabBlockUser.setVisibility(View.VISIBLE);
        }
        else
        {
            if (isUserAlreadyBlock.equals("1"))
            {
                tabBlockUser.setVisibility(View.GONE);
            }
            else
            {
                tabBlockUser.setVisibility(View.VISIBLE);
            }

        }

        if (isUserAlreadyBlock.equals("1"))
            tvBlockUser.setText(getContext().getString(R.string.unblock_user));
        else
            tvBlockUser.setText(getContext().getString(R.string.block_user));

        tabShareProfile.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (Functions.checkLoginUser(getActivity())) {
                shareProfile();
            }
        });
        tabReportUser.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (Functions.checkLoginUser(getActivity())) {
                openUserReport();
            }
        });


        tabBlockUser.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (Functions.checkLoginUser(getActivity())) {
                openBlockUserDialog();
            }
        });

        alertDialog.show();

    }



    private void openBlockUserDialog() {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id",
                    Functions.getSharedPreference(getActivity()).getString(Variables.U_ID, ""));
            params.put("block_user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Functions.showLoader(getActivity(), false, false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.blockUser, params,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        if(msgObj.has("BlockUser"))
                        {
                            Functions.showToast(getActivity(), getString(R.string.user_blocked));
                            tvBlockUser.setText(R.string.unblock_user);
                            isUserAlreadyBlock = "1";
                        }
                        else
                        {
                            isUserAlreadyBlock = "0";
                        }
                    }
                    else
                    {
                        isUserAlreadyBlock = "0";
                    }
                    hitUserprofileDetail();
                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });

    }


    private void shareProfile() {
        boolean fromSetting=false;
        if (userId.equalsIgnoreCase(Functions.getSharedPreference(getActivity()).getString(Variables.U_ID,"")))
        {
            fromSetting=true;
        }
        else
        {
            fromSetting=false;
        }

        final ShareUserProfileF fragment = new ShareUserProfileF(userId,selectedModel.getUsername(),selectedModel.getFirstName()+" "+selectedModel.getLastName(),
                selectedModel.getProfilePic(),selectedModel.getButton(),isDirectMessage,fromSetting, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    hitUserprofileDetail();
                }
            }
        });
        fragment.show(getChildFragmentManager(), "");

    }




    public void openUserReport() {
        Intent intent = new Intent(getActivity(), ReportTypeA.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("isFrom", false);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }






    private void openFollowings() {

        Intent intent=new Intent(getActivity(), FollowsMainTabA.class);
        intent.putExtra("id", userId);
        intent.putExtra("from_where", "following");
        intent.putExtra("userName", selectedModel.getUsername());
        intent.putExtra("followingCount",""+selectedModel.getFollowingCount());
        intent.putExtra("followerCount",""+selectedModel.getFollowersCount());
        resultFollowCallback.launch(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }

    // open the followers screen
    private void openFollowers() {

        Intent intent=new Intent(getActivity(), FollowsMainTabA.class);
        intent.putExtra("id", userId);
        intent.putExtra("from_where", "fan");
        intent.putExtra("userName", selectedModel.getUsername());
        intent.putExtra("followingCount",""+selectedModel.getFollowingCount());
        intent.putExtra("followerCount",""+selectedModel.getFollowersCount());
        resultFollowCallback.launch(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    ActivityResultLauncher<Intent> resultFollowCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            hitUserprofileDetail();
                        }

                    }
                }
            });



    private void hitUserprofileDetail() {
        JSONObject parameters = new JSONObject();
        try {
            if (userId.equals( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")))
            {
                parameters.put("user_id",  Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""));
            }
            else
            {
                parameters.put("user_id", Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""));
                parameters.put("other_user_id",userId);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: hitUserprofileDetail "+e);
        }

        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.showUserDetail, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                parseUserDetailRes(resp);
            }
        });


    }

    private void parseUserDetailRes(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject jsonObj = jsonObject.getJSONObject("msg");
                UserModel userDetailModel= DataParsing.getUserDataModel(jsonObj.getJSONObject("User"));

                selectedModel=userDetailModel;

                JSONObject push_notification_setting = jsonObj.optJSONObject("PushNotification");
                JSONObject privacy_policy_setting = jsonObj.optJSONObject("PrivacySetting");

                PushNotificationSettingModel pushNotificationSetting_model = new PushNotificationSettingModel();
                pushNotificationSetting_model.setComments("" + push_notification_setting.optString("comments"));
                pushNotificationSetting_model.setLikes("" + push_notification_setting.optString("likes"));
                pushNotificationSetting_model.setNewfollowers("" + push_notification_setting.optString("new_followers"));
                pushNotificationSetting_model.setMentions("" + push_notification_setting.optString("mentions"));
                pushNotificationSetting_model.setDirectmessage("" + push_notification_setting.optString("direct_messages"));
                pushNotificationSetting_model.setVideoupdates("" + push_notification_setting.optString("video_updates"));


                PrivacyPolicySettingModel privacyPolicySetting_model = new PrivacyPolicySettingModel();
                privacyPolicySetting_model.setVideos_download("" + privacy_policy_setting.optString("videos_download"));
                privacyPolicySetting_model.setDirect_message("" + privacy_policy_setting.optString("direct_message"));
                privacyPolicySetting_model.setDuet("" + privacy_policy_setting.optString("duet"));
                privacyPolicySetting_model.setLiked_videos("" + privacy_policy_setting.optString("liked_videos"));
                privacyPolicySetting_model.setVideo_comment("" + privacy_policy_setting.optString("video_comment"));


                if (Functions.isShowContentPrivacy(getActivity(), privacyPolicySetting_model.getDirect_message(),
                        selectedModel.getButton().toLowerCase().equalsIgnoreCase("friends"))) {
                    isDirectMessage=true;
                } else {
                    isDirectMessage=false;
                }

                setUpScreenData();
            }
            else {
                Functions.showError(getActivity(),jsonObject.optString("msg"));
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }
    }



    ValueEventListener speakInvitationListener;
    private void registerSpeakInvitationListener() {
        if(speakInvitationListener ==null)
        {
            speakInvitationListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(Constants.tag,"roomUpdateListener : "+dataSnapshot);
                    if (dataSnapshot.exists())
                    {
                        InviteForSpeakModel invitation=dataSnapshot.getValue(InviteForSpeakModel.class);
                        if (invitation.getInvite().equals("1"))
                        {
                            isInvitedAsSpeaker=true;
                            binding.tabInviteToSpeak.setVisibility(View.VISIBLE);
                            binding.tvInviteToSpeak.setText(binding.getRoot().getContext().getString(R.string.accept_speaking_invitation));
                        }
                        else
                        {
                            isInvitedAsSpeaker=false;
                            binding.tabInviteToSpeak.setVisibility(View.GONE);
                            binding.tvInviteToSpeak.setText(binding.getRoot().getContext().getString(R.string.invite_to_speak));
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.child(Variables.roomKey).child(roomId).
                    child(Variables.roomInvitation)
                    .child( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")).addValueEventListener(speakInvitationListener);

        }
    }
    public void removeSpeakInvitationListener() {
        if (reference!=null && speakInvitationListener != null) {
            reference.child(Variables.roomKey).child(roomId).
                    child(Variables.roomInvitation)
                    .child( Functions.getSharedPreference(getContext()).getString(Variables.U_ID,"")).removeEventListener(speakInvitationListener);
            speakInvitationListener =null;
        }
    }


    @Override
    public void onDetach() {
        removeSpeakInvitationListener();
        super.onDetach();
    }
}