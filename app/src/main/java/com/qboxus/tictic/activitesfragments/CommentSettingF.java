package com.qboxus.tictic.activitesfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.databinding.FragmentCommentSettingBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.CommentModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;


public class CommentSettingF extends BottomSheetDialogFragment implements View.OnClickListener{


    FragmentCommentSettingBinding binding;
    CommentModel item;
    FragmentCallBack callBack;

    public CommentSettingF(CommentModel item, FragmentCallBack callBack) {
        this.item=item;
        this.callBack=callBack;
    }

    public CommentSettingF() {
        //Required Empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_comment_setting, container, false);
        InitControl();
        return binding.getRoot();
    }

    private void InitControl() {
        Functions.hideSoftKeyboard(getActivity());
        binding.tvPinComment.setOnClickListener(this);
        binding.tvCopy.setOnClickListener(this);
        binding.tvDelete.setOnClickListener(this);

        if (item.videoOwnerId.equals(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,"")))
        {

            if (item.comment_id.equals(item.pin_comment_id))
            {
                binding.tvPinComment.setText(binding.getRoot().getContext().getString(R.string.unpin_comment));
            }
            else
            {
                binding.tvPinComment.setText(binding.getRoot().getContext().getString(R.string.pin_comment));
            }
            binding.tvPinComment.setVisibility(View.VISIBLE);
            binding.tvDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.tvPinComment.setVisibility(View.GONE);
            if (item.userId.equals(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID,"")))
            {
                binding.tvDelete.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.tvDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tvPinComment:
            {
                performAction("pinComment");
            }
            break;
            case R.id.tvCopy:
            {
                performAction("copyText");
            }
            break;
            case R.id.tvDelete:
            {
                performAction("deleteComment");
            }
            break;
        }
    }

    private void performAction(String action) {
        Bundle bundle=new Bundle();
        bundle.putBoolean("isShow",true);
        bundle.putString("action",action);
        callBack.onResponce(bundle);
        dismiss();
    }
}