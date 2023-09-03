package com.qboxus.tictic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.OptionSelectionModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.OptionSelectionItemViewBinding;

import java.util.ArrayList;

public class OptionSelectionAdapter extends RecyclerView.Adapter<OptionSelectionAdapter.ViewHolder> {

    private ArrayList<OptionSelectionModel> list;
    private AdapterClickListener listener;

    public OptionSelectionAdapter(ArrayList<OptionSelectionModel> list, AdapterClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OptionSelectionItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.option_selection_item_view, parent,false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OptionSelectionModel model = list.get(position);

        holder.binding.tvTitle.setText(""+model.getTitle());

        if (list!=null && list.size()>0 && (list.size()-1)==position)
        {
            holder.binding.viewTitle.setVisibility(View.GONE);
        }
        else
        {
            holder.binding.viewTitle.setVisibility(View.VISIBLE);
        }

        holder.bind(position, model, listener);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder {

        OptionSelectionItemViewBinding binding;

        ViewHolder(OptionSelectionItemViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }


        public void bind(final int pos, final Object model, final AdapterClickListener listener) {
            binding.tabTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,model);
                }
            });
        }
    }

}