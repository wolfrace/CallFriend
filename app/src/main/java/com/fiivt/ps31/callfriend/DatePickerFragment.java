package com.fiivt.ps31.callfriend;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import lombok.Setter;

public class DatePickerFragment extends DialogFragment
        implements android.app.DatePickerDialog.OnDateSetListener {

    @Setter
    private OnDateSetListener onDateSetListener;

    public interface OnDateSetListener {
        void onDateSet(Date date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        if (onDateSetListener != null) {
            Date date = calendar.getTime();
            onDateSetListener.onDateSet(date);
        }
    }
}