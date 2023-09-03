package com.qboxus.tictic.activitesfragments.livestreaming.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.livestreaming.model.ContactsDataModel;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;

public class Contacts_Adapter extends RecyclerView.Adapter<Contacts_Adapter.CustomViewHolder >{
    public Context context;
    ArrayList<ContactsDataModel> user_dataList = new ArrayList<>();
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(ContactsDataModel item, int position);
    }

    public Contacts_Adapter(Context context, ArrayList<ContactsDataModel> user_dataList, OnItemClickListener listener) {
        this.context = context;
        this.user_dataList=user_dataList;
        this.listener = listener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contacts,viewGroup,false);
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return user_dataList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView user_name,name_single_letter,email;
        ImageView contactimage,ivVarified;
        SimpleDraweeView user_image;

        public CustomViewHolder(View view) {
            super(view);
            this.user_name=view.findViewById(R.id.username);
            this.user_image=view.findViewById(R.id.userimage);
            contactimage=view.findViewById(R.id.contactimage);
            ivVarified=view.findViewById(R.id.ivVarified);
            this.email=view.findViewById(R.id.email);
            this.name_single_letter=view.findViewById(R.id.name_single_letter);
        }

        public void bind(final ContactsDataModel item, final int postion , final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,postion);
                }
            });
        }
    }

    public void filter(ArrayList<ContactsDataModel> filter_list) {
        this.user_dataList=filter_list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        final ContactsDataModel item=user_dataList.get(i);
        holder.bind(item,i,listener);
        if(item.getIsexits()){
            holder.contactimage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_circle_primary));
        }else {
            holder.contactimage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_un_selected));
        }
        if (item.getPicture().contains(Variables.http) || item.getPicture().contains(".png"))
        {
            holder.user_image.setController(Functions.frescoImageLoad(item.getPicture(),holder.user_image,false));
            holder.name_single_letter.setVisibility(View.GONE);
        }
        else
        {
            holder.user_image.setController(Functions.frescoImageLoad(new ColorDrawable(item.getImagecolor()),holder.user_image,false));
            holder.name_single_letter.setText(item.getUsername().substring(0, 1).toString());
            holder.name_single_letter.setVisibility(View.VISIBLE);
        }
        holder.email.setText(item.getFirstName()+" "+item.getLastName());
        holder.user_name.setText(item.getUsername());

        if (item.getVerified().equals("1"))
        {
            holder.ivVarified.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivVarified.setVisibility(View.GONE);
        }
    }





}