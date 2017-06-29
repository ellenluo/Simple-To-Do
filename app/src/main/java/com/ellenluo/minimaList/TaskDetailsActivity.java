package com.ellenluo.minimaList;

/**
 * TaskDetailsActivity
 * Created by Ellen Luo
 * Activity that allows users to view the parameters of a task.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TaskDetailsActivity extends AppCompatActivity {

    private DBHandler db = new DBHandler(this);

    private Task curTask;
    private Handler handler = new Handler();
    private Helper h;

    private long id;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google analytics
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);

        // set theme
        h = new Helper(this);
        h.setTheme();

        setContentView(R.layout.activity_task_details);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setPadding(0, h.getStatusBarHeight(), 0, 0);
        }

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean militaryTime = prefSettings.getBoolean("24h", false);

        // get task from database
        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getLong("id");
        }
        Log.d("TaskDetailsActivity", "id is " + id);
        curTask = db.getTask(id);

        // set task info
        TextView tvName = (TextView) findViewById(R.id.task_name);
        TextView tvDue = (TextView) findViewById(R.id.due_date);
        TextView tvRemind = (TextView) findViewById(R.id.reminder);
        TextView tvRepeatLabel = (TextView) findViewById(R.id.repeat_label);
        TextView tvRepeat = (TextView) findViewById(R.id.repeat);
        TextView tvList = (TextView) findViewById(R.id.list);
        TextView tvDetails = (TextView) findViewById(R.id.details);

        tvName.setText(curTask.getName());
        tvDetails.setText(curTask.getDetails());
        tvRepeat.setText(getResources().getStringArray(R.array.repeat_options)[(int) curTask.getRepeat()]);

        // set list
        if (curTask.getList() != -1) {
            tvList.setText(db.getList(curTask.getList()).getName());
        }

        // set due date
        if (curTask.getDue() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curTask.getDue());
            Date date = cal.getTime();

            if (militaryTime) {
                tvDue.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date) + " " +
                        getString(R.string.details_at) + " "
                        + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date));
            } else {
                tvDue.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date) + " " +
                        getString(R.string.details_at) + " "
                        + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date));
            }
        }

        // set remind
        if (curTask.getRemind() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curTask.getRemind());
            Date date = cal.getTime();

            if (militaryTime) {
                tvRemind.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date) + " " +
                        getString(R.string.details_at) + " " + new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(date));
            } else {
                tvRemind.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date) + " " +
                        getString(R.string.details_at) + " " + new SimpleDateFormat("hh:mm a", Locale.getDefault())
                        .format(date));
            }

            tvRepeatLabel.setVisibility(View.VISIBLE);
            tvRepeat.setVisibility(View.VISIBLE);
        }

        // task completed checkbox
        CheckBox cbComplete = (CheckBox) findViewById(R.id.cb_complete);

        cbComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(TaskDetailsActivity.this, "'" + curTask.getName() + "' " +
                            getString(R.string.delete_task_confirmation), Toast.LENGTH_SHORT).show();

                    // cancel any reminders
                    h.cancelReminder(curTask.getId());

                    // delay deletion
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            db.deleteTask(curTask);

                            // update widgets
                            h.updateWidgets();

                            // return to main activity
                            Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
                            startActivity(intent);
                            TaskDetailsActivity.this.finish();
                        }
                    }, 500);
                }
            }
        });
    }

    // inflates action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return true;
    }

    // if action bar item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // edit task
        if (itemId == R.id.action_edit) {
            Intent intent = new Intent(TaskDetailsActivity.this, EditTaskActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
            TaskDetailsActivity.this.finish();
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // if activity started from notification
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.hasExtra("id")) {
            id = getIntent().getExtras().getLong("id");
        }
    }

    // make back button return to main activity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        TaskDetailsActivity.this.finish();
    }

    // close database
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

}
