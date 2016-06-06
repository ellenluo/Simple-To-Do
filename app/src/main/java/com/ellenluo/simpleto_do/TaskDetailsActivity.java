package com.ellenluo.simpleto_do;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class TaskDetailsActivity extends AppCompatActivity {

    DBHandler db = new DBHandler(this);
    ArrayList<Task> taskList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        taskList = db.getAllTasks();
        int position = getIntent().getExtras().getInt("position");

        TextView tvName = (TextView) findViewById(R.id.task_details_name);
        TextView tvDetails = (TextView) findViewById(R.id.task_details_details);

        tvName.setText(taskList.get(position).getName());
        tvDetails.setText(taskList.get(position).getDetails());
    }

}
