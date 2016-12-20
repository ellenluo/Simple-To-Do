package com.ellenluo.minimaList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditTaskActivity extends AppCompatActivity implements TimePickerFragment.OnTimeSetListener, DatePickerFragment.OnDateSetListener {

    DBHandler db;
    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    Calendar due;
    Calendar remind;
    Task curTask;

    EditText etName;
    EditText etDetails;
    EditText etAddList;
    Spinner listSpinner;
    TextView tvAddList;
    TextView tvDueDate;
    TextView tvDueTime;
    TextView tvRemindDate;
    TextView tvRemindTime;
    Button btnClearDue;
    Button btnClearRemind;

    long id;
    int picker = 0;
    int size = 0;
    String[] spinnerItem;
    ArrayList<List> listList;
    long listId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, Reference.getStatusBarHeight(this), 0, 0);

        // get task from database
        pref = getSharedPreferences("Main", PREFERENCE_MODE_PRIVATE);
        id = getIntent().getExtras().getLong("id");
        db = new DBHandler(this);
        curTask = db.getTask(id);

        // set fields to current values
        etName = (EditText) findViewById(R.id.task_name);
        etDetails = (EditText) findViewById(R.id.task_details);
        etName.setText(curTask.getName());
        etDetails.setText(curTask.getDetails());

        // set up current due date/time
        tvDueDate = (TextView) findViewById(R.id.due_date);
        tvDueTime = (TextView) findViewById(R.id.due_time);
        btnClearDue = (Button) findViewById(R.id.clear_due);

        if (curTask.getDue() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curTask.getDue());
            Date date = cal.getTime();
            due = cal;

            tvDueDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(date));
            tvDueTime.setText(new SimpleDateFormat("hh:mm a").format(date));
            btnClearDue.setVisibility(View.VISIBLE);
        }

        // set up current reminder
        final Button btnSetRemind = (Button) findViewById(R.id.set_remind);
        Switch remindSwitch = (Switch) findViewById(R.id.remind_switch);
        tvRemindDate = (TextView) findViewById(R.id.remind_date);
        tvRemindTime = (TextView) findViewById(R.id.remind_time);
        btnClearRemind = (Button) findViewById(R.id.clear_remind);

        if (curTask.getRemind() == -1) {
            // make reminder date invisible
            tvRemindDate.setVisibility(View.GONE);
            tvRemindTime.setVisibility(View.GONE);
            btnSetRemind.setVisibility(View.GONE);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(curTask.getRemind());
            Date date = cal.getTime();
            remind = cal;

            tvRemindDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(date));
            tvRemindTime.setText(new SimpleDateFormat("hh:mm a").format(date));
            btnClearRemind.setVisibility(View.VISIBLE);
            remindSwitch.setChecked(true);
        }

        // set up reminder switch
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

        // initialize new list instructions/text field
        tvAddList = (TextView) findViewById(R.id.new_list_instructions);
        etAddList = (EditText) findViewById(R.id.list_name);

        // set up spinner
        listSpinner = (Spinner) findViewById(R.id.list_spinner);

        db = new DBHandler(this);
        //db.getReadableDatabase();
        listList = db.getAllLists();
        size = listList.size();

        spinnerItem = new String[size + 2];
        spinnerItem[0] = "Select one";
        spinnerItem[size + 1] = "Add new list";

        if (size > 0) {
            for (int i = 0; i < listList.size(); i++) {
                spinnerItem[i + 1] = listList.get(i).getName();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItem);
        listSpinner.setAdapter(adapter);

        if (curTask.getList() == -1) {
            listSpinner.setSelection(getIndex("All Tasks"));
        } else {
            listSpinner.setSelection(getIndex(db.getList(curTask.getList()).getName()));
        }

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
                    listId = db.getList(spinnerItem[position]).getId();
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
            due.set(year, month, day);
            tvDueDate.setText(monthArray[month] + " " + day + ", " + year);
            btnClearDue.setVisibility(View.VISIBLE);
        } else {
            remind = Calendar.getInstance();
            remind.set(year, month, day);
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
            tvDueTime.setText(new SimpleDateFormat("hh:mm a").format(date));
        } else {
            remind.set(Calendar.HOUR_OF_DAY, hourOfDay);
            remind.set(Calendar.MINUTE, minute);
            Date date = remind.getTime();
            tvRemindTime.setText(new SimpleDateFormat("hh:mm a").format(date));
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

    // save changes
    public boolean saveChanges() {
        String newName = etName.getText().toString();
        String newDetails = etDetails.getText().toString();

        // check if new list was added
        if (tvAddList.getVisibility() == View.VISIBLE && etAddList.getVisibility() == View.VISIBLE) {
            String listName = etAddList.getText().toString();

            // check for empty list name
            if (listName.length() == 0) {
                Reference.displayAlert(this, "New list name cannot be empty.", "Got it", "");
                return true;
            }

            // check for duplicate list name
            ArrayList<List> listList = db.getAllLists();

            for (int i = 0; i < listList.size(); i++) {
                if (listName.equals(listList.get(i).getName())) {
                    Reference.displayAlert(this, "List name already exists. Please enter a new list name.", "Got it", "");
                    return true;
                }
            }

            // check for "All Tasks" name
            if (listName.equals("All Tasks")) {
                Reference.displayAlert(this, "List name already exists. Please enter a new list name.", "Got it", "");
                return true;
            }

            List newList = new List(listName);
            db.addList(newList);
            listId = db.getList(listName).getId();
        }

        // get time & date
        long dueMillis = -1;

        if (due != null) {
            dueMillis = due.getTimeInMillis();
        }

        // get reminder
        long remindMillis = -1;

        if (remind != null) {
            remindMillis = remind.getTimeInMillis();
        }

        // set reminder
        if (remindMillis != curTask.getRemind()) {
            // cancel reminder
            Intent intent = new Intent(EditTaskActivity.this, AlarmManagerReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) curTask.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            if (remindMillis != -1 && remindMillis > System.currentTimeMillis()) {
                // set new reminder
                intent = new Intent(EditTaskActivity.this, AlarmManagerReceiver.class);
                intent.putExtra("text", curTask.getName());
                intent.putExtra("id", curTask.getId());
                pendingIntent = PendingIntent.getBroadcast(this, (int) curTask.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    Log.d("EditTaskActivity", "using set");
                    alarmManager.set(AlarmManager.RTC_WAKEUP, remindMillis, pendingIntent);
                } else {
                    Log.d("EditTaskActivity", "Using setExact");
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, remindMillis, pendingIntent);
                }
            }
        }

        // update task & print completion message
        curTask.setName(newName);
        curTask.setDetails(newDetails);
        curTask.setDue(dueMillis);
        curTask.setRemind(remindMillis);
        curTask.setList(listId);
        db.updateTask(curTask);
        Toast.makeText(this, "'" + curTask.getName() + "' successfully updated", Toast.LENGTH_SHORT).show();

        // update widgets
        Reference.updateWidgets(this);

        // return to list that edited task belongs to
        if (listId == -1) {
            pref.edit().putString("current_list", "All Tasks").apply();
        } else {
            pref.edit().putString("current_list", db.getList(listId).getName()).apply();
        }

        // return to task details
        Intent intent = new Intent(EditTaskActivity.this, TaskDetailsActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

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
            saveChanges();
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // back button confirmation
    @Override
    public void onBackPressed() {
        Reference.displayAlert(this, "Are you sure you want to discard your changes?", "Keep editing", "Discard");
    }

}
