package com.qboxus.tictic.activitesfragments.spaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.models.GroupModel;
import com.qboxus.tictic.activitesfragments.spaces.models.TopicModel;
import com.qboxus.tictic.databinding.FragmentCreateRoomBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.Functions;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;

import java.util.ArrayList;


public class CreateRoomF extends BottomSheetDialogFragment implements View.OnClickListener {


    FragmentCallBack fragmentCallback;
    FragmentCreateRoomBinding binding;
    GroupModel groupModel;
    int width=0;

    DatabaseReference reference;


    public CreateRoomF() {
    }

    public CreateRoomF(FragmentCallBack fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_room, container, false);
        InitControl();
        return binding.getRoot();
    }


    private void InitControl() {
        width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        groupModel=new GroupModel();
        reference= FirebaseDatabase.getInstance().getReference();
        binding.tabGenrateGroup.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabGenrateGroup);
        binding.tabChoosePeople.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tabChoosePeople);

        binding.addTopicTxt.setOnClickListener(this);

        binding.addTopicbtn.setOnClickListener(this);
        setUpScreenData();
    }

    private void setUpScreenData() {
        groupModel.setPrivacyType("0");
        groupModel.setName("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tabGenrateGroup:
            {
                groupModel.name= binding.titleEdit.getText().toString();
                if(TextUtils.isEmpty(groupModel.name)){
                    Functions.showError(getActivity(),"Please enter room title!");
                }
                else if(selectedTopics.isEmpty()){
                    Functions.showError(getActivity(),"Please select the topic!");
                }
                else {
                    Bundle bundleGenrate = new Bundle();
                    bundleGenrate.putBoolean("isShow", false);
                    bundleGenrate.putString("action", "genrateRoom");
                    bundleGenrate.putSerializable("groupModel", groupModel);
                    bundleGenrate.putString("roomName",""+groupModel.getName());
                    bundleGenrate.putString("privacyType",""+groupModel.getPrivacyType());
                    bundleGenrate.putSerializable("selectedFriends",selectedFriends);
                    bundleGenrate.putSerializable("topics",selectedTopics);
                    fragmentCallback.onResponce(bundleGenrate);
                    dismiss();
                }
            }
            break;

            case R.id.tabChoosePeople: {
                addFriendsToRoom();
            }
            break;

            case R.id.addTopicbtn:
            case R.id.addTopicTxt:
                addTopics();
                break;


        }
    }


    ArrayList<TopicModel> selectedTopics=new ArrayList<>();
    private void addTopics() {
        Intent intent=new Intent(getActivity(), InterestPreferenceA.class);
        resultCallback.launch(intent);
    }

    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            selectedTopics = (ArrayList<TopicModel>) data.getSerializableExtra("dataList");

                            if (selectedTopics.size()>0)
                            {
                                binding.topicListLayout.setVisibility(View.VISIBLE);
                                binding.addTopicTxt.setVisibility(View.GONE);
                            }
                            else
                            {
                                binding.topicListLayout.setVisibility(View.GONE);
                                binding.addTopicTxt.setVisibility(View.VISIBLE);
                            }

                            addTopicItem();
                        }
                    }
                }
            });


    public void addTopicItem(){
        binding.topicList.removeAllViews();
        for (int i=0;i<selectedTopics.size();i++)
        {
            TopicModel itemModel=selectedTopics.get(i);

            RelativeLayout tabTag = (RelativeLayout) LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.item_topic, null);
            LinearLayout innerView=tabTag.findViewById(R.id.innerView);
            SimpleDraweeView ivTag=innerView.findViewById(R.id.ivTag);
            View ivFrameTag=innerView.findViewById(R.id.ivFrameTag);
            TextView tvTag=innerView.findViewById(R.id.tvTag);
            tvTag.setText(""+itemModel.getTitle());

            tabTag.setTag(i);
            ivTag.setController(Functions.frescoImageLoad(binding.getRoot().getContext(),
                    ""+itemModel.getTitle(),
                    (int) binding.getRoot().getContext().getResources().getDimension(R.dimen._9sdp)
                    ,itemModel.getImage(),ivTag));

            tvTag.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(),
                    R.color.white));
            tabTag.setActivated(true);
            ivFrameTag.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.appColor));

            binding.topicList.addView(tabTag);
        }

    }





    ArrayList<UserModel> selectedFriends;
    private void addFriendsToRoom() {
        final AddFriendsSelectionF fragment = new AddFriendsSelectionF(bundle -> {
            if (bundle.getBoolean("isShow",false))
            {
                selectedFriends= (ArrayList<UserModel>) bundle.getSerializable("UserList");

                if (selectedFriends.size()>0)
                {
                    binding.tabGenrateGroup.setVisibility(View.VISIBLE);
                    binding.tabChoosePeople.setVisibility(View.GONE);
                }
                else
                {
                    binding.tabGenrateGroup.setVisibility(View.GONE);
                    binding.tabChoosePeople.setVisibility(View.VISIBLE);
                }

            }
        },false);
        fragment.show(getChildFragmentManager(), "AddFriendsSelectionF");
    }




}