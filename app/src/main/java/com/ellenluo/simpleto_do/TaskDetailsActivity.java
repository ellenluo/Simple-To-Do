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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class TaskDetailsActivity extends AppCompatActivity {

    DBHandler db = new DBHandler(this);
    long id;
    Task curTask;

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
        id = pref.getLong("id", 0);
        curTask = db.getTask(id);

        // set task info
        TextView tvName = (TextView) findViewById(R.id.task_details_name);
        TextView tvDue = (TextView) findViewById(R.id.task_details_due);
        TextView tvRemind = (TextView) findViewById(R.id.task_details_remind);
        TextView tvList = (TextView) findViewById(R.id.task_details_list);
        TextView tvDetails = (TextView) findViewById(R.id.task_details_details);

        tvName.setText(curTask.getName());
        tvList.setText(curTask.getList());
        tvDetails.setText(curTask.getDetails());

        // set due date
        if (curTask.getDue() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curTask.getDue());
            Date date = cal.getTime();
            tvDue.setText(new SimpleDateFormat("MMMM dd, yyyy").format(date) + " at " + new SimpleDateFormat("hh:mm a").format(date));
        }

        // set remind
        if (curTask.getRemind() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curTask.getRemind());
            Date date = cal.getTime();
            tvRemind.setText(new SimpleDateFormat("MMMM dd, yyyy").format(date) + " at " + new SimpleDateFormat("hh:mm a").format(date));
        }
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

    // make back button return to main activity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
