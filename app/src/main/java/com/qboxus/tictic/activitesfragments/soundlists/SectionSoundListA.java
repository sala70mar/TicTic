package com.qboxus.tictic.activitesfragments.soundlists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.qboxus.tictic.adapters.SoundListAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.SoundsModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SectionSoundListA extends AppCompatActivity implements Player.Listener, View.OnClickListener{

    Context context;
    TextView titleTxt;
    String id;
    ArrayList<Object> datalist;
    SoundListAdapter adapter;
    static boolean active = false;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    DownloadRequest prDownloader;
    public static String running_sound_id;
    ProgressBar pbar;
    SwipeRefreshLayout swiperefresh;
    RelativeLayout noDataLayout;
    int pageCount = 0;
    boolean ispostFinsh;
    ProgressBar loadMoreProgress;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // Milliseconds
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_section_sound_list);
        context = SectionSoundListA.this;


        titleTxt = findViewById(R.id.title_txt);
        etSearch=findViewById(R.id.search_edit);
        id = getIntent().getStringExtra("id");
        titleTxt.setText(getIntent().getStringExtra("name"));

        running_sound_id = "none";
        PRDownloader.initialize(context);

        findViewById(R.id.back_btn).setOnClickListener(this);
        pbar = findViewById(R.id.pbar);
        loadMoreProgress = findViewById(R.id.load_more_progress);

        noDataLayout = findViewById(R.id.no_data_layout);

        recyclerView = findViewById(R.id.listview);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);


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

                Functions.printLog("resp", "" + scrollOutitems);
                if (userScrolled && (scrollOutitems == datalist.size() - 1)) {
                    userScrolled = false;

                    if (loadMoreProgress.getVisibility() != View.VISIBLE && !ispostFinsh) {
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        pageCount = pageCount + 1;
                        callApiForSound();
                    }
                }


            }
        });


        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                pageCount = 0;
                callApiForSound();
            }
        });


        etSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        SectionSoundListA.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pageCount=0;
                                                callApiForSound();
                                            }
                                        });
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );
        setAdapter();
        callApiForSound();

    }


    private void callApiForSound() {
        if (etSearch.getText().toString().length() > 0) {
            callApiForAllsoundSearch(etSearch.getText().toString());
        }
        else
        {
            callApi();
        }
    }


    private void callApiForAllsoundSearch(String key) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id",Functions.getSharedPreference(context).getString(Variables.U_ID, "0"));
            parameters.put("sound_section_id",id);
            parameters.put("keyword", key);
            parameters.put("starting_point", "" + pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(SectionSoundListA.this, ApiLinks.searchSoundsAgainstSection, parameters,Functions.getHeaders(SectionSoundListA.this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(SectionSoundListA.this,resp);
                swiperefresh.setRefreshing(false);
                pbar.setVisibility(View.GONE);
                parseData(resp);
            }
        });
    }


    // set the adapter for show list
    public void setAdapter() {
        datalist = new ArrayList<>();

        adapter = new SoundListAdapter(context, datalist, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {

                SoundsModel item = (SoundsModel) object;

                if (view.getId() == R.id.done) {
                    stopPlaying();
                    downLoadMp3(item.id, item.sound_name, item.getAcc_path());
                } else if (view.getId() == R.id.fav_btn) {
                    callApiForFavSound(pos, item);
                } else {
                    if (thread != null && !thread.isAlive()) {
                        stopPlaying();
                        playaudio(view, item);
                    } else if (thread == null) {
                        stopPlaying();
                        playaudio(view, item);
                    }
                }
            }
        });

        recyclerView.setAdapter(adapter);


    }


    public void callApi() {

        JSONObject params = new JSONObject();
        try {
            params.put("starting_point", pageCount);
            params.put("sound_section_id", id);
            params.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(SectionSoundListA.this, ApiLinks.showSoundsAgainstSection, params,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(SectionSoundListA.this,resp);
                pbar.setVisibility(View.GONE);
                loadMoreProgress.setVisibility(View.GONE);
                parseData(resp);
            }
        });

    }

    // parse  the data of sound list
    public void parseData(String responce) {


        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msgArray = jsonObject.getJSONArray("msg");

                ArrayList<SoundsModel> tempList = new ArrayList<>();
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i).optJSONObject("Sound");

                    SoundsModel item = new SoundsModel();

                    item.id = itemdata.optString("id");

                    item.setAcc_path(itemdata.optString("audio"));
                    item.sound_name = itemdata.optString("name");
                    item.description = itemdata.optString("description");
                    item.section = itemdata.optString("section");
                    item.setThum(itemdata.optString("thum"));

                    item.duration = itemdata.optString("duration");
                    item.date_created = itemdata.optString("created");
                    item.fav = itemdata.optString("favourite");


                    tempList.add(item);
                }


                if (pageCount == 0) {
                    datalist.clear();
                    datalist.addAll(tempList);
                } else {
                    datalist.addAll(tempList);
                }

                adapter.notifyDataSetChanged();
            }

            if (datalist.isEmpty()) {
                findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.no_data_layout).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loadMoreProgress.setVisibility(View.GONE);
        }

    }


    // initialize the player for play the audio

    View previousView;
    ExoPlayer player;
    Thread thread;
    String previous_url = "none";

    public void playaudio(View view, final SoundsModel item) {
        previousView = view;

        if (previous_url.equals(item.getAcc_path())) {
            previous_url = "none";
            running_sound_id = "none";
        } else {

            previous_url = item.getAcc_path();
            running_sound_id = item.id;

            player = new ExoPlayer.Builder(context).
                    setTrackSelector(new DefaultTrackSelector(context)).
                    build();

            player.setMediaItem(MediaItem.fromUri(item.getAcc_path()));
            player.prepare();
            player.addListener(this);
            player.setPlayWhenReady(true);

            try {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build();
                player.setAudioAttributes(audioAttributes, true);
            }
            catch (Exception e)
            {
                Log.d(Constants.tag,"Exception audio focus : "+e);
            }
        }

    }


    public void stopPlaying() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }

        showStopState();

    }


    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;

        running_sound_id = "null";

        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }

        showStopState();

    }


    public void showRunState() {

        if (previousView != null) {
            previousView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            previousView.findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);
            View imgDone= previousView.findViewById(R.id.done);
            View imgFav= previousView.findViewById(R.id.fav_btn);
            imgFav.animate().translationX(0).setDuration(400).start();
            imgDone.animate().translationX(0).setDuration(400).start();
        }

    }


    public void showLoadingState() {
        previousView.findViewById(R.id.play_btn).setVisibility(View.GONE);
        previousView.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
    }


    public void showStopState() {

        if (previousView != null) {
            previousView.findViewById(R.id.play_btn).setVisibility(View.VISIBLE);
            previousView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
            previousView.findViewById(R.id.pause_btn).setVisibility(View.GONE);
            View imgDone= previousView.findViewById(R.id.done);
            View imgFav= previousView.findViewById(R.id.fav_btn);
            imgDone.animate().translationX(Float.valueOf(""+context.getResources().getDimension(R.dimen._80sdp))).setDuration(400).start();
            imgFav.animate().translationX(Float.valueOf(""+context.getResources().getDimension(R.dimen._50sdp))).setDuration(400).start();
        }

        running_sound_id = "none";

    }


    public void downLoadMp3(final String id, final String sound_name, String url) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.please_wait_));
        progressDialog.show();

        prDownloader = PRDownloader.download(url, Functions.getAppFolder(SectionSoundListA.this)+Variables.APP_HIDED_FOLDER, Variables.SelectedAudio_AAC)
                .build();

        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                progressDialog.dismiss();
                Intent output = new Intent();
                output.putExtra("isSelected", "yes");
                output.putExtra("sound_name", sound_name);
                output.putExtra("sound_id", id);
                setResult(RESULT_OK, output);
                finish();
                overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
            }

            @Override
            public void onError(Error error) {
                progressDialog.dismiss();
            }
        });

    }


    private void callApiForFavSound(final int pos, final SoundsModel item) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, "0"));
            parameters.put("sound_id", item.id);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(SectionSoundListA.this, false, false);
        VolleyRequest.JsonPostRequest(SectionSoundListA.this, ApiLinks.addSoundFavourite, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(SectionSoundListA.this,resp);
                Functions.cancelLoader();

                if (item.fav.equals("1"))
                    item.fav = "0";
                else
                    item.fav = "1";

                datalist.remove(item);
                datalist.add(pos, item);
                adapter.notifyDataSetChanged();

            }
        });


    }


    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            showLoadingState();
        } else if (playbackState == Player.STATE_READY) {
            showRunState();
        } else if (playbackState == Player.STATE_ENDED) {
            showStopState();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                SectionSoundListA.super.onBackPressed();
                break;
        }
    }


}