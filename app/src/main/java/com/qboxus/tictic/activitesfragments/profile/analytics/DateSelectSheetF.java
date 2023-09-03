package com.qboxus.tictic.activitesfragments.profile.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.FragmentSelectDateSheetBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;

import java.util.Calendar;


public class DateSelectSheetF extends BottomSheetDialogFragment {


    FragmentCallBack callback;
    FragmentSelectDateSheetBinding binding;

    Calendar selectedCalender,startCalender,endCalender;

    public DateSelectSheetF() {
    }

    public DateSelectSheetF( FragmentCallBack callback) {
        this.callback = callback;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_select_date_sheet, container, false);

        selectedCalender=Calendar.getInstance();
        startCalender= Calendar.getInstance();
        endCalender=Calendar.getInstance();

        Bundle bundle=getArguments();
        if(bundle!=null) {
            startCalender.setTimeInMillis(bundle.getLong("startDate"));
            selectedCalender.setTimeInMillis(bundle.getLong("startDate"));
            endCalender.setTimeInMillis(bundle.getLong("endDate"));
        }

        Long days=DateOperations.INSTANCE.getDays(startCalender.getTime(),endCalender.getTime());
        if(days<=7){
            binding.weekTxt.setChecked(true);
        }
        else if(days<=31){
            binding.monthTxt.setChecked(true);
        }
        else if(days<=61){
            binding.twomonthTxt.setChecked(true);
        }

        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void initControl() {

    }


    private void actionControl() {
        binding.tabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.customBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putBoolean("isCustom",true);
                callback.onResponce(bundle);
                dismiss();
            }
        });

        binding.weekTxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    selectedCalender=Calendar.getInstance();
                    selectedCalender.set(Calendar.DAY_OF_YEAR,selectedCalender.get(Calendar.DAY_OF_YEAR)-7);
                    responceBack();
                }
            }
        });

        binding.monthTxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selectedCalender = Calendar.getInstance();
                    selectedCalender.set(Calendar.MONTH, selectedCalender.get(Calendar.MONTH) - 1);
                    responceBack();
                }
            }
        });

        binding.twomonthTxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selectedCalender = Calendar.getInstance();
                    selectedCalender.set(Calendar.MONTH, selectedCalender.get(Calendar.MONTH) - 2);
                    responceBack();
                }
            }
        });


    }

    public void responceBack(){
        Bundle bundle=new Bundle();
        bundle.putLong("startDate",selectedCalender.getTimeInMillis());
        bundle.putLong("endDate",endCalender.getTimeInMillis());
        callback.onResponce(bundle);
        dismiss();
    }


}