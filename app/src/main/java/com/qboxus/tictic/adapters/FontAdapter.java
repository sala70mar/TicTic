package com.qboxus.tictic.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.interfaces.AdapterClickListener;
import com.qboxus.tictic.models.FontModel;
import com.qboxus.tictic.R;

import java.util.ArrayList;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.CustomViewHolder> {

    public Context context;
    private ArrayList<FontModel> dataList;
    AdapterClickListener adapterClickListener;

    public FontAdapter(Context context, ArrayList<FontModel> dataList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.adapterClickListener = adapterClickListener;
    }

    public FontModel selectedFont=null;
    public void setSelectedFont(FontModel selectedFont){
        this.selectedFont=selectedFont;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_font_list, viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        LinearLayout main_div;

        public CustomViewHolder(View view) {
            super(view);
            text=view.findViewById(R.id.text);
            main_div=view.findViewById(R.id.main_div);
        }

        public void bind(final int position, final FontModel item, final AdapterClickListener listener) {
            itemView.setOnClickListener(v -> {
                adapterClickListener.onItemClick(v, position, item);

            });

        }

    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        final FontModel item = dataList.get(i);
        if(item.name.equals(selectedFont.name)) {
            holder.main_div.setBackground(ContextCompat.getDrawable(context,R.drawable.d_rounded_white_border_2));
        }else {
            holder.main_div.setBackground(ContextCompat.getDrawable(context,R.drawable.d_rounded_white_border_1));
        }
        holder.text.setText(item.name);
        Typeface typeface = ResourcesCompat.getFont(context, item.font);
        holder.text.setTypeface(typeface);

        holder.bind(i, item, adapterClickListener);

    }

}
