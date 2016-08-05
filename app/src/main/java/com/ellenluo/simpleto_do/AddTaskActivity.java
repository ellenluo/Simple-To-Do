package com.ellenluo.simpleto_do;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity implements TimePickerFragment.OnTimeSetListener, DatePickerFragment.OnDateSetListener {

    DBHandler db;
    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    String[] listSpinnerItem;
    ArrayList<List> listList;
    String listName = "";
    int size = 0;
    int picker = 0;

    Calendar due;
    Calendar remind;

    TextView tvAddList;
    TextView tvDueDate;
    TextView tvRemindDate;
    TextView tvRemindTime;
    TextView tvDueTime;
    EditText etAddList;
    Spinner listSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        // initialize due date/time
        tvDueDate = (TextView) findViewById(R.id.task_date);
        tvDueTime = (TextView) findViewById(R.id.task_time);

        // make reminder date invisible
        tvRemindDate = (TextView) findViewById(R.id.remind_date);
        tvRemindTime = (TextView) findViewById(R.id.remind_time);
        final Button btnSetRemind = (Button) findViewById(R.id.remind_set);
        tvRemindDate.setVisibility(View.GONE);
        tvRemindTime.setVisibility(View.GONE);
        btnSetRemind.setVisibility(View.GONE);

        // set up reminder switch
        Switch remindSwitch = (Switch) findViewById(R.id.remind_switch);

        remindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvRemindDate.setVisibility(View.VISIBLE);
                    tvRemindTime.setVisibility(View.VISIBLE);
                    btnSetRemind.setVisibility(View.VISIBLE);
                    tvRemindDate.setText(tvDueDate.getText());
                    tvRemindTime.setText(tvDueTime.getText());

                    if (due != null) {
                        remind = due;
                    }
                } else {
                    tvRemindDate.setVisibility(View.GONE);
                    tvRemindTime.setVisibility(View.GONE);
                    btnSetRemind.setVisibility(View.GONE);
                }
            }
        });

        // make new list edittext & textview invisible
        tvAddList = (TextView) findViewById(R.id.add_task_instructions);
        etAddList = (EditText) findViewById(R.id.add_task_list_name);
        tvAddList.setVisibility(View.GONE);
        etAddList.setVisibility(View.GONE);

        // set up list spinner
        listSpinner = (Spinner) findViewById(R.id.add_task_list_spinner);

        db = new DBHandler(this);
        db.getReadableDatabase();
        listList = db.getAllLists();
        size = listList.size();

        listSpinnerItem = new String[size + 2];
        listSpinnerItem[0] = "Select one";
        listSpinnerItem[size + 1] = "Add new list";

        if (size > 0)
            for (int i = 0; i < listList.size(); i++)
                listSpinnerItem[i + 1] = listList.get(i).getName();

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listSpinnerItem);
        listSpinner.setAdapter(listAdapter);

        // set default list option
        pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        String curList = pref.getString("current_list", "All Tasks");
        listSpinner.setSelection(getIndex(curList));

        // if spinner item is selected
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == size + 1) {
                    tvAddList.setVisibility(View.VISIBLE);
                    etAddList.setVisibility(View.VISIBLE);
                } else if (position == 0) {
                    tvAddList.setVisibility(View.GONE);
                    etAddList.setVisibility(View.GONE);
                    listName = "";
                } else {
                    tvAddList.setVisibility(View.GONE);
                    etAddList.setVisibility(View.GONE);
                    listName = listSpinnerItem[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listName = "";
            }
        });
    }

    public void setDate(View view) {
        picker = 0;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    public void setRemind(View view) {
        picker = 1;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    // when date is set
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (picker == 0) {
            due = Calendar.getInstance();
            due.set(year, month, day);
            tvDueDate.setText(monthArray[month] + " " + day + ", " + year);
        } else {
            remind = Calendar.getInstance();
            remind.set(year, month, day);
            tvRemindDate.setText(monthArray[month] + " " + day + ", " + year);
        }

        // show time picker
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");
    }

    // when time is set
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (picker == 0) {
            due.set(Calendar.HOUR_OF_DAY, hourOfDay);
            due.set(Calendar.MINUTE, minute);
            Date date = due.getTime();
            tvDueTime.setText(new SimpleDateFormat("hh:mm a").format(date));
        } else {
            remind.set(Calendar.HOUR_OF_DAY, hourOfDay);
            remind.set(Calendar.MINUTE, minute);
            Date date = remind.getTime();
            tvRemindTime.setText(new SimpleDateFormat("hh:mm a").format(date));
        }
    }

    // add task to database
    public void addTask() {
        EditText etName = (EditText) findViewById(R.id.add_task_task_name);
        EditText etText = (EditText) findViewById(R.id.add_task_task_details);
        DBHandler db = new DBHandler(this);

        // check if new list was added
        if (etText.getVisibility() == View.VISIBLE && etAddList.getVisibility() == View.VISIBLE) {
            listName = etAddList.getText().toString();
            db.addList(new List(listName));
        }

        // get due date
        long dueMillis = -1;

        if (due != null) {
            dueMillis = due.getTimeInMillis();
        }

        // get reminder
        long remindMillis = -1;

        if (remind != null) {
            remindMillis = remind.getTimeInMillis();
        }


        // add task & return to main activity
        Task newTask = new Task(etName.getText().toString(), etText.getText().toString(), dueMillis, remindMillis, listName);
        db.addTask(newTask);
        Toast.makeText(this, "New task successfully added", Toast.LENGTH_SHORT).show();

        // set reminder
        if (remind != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(AddTaskActivity.this, AlarmManagerReceiver.class);
            intent.putExtra("text", etName.getText().toString());
            intent.putExtra("id", newTask.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmManager.set(AlarmManager.RTC, remindMillis, pendingIntent);
        }

        Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // get index of item in spinner
    private int getIndex(String item) {
        int index = 0;

        for (int i = 0; i < listSpinner.getCount(); i++) {
            if (listSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                index = i;
                break;
            }
        }
        return index;
    }

    // find height of status bar
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // inflates action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_task_toolbar, menu);
        return true;
    }

    // if action bar item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            addTask();
        }

        return super.onOptionsItemSelected(item);
    }
}
