package com.qboxus.tictic.activitesfragments.argear.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.tictic.activitesfragments.argear.model.ItemModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;

import java.util.ArrayList;
import java.util.List;


public class StickerListAdapter extends RecyclerView.Adapter<StickerListAdapter.ViewHolder> {

	private List<ItemModel> mItems = new ArrayList<>();

	public interface Listener {
		void onStickerSelected(int position, ItemModel item);
	}

	private Listener mListener;


	public StickerListAdapter( Listener listener) {
		mListener = listener;
	}

	public void setData(List<ItemModel> items) {
		mItems.clear();
		if(items != null) {
			mItems.addAll(items);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.bind(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
		return new StickerViewHolder(v);
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	abstract class ViewHolder extends RecyclerView.ViewHolder {
		abstract void bind(int position);

		ViewHolder(View v) {
			super(v);
		}
	}

	public class StickerViewHolder extends ViewHolder implements View.OnClickListener {
		SimpleDraweeView mImageViewItemThumbnail = null;

		ItemModel mItem;
		int position;

		StickerViewHolder(View v) {
			super(v);
			mImageViewItemThumbnail = v.findViewById(R.id.item_thumbnail_imageview);
		}

		@Override
		void bind(int position) {
			mItem = mItems.get(position);
			this.position = position;

			mImageViewItemThumbnail.setOnClickListener(this);
			mImageViewItemThumbnail.setController(Functions.frescoImageLoad(mItem.thumbnailUrl,R.drawable.image_placeholder,mImageViewItemThumbnail,false));

		}

		@Override
		public void onClick(View v) {
			if(mListener != null){
				mListener.onStickerSelected(position, mItem);
			}
		}


	}

}