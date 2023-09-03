package com.qboxus.tictic.activitesfragments.storyeditors;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListMediaResponse;
import com.qboxus.tictic.activitesfragments.chat.GifAdapter;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.databinding.FragmentStoryGiphyBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.R;

import java.util.ArrayList;


public class StoryGiphyF extends Fragment {


    FragmentStoryGiphyBinding binding;
    GifAdapter adapter;
    final ArrayList<String> dataList = new ArrayList<>();
    GPHApi client;
    String searchKey;
    FragmentCallBack callBack;

    public StoryGiphyF(FragmentCallBack callBack) {
        this.callBack=callBack;
    }

    public StoryGiphyF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_story_giphy, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (binding.etSearch.getText().toString().length() > 0) {
                    binding.tvSearch.setVisibility(View.VISIBLE);

                } else {
                    binding.tvSearch.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGiphyList();
            }
        });

        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragment().getChildFragmentManager().popBackStackImmediate();
            }
        });

        getGiphyList();
    }

    private void getGiphyList() {
        if (!(binding.etSearch.getText().toString().isEmpty()))
        {
            searchGif(""+binding.etSearch.getText().toString());
        }
        else
        {
            getTrendingGif();
        }
    }


    private void initControl() {
        searchKey=getArguments().getString("searchKey");
        client = new GPHApiClient(binding.getRoot().getContext().getString(R.string.gif_api_key));
        binding.etSearch.setText(""+searchKey);
        getGipy();
    }

    public void getGipy() {
        dataList.clear();
        GridLayoutManager layoutManager=new GridLayoutManager(binding.getRoot().getContext(),4);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(layoutManager);
        adapter = new GifAdapter(binding.getRoot().getContext(), dataList, new GifAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {

                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                bundle.putString("type","gif");
                bundle.putString("data",item);
                callBack.onResponce(bundle);
            }
        });
        binding.recylerview.setAdapter(adapter);


    }

    private void getTrendingGif() {
        client.trending(MediaType.gif, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                binding.progressBar.setVisibility(View.GONE);
                if (result == null) {
                    Log.d(Constants.tag,"Result: null");
                } else {
                    if (result.getData() != null) {
                        for (Media gif : result.getData()) {
                            dataList.add(gif.getId());
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.d(Constants.tag,"Result: No results found");
                    }
                }
            }
        });
    }


    // if we want to search the gif then this mehtod is immportaant
    public void searchGif(String search) {
        client.search(search, MediaType.gif, null, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                binding.progressBar.setVisibility(View.GONE);
                if (result == null) {
                    Log.d(Constants.tag,"Result: null");
                } else {
                    if (result.getData() != null) {
                        dataList.clear();
                        for (Media gif : result.getData()) {
                            dataList.add(gif.getId());
                            adapter.notifyDataSetChanged();
                        }
                        binding.recylerview.smoothScrollToPosition(0);

                    } else {
                        Log.d(Constants.tag,"Result: No results found");
                    }
                }
            }
        });
    }

}