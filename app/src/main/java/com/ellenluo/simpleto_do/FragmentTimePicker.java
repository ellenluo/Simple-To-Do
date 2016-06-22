package com.ellenluo.simpleto_do;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentTimePicker extends DialogFragment {

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;
    OnTimeSetListener listener;

    // interface
    public interface OnTimeSetListener extends TimePickerDialog.OnTimeSetListener {
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // set current time
        final Calendar c = Calendar.getInstance();
        int initHour = c.get(Calendar.HOUR_OF_DAY);
        int initMin = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), listener, initHour, initMin, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnTimeSetListener) activity;
    }

}

