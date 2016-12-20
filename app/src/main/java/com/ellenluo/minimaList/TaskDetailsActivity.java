package com.ellenluo.minimaList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean militaryTime = prefSettings.getBoolean("24h", false);

        // get task from database
        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getLong("id");
        }
        curTask = db.getTask(id);

        // set task info
        TextView tvName = (TextView) findViewById(R.id.task_name);
        TextView tvDue = (TextView) findViewById(R.id.due_date);
        TextView tvRemind = (TextView) findViewById(R.id.reminder);
        TextView tvList = (TextView) findViewById(R.id.list);
        TextView tvDetails = (TextView) findViewById(R.id.details);

        tvName.setText(curTask.getName());
        tvDetails.setText(curTask.getDetails());

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
                tvDue.setText(new SimpleDateFormat("MMMM dd, yyyy").format(date) + " at " + new SimpleDateFormat("HH:mm").format(date));
            } else {
                tvDue.setText(new SimpleDateFormat("MMMM dd, yyyy").format(date) + " at " + new SimpleDateFormat("hh:mm a").format(date));
            }
        }

        // set remind
        if (curTask.getRemind() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curTask.getRemind());
            Date date = cal.getTime();

            if (militaryTime) {
                tvRemind.setText(new SimpleDateFormat("MMMM dd, yyyy").format(date) + " at " + new SimpleDateFormat("HH:mm").format(date));
            } else {
                tvRemind.setText(new SimpleDateFormat("MMMM dd, yyyy").format(date) + " at " + new SimpleDateFormat("hh:mm a").format(date));
            }
        }

        // task completed checkbox
        CheckBox cbComplete = (CheckBox) findViewById(R.id.cb_complete);

        cbComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(TaskDetailsActivity.this, "'" + curTask.getName() + "' successfully removed", Toast.LENGTH_SHORT).show();

                    // cancel any reminders
                    Intent intent = new Intent(TaskDetailsActivity.this, AlarmManagerReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskDetailsActivity.this, (int) curTask.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    // delay deletion
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            db.deleteTask(curTask);

                            // update widgets
                            Reference.updateWidgets(TaskDetailsActivity.this);

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
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return true;
    }

    // if action bar item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_edit) {
            Intent intent = new Intent(TaskDetailsActivity.this, EditTaskActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
    }

}
