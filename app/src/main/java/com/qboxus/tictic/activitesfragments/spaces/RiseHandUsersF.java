package com.qboxus.tictic.activitesfragments.spaces;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.spaces.adapters.RiseHandUsersAdapter;
import com.qboxus.tictic.activitesfragments.spaces.models.HomeUserModel;
import com.qboxus.tictic.databinding.FragmentRiseHandUsersBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.realpacific.clickshrinkeffect.ClickShrinkUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class RiseHandUsersF extends BottomSheetDialogFragment implements View.OnClickListener {

    FragmentRiseHandUsersBinding binding;
    RiseHandUsersAdapter adapter;
    ArrayList<HomeUserModel> currentUserList=new ArrayList<>();
    FragmentCallBack callBack;
    DatabaseReference reference;
    String roomId,riseHandRule;

    public RiseHandUsersF(String roomId,String riseHandRule, FragmentCallBack callBack) {
        this.roomId=roomId;
        this.riseHandRule=riseHandRule;
        this.callBack = callBack;
    }

    public RiseHandUsersF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_rise_hand_users, container, false);
        initControl();
        return binding.getRoot();
    }

    private void initControl() {
        reference= FirebaseDatabase.getInstance().getReference();
        binding.tvEdit.setOnClickListener(this);
        ClickShrinkUtils.applyClickShrink(binding.tvEdit);



        setupAdapter();
        setupScreenData();
    }

    private void setupScreenData() {
        String rule="";
        Functions.printLog(Constants.tag,"riseHandRule:"+riseHandRule);

        if (riseHandRule.equals("1")) {

            rule=binding.getRoot().getContext().getString(R.string.open_to_everyone);
            registerMyRoomListener();
        }

        else {
            rule=binding.getRoot().getContext().getString(R.string.off);
            removeMyRoomListener();
            currentUserList.clear();
            adapter.notifyDataSetChanged();
        }

        binding.tvRaiseRule.setText(""+rule);
    }

    private void setupAdapter() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(binding.getRoot().getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(layoutManager);
        adapter=new RiseHandUsersAdapter(currentUserList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                HomeUserModel itemUpdate=currentUserList.get(pos);

                if (view.getId()==R.id.ivProfile)
                {

                }
                else
                if (view.getId()==R.id.tabAddToSpeak)
                {
                    if (itemUpdate.getRiseHand().equals("1"))
                    {
                       sendInviteToUserForSpeak(itemUpdate,pos);
                    }

                }


            }
        });
        binding.recylerview.setAdapter(adapter);
    }

    private void sendInviteToUserForSpeak(HomeUserModel itemUpdate,int pos) {
        itemUpdate.setRiseHand("2");
        reference.child(Variables.roomKey)
                .child(roomId).child(Variables.roomUsers)
                .child(itemUpdate.getUserModel().getId())
                .setValue(itemUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Bundle bundle=new Bundle();
                            bundle.putBoolean("isShow",true);
                            bundle.putString("action","invite");
                            bundle.putSerializable("itemModel",itemUpdate);
                            callBack.onResponce(bundle);

                            currentUserList.set(pos,itemUpdate);
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
    }

    ChildEventListener myRoomListener;
    private void registerMyRoomListener() {
        if(myRoomListener ==null)
        {
            myRoomListener =new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        HomeUserModel itemModel=snapshot.getValue(HomeUserModel.class);
                        if (itemModel.getRiseHand().equals("1"))
                        {
                            currentUserList.add(itemModel);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (!(TextUtils.isEmpty(snapshot.getValue().toString())))
                    {
                        HomeUserModel itemModel=snapshot.getValue(HomeUserModel.class);

                            int indexPostion = getlistPostion(currentUserList, itemModel);
                            if(indexPostion>=0) {

                                if (itemModel.getRiseHand().equals("0")) {
                                    currentUserList.remove(indexPostion);
                                }
                                else {
                                    currentUserList.set(indexPostion, itemModel);
                                }

                                adapter.notifyDataSetChanged();
                            }
                            else  if (!itemModel.getRiseHand().equals("0"))
                            {
                                currentUserList.add(itemModel);
                                adapter.notifyDataSetChanged();
                            }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        HomeUserModel dataItem=snapshot.getValue(HomeUserModel.class);
                        int indexPostion=getlistPostion(currentUserList,dataItem);
                        if(indexPostion>=0) {
                            currentUserList.remove(indexPostion);
                            adapter.notifyItemRemoved(indexPostion);
                        }
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            reference.child(Variables.roomKey).child(roomId).child(Variables.roomUsers).addChildEventListener(myRoomListener);

        }
        else
        {
            Log.d(Constants.tag,"myRoomListener not null");
        }
    }
    public void removeMyRoomListener() {
        if (reference!=null && myRoomListener != null) {
            reference.child(Variables.roomKey).child(roomId).removeEventListener(myRoomListener);
            myRoomListener =null;
        }
    }


    private int getlistPostion(ArrayList<HomeUserModel> currentUserList, HomeUserModel dataItem) {
        for (int i=0;i<currentUserList.size();i++)
        {
            if (currentUserList.get(i).getUserModel().getId().equals(dataItem.getUserModel().getId()))
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDetach() {


        removeMyRoomListener();
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvEdit:
            {
                openEditMenu();
            }
            break;
        }
    }

    private void openEditMenu() {
        Context wrapper = new ContextThemeWrapper(binding.getRoot().getContext(), R.style.AlertDialogCustom);
        PopupMenu popup = new PopupMenu(wrapper, binding.tvEdit);

        popup.getMenuInflater().inflate(R.menu.room_rise_hand_rule, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.setGravity(Gravity.TOP | Gravity.END);
        }

        MenuItem itemOne=popup.getMenu().getItem(0);
        MenuItem itemTwo=popup.getMenu().getItem(1);

        Functions.printLog(Constants.tag,"openEditMenu:riseHandRule:"+riseHandRule);

        if (riseHandRule.equals("1")) {
            itemOne.setChecked(false);
            itemTwo.setChecked(true);
        }
        else {
            itemOne.setChecked(true);
            itemTwo.setChecked(false);
        }
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.itemOff: {

                        if (!(itemOne.isChecked())) {

                            HashMap<String,Object> updateRiseRuleMap=new HashMap<>();
                            updateRiseRuleMap.put("riseHandRule","0");
                            reference.child(Variables.roomKey).child(roomId).updateChildren(updateRiseRuleMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        riseHandRule="0";
                                        Functions.showError(getActivity(),binding.getRoot().getContext().getString(R.string.hand_raising_was_turned_off_by_moderators_of_the_room));
                                        setupScreenData();
                                    }
                                }
                            });
                        }

                    }
                    break;
                    case R.id.itemEveryone:
                    {
                        if (!(itemTwo.isChecked())) {

                            HashMap<String,Object> updateRiseRuleMap=new HashMap<>();
                            updateRiseRuleMap.put("riseHandRule","1");
                            reference.child(Variables.roomKey).child(roomId).updateChildren(updateRiseRuleMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        riseHandRule="1";
                                        setupScreenData();
                                    }
                                }
                            });

                        }
                    }
                    break;
                }
                return true;
            }
        });

    }
}