package com.qboxus.tictic.activitesfragments.argear;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.activitesfragments.argear.adapter.StickerCategoryListAdapter;
import com.qboxus.tictic.activitesfragments.argear.adapter.StickerListAdapter;
import com.qboxus.tictic.activitesfragments.argear.api.ContentsResponse;
import com.qboxus.tictic.activitesfragments.argear.model.CategoryModel;
import com.qboxus.tictic.activitesfragments.argear.model.ItemModel;
import com.qboxus.tictic.activitesfragments.argear.viewmodel.ContentsViewModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderA;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderDuetA;

public class StickerFragment extends BottomSheetDialogFragment implements View.OnClickListener, StickerCategoryListAdapter.Listener, StickerListAdapter.Listener {

    private StickerCategoryListAdapter mStickerCategoryListAdapter;
    private StickerListAdapter mStickerListAdapter;
    private ContentsViewModel mContentsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sticker, container, false);

        rootView.findViewById(R.id.close_sticker_button).setOnClickListener(this);
        rootView.findViewById(R.id.clear_sticker_button).setOnClickListener(this);

        // init category_sticker list
        RecyclerView recyclerViewStickerCategory = rootView.findViewById(R.id.sticker_category_recyclerview);

        recyclerViewStickerCategory.setHasFixedSize(true);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext());
        categoryLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewStickerCategory.setLayoutManager(categoryLayoutManager);

        mStickerCategoryListAdapter = new StickerCategoryListAdapter(getContext(),this);
        recyclerViewStickerCategory.setAdapter(mStickerCategoryListAdapter);

        // init item_sticker list
        RecyclerView recyclerViewSticker = rootView.findViewById(R.id.sticker_recyclerview);

        recyclerViewSticker.setHasFixedSize(true);
        LinearLayoutManager itemsLayoutManager = new LinearLayoutManager(getContext());
        itemsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewSticker.setLayoutManager(itemsLayoutManager);

        mStickerListAdapter = new StickerListAdapter(this);
        recyclerViewSticker.setAdapter(mStickerListAdapter);



        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            Log.d(Constants.tag,"onActivityCreated: ");
            mContentsViewModel = new ViewModelProvider(getActivity()).get(ContentsViewModel.class);
            mContentsViewModel.getContents().observe(getViewLifecycleOwner(), new Observer<ContentsResponse>() {
                @Override
                public void onChanged(ContentsResponse contentsResponse) {

                    if (contentsResponse != null && contentsResponse.categories != null) {

                       for(int i=0;i<contentsResponse.categories.size();i++){

                           if(i==0)
                           contentsResponse.categories.get(0).isSelect=true;
                           else
                               contentsResponse.categories.get(i).isSelect=false;

                       }
                       mStickerCategoryListAdapter.setData(contentsResponse.categories);
                       mStickerListAdapter.setData(contentsResponse.categories.get(0).items);
                    }

                }
            });
        }


    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.close_sticker_button:
                dismiss();
                break;


            case R.id.clear_sticker_button: {
                if (getActivity() instanceof VideoRecoderA)
                {
                    ((VideoRecoderA)getActivity()).clearStickers();
                }
                else
                {
                    ((VideoRecoderDuetA)getActivity()).clearStickers();
                }
                dismiss();
                break;
            }


        }
    }

    @Override
    public void onCategorySelected(CategoryModel category) {
        mStickerListAdapter.setData(category.items);
    }

    @Override
    public void onStickerSelected(int position, ItemModel item) {
        if (getActivity() instanceof VideoRecoderA)
        {
            ((VideoRecoderA)getActivity()).setSticker(item);
        }
        else
        {
            ((VideoRecoderDuetA)getActivity()).setSticker(item);
        }

    }


}
