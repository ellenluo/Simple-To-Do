package com.ellenluo.minimaList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskListAdapter extends ArrayAdapter {
    ArrayList<Task> taskList;
    DBHandler db;
    Context context;

    public TaskListAdapter(Context context, ArrayList<Task> resource) {
        super(context, R.layout.task_row, resource);
        this.context = context;
        this.taskList = resource;
        db = new DBHandler(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.task_row, parent, false);

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean militaryTime = prefSettings.getBoolean("24h", false);

        // set name
        TextView tvName = (TextView) convertView.findViewById(R.id.task_row_name);
        tvName.setText(taskList.get(position).getName());

        // set list
        TextView tvList = (TextView) convertView.findViewById(R.id.task_row_list);

        if (taskList.get(position).getList() != -1) {
            String list = db.getList(taskList.get(position).getList()).getName();
            tvList.setText(list);
        }

        // get date & time text
        TextView tvDate = (TextView) convertView.findViewById(R.id.task_row_date);
        TextView tvTime = (TextView) convertView.findViewById(R.id.task_row_time);
        long millis = taskList.get(position).getDue();

        if (millis != -1) {
            // get calendars
            Calendar now = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);
            Calendar due = Calendar.getInstance();
            due.setTimeInMillis(millis);

            // get date
            Date date = due.getTime();

            // set text
            if (due.before(now))
                tvDate.setText("Overdue");
            else {
                if (militaryTime) {
                    tvTime.setText(new SimpleDateFormat("HH:mm").format(date));
                } else {
                    tvTime.setText(new SimpleDateFormat("hh:mm a").format(date));
                }

                if (now.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    tvDate.setText("Today");
                else if (tomorrow.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && tomorrow.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    tvDate.setText("Tomorrow");
                else if (now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    tvDate.setText(new SimpleDateFormat("MMM dd").format(date));
                else
                    tvDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(date));
            }
        }

        return convertView;
    }

}