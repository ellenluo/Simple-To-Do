package com.ellenluo.simpleto_do;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class FragmentTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // set current time
        final Calendar c = Calendar.getInstance();
        int initHour = c.get(Calendar.HOUR_OF_DAY);
        int initMin = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, initHour, initMin, DateFormat.is24HourFormat(getActivity()));
    }

    // when time is set
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tvDate= (TextView) getActivity().findViewById(R.id.add_task_time);
        tvDate.setText(hourOfDay + ":" + minute);
    }

}

