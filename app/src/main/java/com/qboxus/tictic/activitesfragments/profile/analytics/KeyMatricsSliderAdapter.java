package com.qboxus.tictic.activitesfragments.profile.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.R;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class KeyMatricsSliderAdapter extends
        SliderViewAdapter<KeyMatricsSliderAdapter.SliderAdapterVH> {

    private List<List<KeyMatricsModel>> list = new ArrayList<>();
    FragmentCallBack callBack;

    public KeyMatricsSliderAdapter(List<List<KeyMatricsModel>> list, FragmentCallBack callBack) {
        this.list = list;

        this.callBack = callBack;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, parent,false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        List<KeyMatricsModel> data_list= list.get(position);

        GridLayoutManager layoutManager=new GridLayoutManager(viewHolder.itemView.getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        viewHolder.recylerview.setLayoutManager(layoutManager);
        viewHolder.adapter=new KeyMatricsAdapter(viewHolder.itemView.getContext(), data_list
                , new KeyMatricsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(KeyMatricsModel item) {

                for (int i=0;i<data_list.size();i++)
                {
                    KeyMatricsModel model=data_list.get(i);
                    if (model.id==item.id)
                    {
                        if (model.isSelected)
                        {
                            model.isSelected=false;
                            data_list.set(i,model);
                        }
                        else
                        {
                            model.isSelected=true;
                            data_list.set(i,model);

                            Bundle bundle=new Bundle();
                            bundle.putBoolean("isShow",true);
                            bundle.putSerializable("Data",model);
                            callBack.onResponce(bundle);
                        }

                    }
                    else
                    {
                        model.isSelected=false;
                        data_list.set(i,model);
                    }
                    viewHolder.adapter.notifyDataSetChanged();
                }

            }
        });

        viewHolder.recylerview.setAdapter(viewHolder.adapter);
    }


    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return list.size();
    }


    public class SliderAdapterVH extends ViewHolder {

        private View itemView;
        private RecyclerView recylerview;
        public KeyMatricsAdapter adapter;



        public SliderAdapterVH(View itemView) {
            super(itemView);
            recylerview = itemView.findViewById(R.id.recylerview);
            this.itemView = itemView;
        }
    }

}
