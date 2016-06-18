package com.ellenluo.simpleto_do;

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

public class FragmentTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // set current time
        final Calendar c = Calendar.getInstance();
        int initHour = c.get(Calendar.HOUR_OF_DAY);
        int initMin = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, initHour, initMin, DateFormat.is24HourFormat(getActivity()));
    }

    // when time is set
    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
        TextView tvDate= (TextView) getActivity().findViewById(R.id.add_task_time);
        String time = hourOfDay + ":" + minutes;

        pref = getActivity().getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        pref.edit().putInt("hour", hourOfDay).apply();
        pref.edit().putInt("minutes", minutes).apply();

        try {
            final SimpleDateFormat format = new SimpleDateFormat("H:mm");
            final Date date = format.parse(time);
            tvDate.setText(new SimpleDateFormat("hh:mm a").format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

