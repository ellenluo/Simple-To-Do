package com.ellenluo.simpleto_do;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TaskDetailsActivity extends AppCompatActivity {

    DBHandler db = new DBHandler(this);
    long id;
    Task curTask;
    private Handler handler = new Handler();

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
        toolbar.setPadding(0, Reference.getStatusBarHeight(this), 0, 0);

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

        // task completed checkbox
        CheckBox cbComplete = (CheckBox) findViewById(R.id.cb_complete);

        cbComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(TaskDetailsActivity.this, "'" + curTask.getName() + "' successfully removed", Toast.LENGTH_SHORT).show();

                    // delay deletion
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (db.getTasksFromList(curTask.getList()).size() == 1) {
                                pref.edit().putString("current_list", "All Tasks").apply();
                                db.deleteList(db.getList(curTask.getList()));
                                db.deleteTask(curTask);
                            } else {
                                db.deleteTask(curTask);
                            }

                            Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 500);
                }
            }
        });
    }

    // inflates action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_details_toolbar, menu);
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

    // make back button return to main activity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
