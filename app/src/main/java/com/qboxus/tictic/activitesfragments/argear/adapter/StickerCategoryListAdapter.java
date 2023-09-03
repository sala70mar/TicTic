package com.qboxus.tictic.activitesfragments.argear.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.tictic.activitesfragments.argear.model.CategoryModel;
import com.qboxus.tictic.R;

import java.util.ArrayList;
import java.util.List;


public class StickerCategoryListAdapter extends RecyclerView.Adapter<StickerCategoryListAdapter.ViewHolder> {


	public List<CategoryModel> mCategories = new ArrayList<>();

	public interface Listener {
		void onCategorySelected(CategoryModel category);
	}

	private Listener mListener;

	Context context;
	public StickerCategoryListAdapter(Context context, Listener listener) {
		this.context=context;
		mListener = listener;
	}

	public void setData(List<CategoryModel> categories){
		mCategories.clear();
		for (CategoryModel model : categories) {
			if (!TextUtils.equals(model.title, "filters")) {
				mCategories.add(model);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mCategories.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.bind(position);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_sticker, parent, false);
		return new CategoryViewHolder(v);
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

	public class CategoryViewHolder extends ViewHolder implements View.OnClickListener {
		Button mButtonCategory = null;
		CategoryModel mCategory;

		CategoryViewHolder(View v) {
			super(v);
			mButtonCategory = (Button) v.findViewById(R.id.category_button);
		}

		@Override
		void bind(int position) {
			mCategory = mCategories.get(position);
			if(mCategory.isSelect){
				mButtonCategory.setTextColor(ContextCompat.getColor(context,R.color.appColor));
			}
			else {
				mButtonCategory.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));
			}
			mButtonCategory.setText(mCategory.title);
			mButtonCategory.setOnClickListener(this);
		}


		@Override
		public void onClick(View v) {
			if(mListener != null){

				for(int i=0;i<mCategories.size();i++){
					if(mCategories.get(i).uuid.equals(mCategory.uuid)){
						mCategory.isSelect=true;
						mCategories.remove(i);
						mCategories.add(i,mCategory);
					}
					else {
						CategoryModel categoryModel=mCategories.get(i);
						categoryModel.isSelect=false;
						mCategories.remove(i);
						mCategories.add(i,categoryModel);
					}
				}

				notifyDataSetChanged();

				mListener.onCategorySelected(mCategory);
			}
		}

	}


}