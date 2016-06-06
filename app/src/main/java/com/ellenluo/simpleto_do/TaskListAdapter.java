package com.ellenluo.simpleto_do;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskListAdapter extends ArrayAdapter<Task> {
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

        TextView textView = (TextView) convertView.findViewById(R.id.task_row_name);
        textView.setText(taskList.get(position).getName());

        return convertView;
    }

}