package com.qboxus.tictic.activitesfragments.profile.usersstory;

import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.HomeF;
import com.qboxus.tictic.adapters.StoryEmojiAdapter;
import com.qboxus.tictic.databinding.FragmentStoryEmoticonBinding;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.simpleclasses.Functions;
import java.util.ArrayList;


public class StoryEmoticonF extends BottomSheetDialogFragment {


    FragmentCallBack callBack;
    FragmentStoryEmoticonBinding binding;
    StoryEmojiAdapter adapter;
    ArrayList<String> dataList=new ArrayList<>();
    String selectedEmoticon;

    public StoryEmoticonF(FragmentCallBack callBack) {
        this.callBack=callBack;
    }

    public static StoryEmoticonF newInstance(FragmentCallBack callBack) {
        StoryEmoticonF fragment = new StoryEmoticonF(callBack);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StoryEmoticonF() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_story_emoticon, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {

    }

    private void initControl() {

        setupAdapter();
    }

    private void setupAdapter() {
        GridLayoutManager layoutManager=new GridLayoutManager(binding.getRoot().getContext(),5);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recylerview.setLayoutManager(layoutManager);
        adapter=new StoryEmojiAdapter(dataList, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                selectedEmoticon=dataList.get(pos);
                dismiss();
            }
        });
        binding.recylerview.setAdapter(adapter);

        getEmojiList();
    }


    private void getEmojiList() {
        if (!(dataList.size()>0))
        {
            String[] emojiArray=binding.getRoot().getContext().getResources().getStringArray(R.array.photo_editor_emoji);
            for (String emoji:emojiArray) {
                dataList.add(Functions.convertEmoji(emoji));
            }
            adapter.notifyDataSetChanged();
        }

        if (dataList.size()>0)
        {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (selectedEmoticon!=null)
        {
            Bundle bundle=new Bundle();
            bundle.putBoolean("isShow",true);
            bundle.putString("data",selectedEmoticon);
            callBack.onResponce(bundle);
        }
        else
        {
            Bundle bundle=new Bundle();
            bundle.putBoolean("isShow",false);
            callBack.onResponce(bundle);
        }
    }
}