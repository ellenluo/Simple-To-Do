package com.ellenluo.simpleto_do;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FragmentDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // set current date
        final Calendar c = Calendar.getInstance();
        int initYear = c.get(Calendar.YEAR);
        int initMonth = c.get(Calendar.MONTH);
        int initDay = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, initYear, initMonth, initDay);
    }

    // when date is set
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        pref = getActivity().getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        pref.edit().putInt("year", year).apply();
        pref.edit().putInt("month", month).apply();
        pref.edit().putInt("day", day).apply();

        TextView tvDate= (TextView) getActivity().findViewById(R.id.add_task_date);
        tvDate.setText(monthArray[month] + " " + day + ", " + year);
    }

}
