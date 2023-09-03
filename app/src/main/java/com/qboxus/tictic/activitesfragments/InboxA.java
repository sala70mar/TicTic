package com.qboxus.tictic.activitesfragments;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.adcolony.sdk.AdColonyZone;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.tictic.activitesfragments.chat.ChatA;
import com.qboxus.tictic.adapters.InboxAdapter;
import com.qboxus.tictic.models.InboxModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import java.util.ArrayList;
import java.util.Collections;

public class InboxA extends AppCompatLocaleActivity {

    Context context;
    RecyclerView inboxList;
    ArrayList<InboxModel> inboxArraylist;
    DatabaseReference rootRef;
    InboxAdapter inboxAdapter;
    ProgressBar pbar;
    boolean isviewCreated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(InboxA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_inbox);
        context = InboxA.this;

        rootRef = FirebaseDatabase.getInstance().getReference();


        pbar = findViewById(R.id.pbar);
        inboxList = findViewById(R.id.inboxlist);

        // intialize the arraylist and and inboxlist
        inboxArraylist = new ArrayList<>();

        inboxList = (RecyclerView) findViewById(R.id.inboxlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inboxList.setLayoutManager(layout);
        inboxList.setHasFixedSize(false);
        inboxAdapter = new InboxAdapter(context, inboxArraylist, new InboxAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(InboxModel item) {
                chatFragment(item.getId(), item.getName(), item.getPic());
            }
        }, new InboxAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(InboxModel item) {

            }
        });

        inboxList.setAdapter(inboxAdapter);



        findViewById(R.id.back_btn).setOnClickListener(v -> {
            InboxA.super.onBackPressed();


        });
        isviewCreated = true;
        getData();

        if (!Functions.getSettingsPreference(context).getString(Variables.AddType,"none").equalsIgnoreCase("none")){

            if( Functions.getSettingsPreference(context).getString(Variables.AddType,"").equalsIgnoreCase("admob")){
                initBannerGoogleAd();
            }
            else if( Functions.getSettingsPreference(context).getString(Variables.AddType,"").equalsIgnoreCase("adcolony")){
                initBannerAd();
            }

        }

    }


    LinearLayout adView;
    private AdColonyAdView bannerAdColony;
    private void initBannerAd() {
        adView = findViewById(R.id.banneradColony);
        adView.setVisibility(View.VISIBLE);
        AdColonyAdViewListener bannerListener = new AdColonyAdViewListener() {

            // Code to be executed when an ad request is filled
            // or when an ad finishes loading.
            @Override
            public void onRequestFilled(AdColonyAdView adColonyAdView) {

                //Remove previous ad view if present.
                if (adView.getChildCount() > 0) {
                    adView.removeView(bannerAdColony);
                }
                adView.addView(adColonyAdView);
                bannerAdColony = adColonyAdView;
            }

            // Code to be executed when an ad request is not filled
            //or when an ad is not loaded.
            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                super.onRequestNotFilled(zone);
            }

            //Code to be executed when an ad opens
            @Override
            public void onOpened(AdColonyAdView ad) {
                super.onOpened(ad);
            }

            //Code to be executed when user closed an ad
            @Override
            public void onClosed(AdColonyAdView ad) {
                super.onClosed(ad);
            }

            // Code to be executed when the user clicks on an ad.
            @Override
            public void onClicked(AdColonyAdView ad) {
                super.onClicked(ad);
            }

            // called after onAdOpened(), when a user click opens another app
            // (such as the Google Play), backgrounding the current app
            @Override
            public void onLeftApplication(AdColonyAdView ad) {
                super.onLeftApplication(ad);
            }
        };

        // Optional Ad specific options to be sent with request
        AdColonyAdOptions adOptions = new AdColonyAdOptions();

        //Request Ad
        AdColony.requestAdView(Constants.AD_COLONY_BANNER_ID, bannerListener, AdColonyAdSize.BANNER, adOptions);
    }


    AdView adViewGoogle;
    public void initBannerGoogleAd() {
        adViewGoogle = findViewById(R.id.banneradGoogle);
        adViewGoogle.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewGoogle.loadAd(adRequest);
    }


    // on start we will get the Inbox Message of user  which is show in bottom list of third tab
    ValueEventListener eventListener2;
    Query inboxQuery;

    public void getData() {

        pbar.setVisibility(View.VISIBLE);

        inboxQuery = rootRef.child("Inbox").child(Functions.getSharedPreference(InboxA.this).getString(Variables.U_ID, "0")).orderByChild("date");
        eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inboxArraylist.clear();
                pbar.setVisibility(View.GONE);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    InboxModel model = ds.getValue(InboxModel.class);
                    model.setId(ds.getKey());

                    inboxArraylist.add(model);
                }


                if (inboxArraylist.isEmpty()) {
                    Functions.showToast(context, getString(R.string.no_data));
                    findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                } else {
                    Functions.showToast(context,  getString(R.string.no_data));
                    findViewById(R.id.no_data_layout).setVisibility(View.GONE);
                    Collections.reverse(inboxArraylist);
                    inboxAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pbar.setVisibility(View.GONE);
               findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            }
        };

        inboxQuery.addValueEventListener(eventListener2);


    }


    // on stop we will remove the listener
    @Override
    public void onStop() {
        super.onStop();
        if (inboxQuery != null)
            inboxQuery.removeEventListener(eventListener2);
    }


    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String receiverid, String name, String picture) {
        Intent intent=new Intent(InboxA.this,ChatA.class);
        intent.putExtra("user_id", receiverid);
        intent.putExtra("user_name", name);
        intent.putExtra("user_pic", picture);
        resultCallback.launch(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {

                        }
                    }
                }
            });



}
