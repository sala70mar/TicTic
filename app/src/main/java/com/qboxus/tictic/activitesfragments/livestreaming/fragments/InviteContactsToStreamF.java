package com.qboxus.tictic.activitesfragments.livestreaming.fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.activitesfragments.livestreaming.adapter.Contacts_Adapter;
import com.qboxus.tictic.activitesfragments.livestreaming.model.ContactsDataModel;
import com.qboxus.tictic.activitesfragments.livestreaming.model.Group_member_GetSet;
import com.qboxus.tictic.adapters.ProfileSharingAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.ShareAppModel;
import com.qboxus.tictic.models.StreamInviteModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class InviteContactsToStreamF extends BottomSheetDialogFragment {


    ArrayList<ContactsDataModel> userlist=new ArrayList<>();
    HashMap<String,ContactsDataModel> followerlist=new HashMap<>();
    RecyclerView userRecylerview,recylerviewShareToSocial;
    Contacts_Adapter contactsAdapter;
    ProfileSharingAdapter adapterShareToSocial;
    ProgressBar progressBar;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;
    View view;
    Context context;
    int[] androidColors;
    DatabaseReference rootref;
    ImageView ivBack;
    EditText etSearch;
    TextView tvSuggested;
    TextView search_btn;
    int pageCount = 0;
    FragmentCallBack refreshCallback;
    ArrayList<Group_member_GetSet> allMembersArrylist=new ArrayList<>();
    String streamingId;
    String streamType;
    ImageView ivInviteAll;
    HashMap<String,ContactsDataModel> inviteUserMapList=new HashMap<>();



    private BottomSheetBehavior mBehavior;
    BottomSheetDialog dialog;


    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_invite_contacts_to_stream, null);
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



    public InviteContactsToStreamF(String streamingId,String streamType,FragmentCallBack refreshCallback) {
        this.streamingId=streamingId;
        this.streamType=streamType;
        this.refreshCallback = refreshCallback;
    }

    public InviteContactsToStreamF() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_invite_contacts_to_stream, container, false);
        context = view.getContext();
        rootref = FirebaseDatabase.getInstance().getReference();
        progressBar = view.findViewById(R.id.pbar);
        ivInviteAll=view.findViewById(R.id.ivInviteAll);
        androidColors = context.getResources().getIntArray(R.array.bg_color_array);
        progressBar = view.findViewById(R.id.pbar);
        search_btn=view.findViewById(R.id.search_btn);
        tvSuggested=view.findViewById(R.id.tvSuggested);
        ivBack = view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ivInviteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendStreamingMultipleInvite();
            }
        });

        if (streamType.equals("single"))
        {
            ivInviteAll.setVisibility(View.GONE);
        }
        else
        {
            ivInviteAll.setVisibility(View.VISIBLE);
        }
        getOwnSharedApp();
        setupRecyclerAdapter();
        setupSearchEditText();

        progressBar.setVisibility(View.VISIBLE);
        loadMoreProgress = view.findViewById(R.id.load_more_progress);
        pageCount=0;
        callApi();

        return view;
    }

    public void getOwnSharedApp() {
        recylerviewShareToSocial = (RecyclerView) view.findViewById(R.id.recylerviewShareToSocial);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recylerviewShareToSocial.setLayoutManager(layoutManager);
        recylerviewShareToSocial.setHasFixedSize(true);
        adapterShareToSocial = new ProfileSharingAdapter(context, getAppShareDataList(), new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                ShareAppModel item= (ShareAppModel) object;

                shareProfile(item);
            }
        });
        recylerviewShareToSocial.setAdapter(adapterShareToSocial);
    }

    public void shareProfile(ShareAppModel item) {
        String streamingLink = Variables.https+"://"+getString(R.string.share_profile_domain_second)+getString(R.string.share_stream_endpoint_second) + streamingId;
        if (item.getName().equalsIgnoreCase(view.getContext().getString(R.string.whatsapp)))
        {

            try {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, streamingLink);
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            } catch(Exception e) {
                Log.d(Constants.tag,"Exception : "+e);
            }
        }
        else
        if (item.getName().equalsIgnoreCase(view.getContext().getString(R.string.facebook)))
        {
            try {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, streamingLink);
                sendIntent.setPackage("com.facebook.katana");
                startActivity(sendIntent);
            } catch(Exception e) {
                Log.d(Constants.tag,"Exception : "+e);
            }
        }
        else
        if (item.getName().equalsIgnoreCase(view.getContext().getString(R.string.messenger)))
        {
            try {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, streamingLink);
                sendIntent.setPackage("com.facebook.orca");
                startActivity(sendIntent);
            } catch(Exception e) {
                Log.d(Constants.tag,"Exception : "+e);
            }
        }
        else
        if (item.getName().equalsIgnoreCase(view.getContext().getString(R.string.sms)))
        {
            try {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("sms_body",""+streamingLink);
                startActivity(smsIntent);
            } catch(Exception e) {
                Log.d(Constants.tag,"Exception : "+e);
            }
        }
        else
        if (item.getName().equalsIgnoreCase(view.getContext().getString(R.string.copy_link)))
        {
            try {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", streamingLink);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, context.getString(R.string.link_copy_in_clipboard), Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                Log.d(Constants.tag,"Exception : "+e);
            }
        }
        else
        if (item.getName().equalsIgnoreCase(view.getContext().getString(R.string.email)))
        {
            try {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, streamingLink);
                sendIntent.setPackage("com.google.android.gm");
                startActivity(sendIntent);
            } catch(Exception e) {
                Log.d(Constants.tag,"Exception : "+e);
            }
        }
        else
        if (item.getName().equalsIgnoreCase(view.getContext().getString(R.string.other)))
        {
            try {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, streamingLink);
                startActivity(sendIntent);
            } catch(Exception e) {
                Log.d(Constants.tag,"Exception : "+e);
            }
        }

    }

    private ArrayList<ShareAppModel> getAppShareDataList() {
        ArrayList<ShareAppModel> dataList=new ArrayList<>();
        {
            if (Functions.appInstalledOrNot(view.getContext(),"com.whatsapp"))
            {
                ShareAppModel item=new ShareAppModel();
                item.setName(getString(R.string.whatsapp));
                item.setIcon(R.drawable.ic_share_whatsapp);
                dataList.add(item);
            }
        }
        {
            if (Functions.appInstalledOrNot(view.getContext(),"com.facebook.katana"))
            {
                ShareAppModel item=new ShareAppModel();
                item.setName(getString(R.string.facebook));
                item.setIcon(R.drawable.ic_share_facebook);
                dataList.add(item);
            }
        }
        {
            if (Functions.appInstalledOrNot(view.getContext(),"com.facebook.orca"))
            {
                ShareAppModel item=new ShareAppModel();
                item.setName(getString(R.string.messenger));
                item.setIcon(R.drawable.ic_share_messenger);
                dataList.add(item);
            }
        }
        {
            ShareAppModel item=new ShareAppModel();
            item.setName(getString(R.string.sms));
            item.setIcon(R.drawable.ic_share_sms);
            dataList.add(item);
        }
        {
            ShareAppModel item=new ShareAppModel();
            item.setName(getString(R.string.copy_link));
            item.setIcon(R.drawable.ic_share_copy_link);
            dataList.add(item);
        }
        {
            if (Functions.appInstalledOrNot(view.getContext(),"com.whatsapp"))
            {
                ShareAppModel item=new ShareAppModel();
                item.setName(getString(R.string.email));
                item.setIcon(R.drawable.ic_share_email);
                dataList.add(item);
            }
        }
        {
            ShareAppModel item=new ShareAppModel();
            item.setName(getString(R.string.other));
            item.setIcon(R.drawable.ic_share_other);
            dataList.add(item);
        }
        return dataList;
    }



    private void callApi() {
        JSONObject parameters = new JSONObject();
        try {
            if (etSearch.getText().toString().length()>0)
            {
                parameters.put("type", "user");
                parameters.put("keyword", etSearch.getText().toString());
                parameters.put("starting_point", "" + pageCount);
            }
            else
            {
                parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
                parameters.put("starting_point", "" + pageCount);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }

        String url="";
        if (etSearch.getText().toString().length()>0)
        {
            url=ApiLinks.search;
        }
        else
        {
            url=ApiLinks.showFollowers;
        }


        VolleyRequest.JsonPostRequest(getActivity(), url, parameters,Functions.getHeaders(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                progressBar.setVisibility(View.GONE);
                Functions.checkStatus(getActivity(),resp);
                parseFansData(resp);
            }
        });
    }

    private void setupRecyclerAdapter() {
        userRecylerview = view.findViewById(R.id.usersrecylerview);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        userRecylerview.setLayoutManager(linearLayoutManager);
        userRecylerview.setHasFixedSize(true);
        contactsAdapter = new Contacts_Adapter(context, userlist, new Contacts_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(final ContactsDataModel itemUpdate, int positon) {
                ContactsDataModel item=userlist.get(positon);
                if (streamType.equals("single"))
                {
                    if (item.getIsexits())
                    {
                        Toast.makeText(context, view.getContext().getString(R.string.already_a_member), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        add_member_Dialog(item, positon);
                    }

                }
                else
                if (streamType.equals("multiple"))
                {
                    if (inviteUserMapList.containsKey(item.getUserId()))
                    {
                        itemUpdate.setIsexits(false);
                        userlist.set(positon,itemUpdate);
                        contactsAdapter.notifyItemChanged(positon);
                        inviteUserMapList.remove(item.getUserId());
                    }
                    else
                    {
                        itemUpdate.setIsexits(true);
                        userlist.set(positon,itemUpdate);
                        contactsAdapter.notifyItemChanged(positon);
                        inviteUserMapList.put(item.getUserId(),item);
                    }
                    updateButtonStatus();
                }
            }
        });
        userRecylerview.setAdapter(contactsAdapter);


        userRecylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                Functions.printLog("resp", "" + scrollOutitems);
                if (userScrolled && (scrollOutitems == userlist.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE ) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApi();
                    }
                }


            }
        });
    }

    private void setupSearchEditText() {
        etSearch = view.findViewById(R.id.search_edit);
        etSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        if (etSearch.getText().toString().length()>0)
                        {
                            tvSuggested.setText(context.getString(R.string.search_));
                            search_btn.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            tvSuggested.setText(context.getString(R.string.suggested));
                            search_btn.setVisibility(View.GONE);
                            setlistwithFollower();
                        }
                    }
                }
        );
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etSearch.getText().toString().length()>0)
                {
                    Functions.hideSoftKeyboard(getActivity());
                    pageCount=0;
                    callApi();
                }

            }
        });
    }

    private void setlistwithFollower() {
        userlist.clear();
        for (String key:followerlist.keySet())
        {
            ContactsDataModel userItem=followerlist.get(key);
            userlist.add(userItem);
        }
        contactsAdapter.notifyDataSetChanged();


        if (userlist.isEmpty()) {
            view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.no_data_layout).setVisibility(View.GONE);
        }
    }

    private void updateButtonStatus() {
        if (inviteUserMapList.keySet().size()>0)
        {
            ivInviteAll.setVisibility(View.VISIBLE);
        }
        else
        {
            ivInviteAll.setVisibility(View.GONE);
        }
    }


    // parse the list of all the follower list
    public void parseFansData(String responce) {
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equalsIgnoreCase("200")) {

                JSONArray msg = jsonObject.optJSONArray("msg");
                ArrayList<ContactsDataModel> temp_list = new ArrayList<>();
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject data = msg.optJSONObject(i);

                    String userKey="";
                    if (etSearch.getText().toString().length()>0)
                    {
                        userKey="User";
                    }
                    else
                    {
                        userKey="FollowerList";
                    }

                    UserModel userDetailModel= DataParsing.getUserDataModel(data.optJSONObject(userKey));

                    ContactsDataModel user = new ContactsDataModel();
                    user.setUsername(userDetailModel.getUsername());
                    user.setPicture(userDetailModel.getProfilePic());
                    user.setUserId(userDetailModel.getId());
                    user.setEmail(userDetailModel.getEmail());
                    user.setFirstName(userDetailModel.getFirstName());
                    user.setLastName(userDetailModel.getLastName());
                    user.setUid("");
                    user.setVerified(userDetailModel.getVerified());
                    user.setImagecolor(androidColors[new Random().nextInt(androidColors.length)]);
                    user.setIsexits(false);

                    if (etSearch.getText().toString().length()<1)
                    {
                        followerlist.put(user.userId,user);
                    }

                    temp_list.add(user);


                }

                if (pageCount == 0) {
                    userlist.clear();
                    userlist.addAll(temp_list);

                    if (userlist.isEmpty()) {
                        view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.no_data_layout).setVisibility(View.GONE);
                    }
                } else {

                    if (temp_list.isEmpty())
                    {}
                    else {
                        userlist.addAll(temp_list);
                    }

                }

            } else {
                if (userlist.isEmpty())
                    view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        } finally {
            contactsAdapter.notifyDataSetChanged();
            loadMoreProgress.setVisibility(View.GONE);
        }

    }

    private boolean isUserExist(ContactsDataModel user) {
        boolean isExist=false;
        for (Group_member_GetSet member:allMembersArrylist)
        {
            if (user.getUserId().equalsIgnoreCase(member.getUser_id()))
            {
                isExist=true;
            }
        }
        return isExist;
    }


    // below three method is used for delete the message fro the groupchar\t
    private void add_member_Dialog(final ContactsDataModel contactsData, final int positon) {

        final CharSequence[] options = { context.getString(R.string.add)+" "+contactsData.getUsername()+" "+context.getString(R.string.in_this_concert),context.getString(R.string.cancel_) };

        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(context.getString(R.string.add)+" "+contactsData.getUsername()+" "+context.getString(R.string.in_this_concert)))
                {
                    rootref.child("LiveStreamingUsers")
                            .child(streamingId)
                            .child("StreamInvite").child(Functions.getSharedPreference(context).getString(Variables.U_ID,""))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(snapshot.exists())
                                            {
                                                StreamInviteModel itemUpdate=snapshot.getValue(StreamInviteModel.class);
                                                if (snapshot.getChildrenCount()>0)
                                                {

                                                }
                                                else
                                                {
                                                    sendStreamingSingleInvite(contactsData,positon);
                                                }


                                            }
                                            else
                                            {
                                                sendStreamingSingleInvite(contactsData,positon);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
                else
                if (options[item].equals(context.getString(R.string.cancel_))) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    private void sendStreamingSingleInvite(ContactsDataModel contactsData, final int positon) {
        JSONObject parameters = new JSONObject();
        try {
            JSONArray userArray=new JSONArray();
            JSONObject userObj=new JSONObject();
            userObj.put("user_id", contactsData.getUserId());
            userArray.put(userObj);
            parameters.put("users", userArray);
            parameters.put("live_streaming_id", streamingId);
            parameters.put("type", streamType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.inviteUserToStreaming,parameters, Functions.getHeaders(context),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        Add_member(positon);
                    }
                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });

    }


    private void sendStreamingMultipleInvite() {
        JSONObject parameters = new JSONObject();
        try {
            JSONArray userArray=new JSONArray();
            for (String key:inviteUserMapList.keySet())
            {
                ContactsDataModel itemModel=inviteUserMapList.get(key);
                JSONObject userObj=new JSONObject();
                userObj.put("user_id", itemModel.getUserId());
                userArray.put(userObj);
            }
            parameters.put("users", userArray);
            parameters.put("live_streaming_id", streamingId);
            parameters.put("type", streamType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(getActivity(),false,false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.inviteUserToStreaming,parameters, Functions.getHeaders(context),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        Toast.makeText(context, context.getString(R.string.invitation_send_successfully), Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception : "+e);
                }
            }
        });

    }

    private void addMultipleUsersIntoList() {

    }


    public void Add_member(final int position){
        ContactsDataModel itemUpdate=userlist.get(position);
        itemUpdate.setIsexits(true);
        userlist.set(position,itemUpdate);
        contactsAdapter.notifyItemChanged(position);
        Toast.makeText(context, context.getString(R.string.invitation_sended), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (refreshCallback!=null)
        {
            Bundle bundle=new Bundle();
            bundle.putBoolean("isShow",false);
            refreshCallback.onResponce(bundle);
        }
    }
}
