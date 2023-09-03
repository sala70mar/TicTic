package com.qboxus.tictic.activitesfragments.profile.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.R;
import com.qboxus.tictic.databinding.FCalenderLayoutBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomeCalenderF extends BottomSheetDialogFragment implements OnClickListener {

   FCalenderLayoutBinding binding;

   FragmentCallBack fragmentCallBack;
   public CustomeCalenderF(FragmentCallBack fragmentCallBack){
       this.fragmentCallBack=fragmentCallBack;
   }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding= DataBindingUtil.inflate(inflater, R.layout.f_calender_layout,container,false);

        binding.btnOkCalender.setOnClickListener(this);
        binding.backOfCalender.setOnClickListener(this);
        ArrayList<Integer> list = new ArrayList<>();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(1, 2);
        Calendar lastYear = Calendar.getInstance();
        lastYear.add(1, -2);
        binding.calendarView.init(lastYear.getTime(), nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE).withSelectedDate(new Date())
                .withDeactivateDates(list).withHighlightedDates(new ArrayList<Date>());


        return binding.getRoot();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back_of_calender) {
           dismiss();
        }
        else if (id == R.id.btn_ok_calender) {
            List<Date> dates =binding.calendarView.getSelectedDates();
            Bundle bundle=new Bundle();
            bundle.putLong("startDate",dates.get(0).getTime());
            bundle.putLong("endDate",dates.get((dates.size()-1)).getTime());
            fragmentCallBack.onResponce(bundle);
            dismiss();
        }
    }
}
