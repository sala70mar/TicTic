package com.qboxus.tictic.activitesfragments.argear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.R;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderA;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderDuetA;


public class BulgeFragment extends BottomSheetDialogFragment implements View.OnClickListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bulge, container, false);

        rootView.findViewById(R.id.close_bulge_button).setOnClickListener(this);
        rootView.findViewById(R.id.clear_bulge_button).setOnClickListener(this);
        rootView.findViewById(R.id.bulge_fun1_button).setOnClickListener(this);
        rootView.findViewById(R.id.bulge_fun2_button).setOnClickListener(this);
        rootView.findViewById(R.id.bulge_fun3_button).setOnClickListener(this);
        rootView.findViewById(R.id.bulge_fun4_button).setOnClickListener(this);
        rootView.findViewById(R.id.bulge_fun5_button).setOnClickListener(this);
        rootView.findViewById(R.id.bulge_fun6_button).setOnClickListener(this);

        return rootView;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_bulge_button:
                dismiss();
                break;
            case R.id.clear_bulge_button:
            {
                if (getActivity() instanceof VideoRecoderA)
                {
                    ((VideoRecoderA)getActivity()).clearBulge();
                }
                else
                {
                    ((VideoRecoderDuetA)getActivity()).clearBulge();
                }
                dismiss();
            }
                break;
            case R.id.bulge_fun1_button :
                applyFunFilter(1);
                break;
            case R.id.bulge_fun2_button :
                applyFunFilter(2);
                break;
            case R.id.bulge_fun3_button :
                applyFunFilter(3);
                break;
            case R.id.bulge_fun4_button :
                applyFunFilter(4);
                break;
            case R.id.bulge_fun5_button :
                applyFunFilter(5);
                break;
            case R.id.bulge_fun6_button :
                applyFunFilter(6);
                break;

        }
    }

    private void applyFunFilter(int type) {
        if (getActivity() instanceof VideoRecoderA)
        {
            ((VideoRecoderA)getActivity()).setBulgeFunType(type);
        }
        else
        {
            ((VideoRecoderDuetA)getActivity()).setBulgeFunType(type);
        }
        dismiss();
    }
}
