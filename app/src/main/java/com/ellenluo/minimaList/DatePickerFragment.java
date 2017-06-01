package com.ellenluo.minimaList;

/**
 * DatePickerFragment
 * Created by Ellen Luo
 * DialogFragment that displays a calendar date picker.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    OnDateSetListener listener;

    // interface
    public interface OnDateSetListener extends DatePickerDialog.OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int day);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // set current date
        final Calendar c = Calendar.getInstance();
        int initYear = c.get(Calendar.YEAR);
        int initMonth = c.get(Calendar.MONTH);
        int initDay = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), listener, initYear, initMonth, initDay);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnDateSetListener) activity;
    }

}
