package com.ellenluo.minimaList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    long listId = -1;
    int size = 0;
    int picker = 0;
    boolean militaryTime = false;

    Calendar due;
    Calendar remind;

    TextView tvAddList;
    TextView tvDueDate;
    TextView tvRemindDate;
    TextView tvRemindTime;
    TextView tvDueTime;
    EditText etAddList;
    Spinner listSpinner;
    Button btnClearDue;
    Button btnClearRemind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Helper h = new Helper(this);
        toolbar.setPadding(0, h.getStatusBarHeight(), 0, 0);

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        militaryTime = prefSettings.getBoolean("24h", false);

        // initialize due date/time
        tvDueDate = (TextView) findViewById(R.id.due_date);
        tvDueTime = (TextView) findViewById(R.id.due_time);
        btnClearDue = (Button) findViewById(R.id.clear_due);

        // initialize reminder date/time
        tvRemindDate = (TextView) findViewById(R.id.remind_date);
        tvRemindTime = (TextView) findViewById(R.id.remind_time);
        final Button btnSetRemind = (Button) findViewById(R.id.set_remind);
        btnClearRemind = (Button) findViewById(R.id.clear_remind);

        // initialize new list instructions/text field
        tvAddList = (TextView) findViewById(R.id.new_list_instructions);
        etAddList = (EditText) findViewById(R.id.list_name);

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
                        btnClearRemind.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvRemindDate.setVisibility(View.GONE);
                    tvRemindTime.setVisibility(View.GONE);
                    btnSetRemind.setVisibility(View.GONE);
                    btnClearRemind.setVisibility(View.GONE);
                    remind = null;
                }
            }
        });

        // set up list spinner
        listSpinner = (Spinner) findViewById(R.id.list_spinner);

        db = new DBHandler(this);
        //db.getReadableDatabase();
        listList = db.getAllLists();
        size = listList.size();

        listSpinnerItem = new String[size + 2];
        listSpinnerItem[0] = "None";
        listSpinnerItem[size + 1] = "Add new list";

        if (size > 0) {
            for (int i = 0; i < listList.size(); i++) {
                listSpinnerItem[i + 1] = listList.get(i).getName();
            }
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listSpinnerItem);
        listSpinner.setAdapter(listAdapter);

        // set default list option
        pref = getSharedPreferences("Main", PREFERENCE_MODE_PRIVATE);
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
                    listId = -1;
                } else {
                    tvAddList.setVisibility(View.GONE);
                    etAddList.setVisibility(View.GONE);
                    listId = db.getList(listSpinnerItem[position]).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listId = -1;
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

    public void clearDue(View view) {
        tvDueDate.setText("No date selected");
        tvDueTime.setText("");
        due = null;
        btnClearDue.setVisibility(View.GONE);
    }

    public void clearRemind(View view) {
        tvRemindDate.setText("No date selected");
        tvRemindTime.setText("");
        remind = null;
        btnClearRemind.setVisibility(View.GONE);
    }

    // when date is set
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (picker == 0) {
            due = Calendar.getInstance();
            due.set(year, month, day, 0, 0, 0);
            tvDueDate.setText(monthArray[month] + " " + day + ", " + year);
            btnClearDue.setVisibility(View.VISIBLE);
        } else {
            remind = Calendar.getInstance();
            remind.set(year, month, day, 0, 0, 0);
            tvRemindDate.setText(monthArray[month] + " " + day + ", " + year);
            btnClearRemind.setVisibility(View.VISIBLE);
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

            if (militaryTime) {
                tvDueTime.setText(new SimpleDateFormat("HH:mm").format(date));
            } else {
                tvDueTime.setText(new SimpleDateFormat("hh:mm a").format(date));
            }
        } else {
            remind.set(Calendar.HOUR_OF_DAY, hourOfDay);
            remind.set(Calendar.MINUTE, minute);
            Date date = remind.getTime();

            if (militaryTime) {
                tvRemindTime.setText(new SimpleDateFormat("HH:mm").format(date));
            } else {
                tvRemindTime.setText(new SimpleDateFormat("hh:mm a").format(date));
            }
        }
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

    // add task to database
    public boolean addTask() {
        EditText etName = (EditText) findViewById(R.id.task_name);
        EditText etText = (EditText) findViewById(R.id.task_details);
        DBHandler db = new DBHandler(this);

        // check for empty task name
        if (etName.getText().toString().trim().length() == 0) {
            Helper h = new Helper(this);
            h.displayAlert("Task name cannot be empty.", "Got it", "");
            return true;
        }

        // check if new list was added
        if (etText.getVisibility() == View.VISIBLE && etAddList.getVisibility() == View.VISIBLE) {
            String listName = etAddList.getText().toString().trim();

            // check for empty list name
            if (listName.length() == 0) {
                Helper h = new Helper(this);
                h.displayAlert("New list name cannot be empty.", "Got it", "");
                return true;
            }

            // check for duplicate list name
            ArrayList<List> listList = db.getAllLists();

            for (int i = 0; i < listList.size(); i++) {
                if (listName.equals(listList.get(i).getName())) {
                    Helper h = new Helper(this);
                    h.displayAlert("List name already exists. Please enter a new list name.", "Got it", "");
                    return true;
                }
            }

            // check for "All Tasks" name
            if (listName.equals("All Tasks")) {
                Helper h = new Helper(this);
                h.displayAlert("List name already exists. Please enter a new list name.", "Got it", "");
                return true;
            }

            List newList = new List(listName);
            db.addList(newList);
            listId = db.getList(listName).getId();
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

        // add task & print completion message
        Task newTask = new Task(etName.getText().toString().trim(), etText.getText().toString(), dueMillis, remindMillis, listId);
        db.addTask(newTask);
        Toast.makeText(this, "New task successfully added", Toast.LENGTH_SHORT).show();

        // set reminder
        if (remindMillis != -1 && remindMillis > System.currentTimeMillis()) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(AddTaskActivity.this, AlarmManagerReceiver.class);
            intent.putExtra("text", etName.getText().toString());
            intent.putExtra("id", newTask.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) newTask.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, remindMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, remindMillis, pendingIntent);
            }
        }

        // update widgets
        Helper h = new Helper(this);
        h.updateWidgets();

        // return to list that new task belongs to
        if (listId == -1) {
            pref.edit().putString("current_list", "All Tasks").apply();
        } else {
            pref.edit().putString("current_list", db.getList(listId).getName()).apply();
        }

        // return to main activity
        Intent returnIntent = new Intent(AddTaskActivity.this, MainActivity.class);
        startActivity(returnIntent);
        AddTaskActivity.this.finish();
        return true;
    }

    // inflates action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkmark_toolbar, menu);
        return true;
    }

    // if action bar item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            addTask();
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // back button confirmation
    @Override
    public void onBackPressed() {
        Helper h = new Helper(this);
        h.displayAlert("Are you sure you want to discard this task?", "Keep editing", "Discard");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
