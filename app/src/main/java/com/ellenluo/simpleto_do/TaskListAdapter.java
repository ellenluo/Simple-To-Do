package com.ellenluo.simpleto_do;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskListAdapter extends ArrayAdapter {
    ArrayList<Task> taskList;
    Context context;

    public TaskListAdapter(Context context, ArrayList<Task> resource) {
        super(context, R.layout.task_row, resource);
        this.context = context;
        this.taskList = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.task_row, parent, false);

        // set name
        TextView tvName = (TextView) convertView.findViewById(R.id.task_row_name);
        tvName.setText(taskList.get(position).getName());

        // set list
        TextView tvList = (TextView) convertView.findViewById(R.id.task_row_list);

        if (!taskList.get(position).getList().equals(""))
            tvList.setText(taskList.get(position).getList());

        // get date & time
        TextView tvDate = (TextView) convertView.findViewById(R.id.task_row_date);
        TextView tvTime = (TextView) convertView.findViewById(R.id.task_row_time);
        long millis = taskList.get(position).getDue();

        if (millis != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            if (Calendar.getInstance().get(Calendar.YEAR) == year)
                tvDate.setText(monthArray[month] + " " + day);
            else
                tvDate.setText(monthArray[month] + " " + day + ", " + year);


            String time = hour + ":" + minutes;

            try {
                final SimpleDateFormat format = new SimpleDateFormat("H:mm");
                final Date date = format.parse(time);
                tvTime.setText(new SimpleDateFormat("hh:mm a").format(date));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

}