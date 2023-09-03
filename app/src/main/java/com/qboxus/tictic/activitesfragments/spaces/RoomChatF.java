package com.qboxus.tictic.activitesfragments.spaces;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.chat.ChatModel;
import com.qboxus.tictic.activitesfragments.spaces.adapters.RoomChatAdapter;
import com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager.MainStreamingModel;
import com.qboxus.tictic.databinding.FragmentRoomChatBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener2;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class RoomChatF extends Fragment implements View.OnClickListener {

    MainStreamingModel mainStreamingModel;
    FragmentRoomChatBinding binding;

    DatabaseReference rootref;

    public static RoomChatF newInstance(MainStreamingModel mainStreamingModel, FragmentCallBack fragmentCallBack) {
        RoomChatF fragment = new RoomChatF();
        Bundle args = new Bundle();
        args.putSerializable("data",mainStreamingModel);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_room_chat, container, false);

        mainStreamingModel=(MainStreamingModel) getArguments().getSerializable("data");


        rootref = FirebaseDatabase.getInstance().getReference();

        initcontrols();

        return binding.getRoot();
    }


    public void initcontrols(){

        binding.sendbtn.setOnClickListener(this);
        binding.goBack.setOnClickListener(this);

        binding.roomTitle.setText(mainStreamingModel.getModel().getTitle()+" Chat");

        binding.msgedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.sendbtn.setVisibility(View.VISIBLE);
                }
                else {
                    binding.sendbtn.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        initAdapter();
        getChatData();
    }




    ArrayList<ChatModel> dataList=new ArrayList<>();
    RoomChatAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    public void initAdapter() {
        dataList.clear();
        linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerview.setLayoutManager(linearLayoutManager);

        binding.recyclerview.setHasFixedSize(true);

        adapter = new RoomChatAdapter(dataList, Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""), new AdapterClickListener2() {
            @Override
            public void onItemClick(View view, int pos, Object object) {

            }

            @Override
            public void onLongClick(View view, int pos, Object object) {

            }
        });
        binding.recyclerview.setAdapter(adapter);


        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                scrollOutitems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && dataList.size() > 9)) {
                    userScrolled = false;
                    rootref.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).child(Variables.roomchat).orderByChild("chat_id")
                            .endAt(dataList.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<ChatModel> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModel item = snapshot.getValue(ChatModel.class);
                                        arrayList.add(item);
                                    }
                                    for (int i = arrayList.size() - 2; i >= 0; i--) {
                                        dataList.add(0, arrayList.get(i));
                                    }

                                    adapter.notifyDataSetChanged();

                                    if (arrayList.size() > 8) {
                                        binding.recyclerview.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

    }



    ValueEventListener valueEventListener;
    ChildEventListener eventListener;
    Query queryGetchat;
    private void getChatData() {
        dataList.clear();
        queryGetchat = rootref.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).child(Variables.roomchat);

        // this will get all the messages between two users
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatModel model = dataSnapshot.getValue(ChatModel.class);
                    dataList.add(model);
                    adapter.notifyDataSetChanged();
                    binding.recyclerview.scrollToPosition(dataList.size() - 1);
                } catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    try {
                        ChatModel model = dataSnapshot.getValue(ChatModel.class);

                        for (int i = dataList.size() - 1; i >= 0; i--) {
                            if (dataList.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())) {
                                dataList.remove(i);
                                dataList.add(i, model);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e("", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Functions.printLog("", databaseError.getMessage());
            }
        };


        // this will check the two user are do chat before or not
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    binding.progressBar.setVisibility(View.GONE);
                    queryGetchat.removeEventListener(valueEventListener);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    queryGetchat.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



        queryGetchat.limitToLast(20).addChildEventListener(eventListener);
        rootref.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).child(Variables.roomchat).addValueEventListener(valueEventListener);

    }





    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.goBack:
                Functions.hideSoftKeyboard(getActivity());
                getParentFragmentManager().popBackStack();
                break;

            case R.id.sendbtn:

                if (!TextUtils.isEmpty(binding.msgedittext.getText().toString())) {
                    sendMessage(binding.msgedittext.getText().toString());
                    binding.msgedittext.setText(null);
                }

                break;
        }

    }



    // this will add the new message in chat node and update the ChatInbox by new message by present date
    public void sendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        final String current_user_ref = Variables.roomKey + "/" + mainStreamingModel.getModel().getId() + "/" + Variables.roomchat;

        DatabaseReference reference = rootref.child(Variables.roomKey).child(mainStreamingModel.getModel().getId()).child(Variables.roomchat).push();
        final String pushid = reference.getKey();

        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", mainStreamingModel.getModel().getId());
        message_user_map.put("sender_id", Functions.getSharedPreference(getContext()).getString(Variables.U_ID,""));
        message_user_map.put("sender_name", Functions.getSharedPreference(getContext()).getString(Variables.U_NAME,""));
        message_user_map.put("pic_url", Functions.getSharedPreference(getContext()).getString(Variables.U_PIC,""));
        message_user_map.put("chat_id", pushid);
        message_user_map.put("text", message);
        message_user_map.put("type", "text");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //if first message then set the visibility of whoops layout gone

            }
        });


    }




}