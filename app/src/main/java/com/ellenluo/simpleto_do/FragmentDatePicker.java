package com.ellenluo.simpleto_do;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FragmentDatePicker extends DialogFragment {

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;
    OnDateSetListener listener;

    // interface
    public interface OnDateSetListener extends DatePickerDialog.OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int day);
    }

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
