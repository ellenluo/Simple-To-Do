package com.ellenluo.simpleto_do;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class TaskDetailsActivity extends AppCompatActivity {

    DBHandler db = new DBHandler(this);
    int id;

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        // get task from database
        pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        id = pref.getInt("id", 0);
        Task curTask = db.getTask(id);

        // set task details
        TextView tvName = (TextView) findViewById(R.id.task_details_name);
        TextView tvDetails = (TextView) findViewById(R.id.task_details_details);

        tvName.setText(curTask.getName());
        tvDetails.setText(curTask.getDetails());
    }

    // inflates action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    // if action bar item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent(TaskDetailsActivity.this, EditTaskActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // find height of status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
