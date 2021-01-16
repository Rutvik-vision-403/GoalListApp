package com.example.goallist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.goallist.Database.DatabaseHelper;

import java.util.Calendar;

public class SetDateCalenderFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    final TextView showDate;

    public SetDateCalenderFragment(TextView date){
        showDate = date;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        showDate.setText(String.format(" %d/%d/%d", i2, i1 + 1, i));
    }
}
