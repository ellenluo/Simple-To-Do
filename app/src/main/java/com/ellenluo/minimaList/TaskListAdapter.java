package com.ellenluo.minimaList;

/*
 * TaskListAdapter
 * Created by Ellen Luo
 * ArrayAdapter that displays task info with a custom row layout.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class TaskListAdapter extends ArrayAdapter<Task> {
    private DBHandler db;
    private ArrayList<Task> taskList;
    private Context context;

    TaskListAdapter(Context context, ArrayList<Task> resource) {
        super(context, R.layout.task_row, resource);
        this.context = context;
        this.taskList = resource;
        this.db = new DBHandler(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.task_row, parent, false);
        } else {
            v = convertView;
        }

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean militaryTime = prefSettings.getBoolean("24h", false);
        int color = prefSettings.getInt("theme_color", ContextCompat.getColor(this.context, R.color.dark_blue));

        // set name
        Task curTask = this.taskList.get(position);
        TextView tvName = (TextView) v.findViewById(R.id.task_row_name);
        tvName.setText(curTask.getName());

        // set list
        TextView tvList = (TextView) v.findViewById(R.id.task_row_list);
        tvList.setTextColor(color);

        if (curTask.getList() != -1) {
            String list = this.db.getList(curTask.getList()).getName();
            tvList.setText(list);
        }

        // get due date & time text
        TextView tvDate = (TextView) v.findViewById(R.id.task_row_date);
        tvDate.setTextColor(color);
        TextView tvTime = (TextView) v.findViewById(R.id.task_row_time);
        long millis = curTask.getDue();

        if (millis != -1) {
            // get calendars
            Calendar now = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);
            Calendar due = Calendar.getInstance();
            due.setTimeInMillis(millis);

            // get date
            Date date = due.getTime();

            // set due date & time text
            if (due.before(now))
                tvDate.setText(this.context.getString(R.string.task_row_overdue));
            else {
                // set time
                if (militaryTime) {
                    tvTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date));
                } else {
                    tvTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date));
                }

                // set date
                if (now.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    tvDate.setText(this.context.getString(R.string.task_row_today));
                else if (tomorrow.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && tomorrow.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    tvDate.setText(this.context.getString(R.string.task_row_tomorrow));
                else if (now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    tvDate.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(date));
                else
                    tvDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date));
            }
        }

        return v;
    }

}