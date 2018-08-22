package com.ellenluo.minimaList;

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

import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * AddTaskActivity
 * Created by Ellen Luo
 * Activity that allows users to create a new task with customizable name, due date, reminder, list and details.
 */
public class AddTaskActivity extends AppCompatActivity implements TimePickerFragment.OnTimeSetListener,
        DatePickerFragment.OnDateSetListener {

    private DBHandler db;
    private SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    private Calendar due;
    private Calendar remind;
    private Helper h;

    private TextView tvAddList;
    private TextView tvDueDate;
    private TextView tvRemindDate;
    private TextView tvRemindTime;
    private TextView tvDueTime;
    private TextView tvRepeat;
    private EditText etAddList;
    private Button btnClearDue;
    private Button btnClearRemind;
    private Spinner repeatSpinner;

    private String[] listSpinnerItem;
    private ArrayList<List> listList;
    private long listId = -1;
    private int size = 0;
    private int picker = 0;
    boolean militaryTime = false;

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

        setContentView(R.layout.activity_add_task);

        // set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP &&
                getSupportActionBar() != null) {
            toolbar.setPadding(0, h.getStatusBarHeight(), 0, 0);
        }

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        militaryTime = prefSettings.getBoolean("24h", false);

        // initialize due date/time
        tvDueDate = findViewById(R.id.due_date);
        tvDueTime = findViewById(R.id.due_time);
        btnClearDue = findViewById(R.id.clear_due);

        // initialize reminder date/time/repeat
        tvRemindDate = findViewById(R.id.remind_date);
        tvRemindTime = findViewById(R.id.remind_time);
        final Button btnSetRemind = findViewById(R.id.set_remind);
        btnClearRemind = findViewById(R.id.clear_remind);
        tvRepeat = findViewById(R.id.repeat);
        repeatSpinner = findViewById(R.id.repeat_spinner);
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(this, R.array.repeat_options,
                android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(repeatAdapter);

        // initialize new list instructions/text field
        tvAddList = findViewById(R.id.new_list_instructions);
        etAddList = findViewById(R.id.list_name);

        // set up reminder switch
        Switch remindSwitch = findViewById(R.id.remind_switch);

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
                    tvRepeat.setVisibility(View.GONE);
                    repeatSpinner.setVisibility(View.GONE);
                    remind = null;
                }
            }
        });

        // set up list spinner
        Spinner listSpinner = findViewById(R.id.list_spinner);

        db = new DBHandler(this);
        listList = db.getAllLists();
        size = listList.size();

        listSpinnerItem = new String[size + 2];
        listSpinnerItem[0] = getString(R.string.add_task_no_list);
        listSpinnerItem[size + 1] = getString(R.string.add_task_new_list);

        if (size > 0) {
            for (int i = 0; i < listList.size(); i++) {
                listSpinnerItem[i + 1] = listList.get(i).getName();
            }
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                listSpinnerItem);
        listSpinner.setAdapter(listAdapter);

        // set default list option
        pref = getSharedPreferences("Main", PREFERENCE_MODE_PRIVATE);
        String curList = pref.getString("current_list", "All Tasks");
        listSpinner.setSelection(h.getIndex(curList, listSpinner));

        // if list item is selected
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

    // when set due button clicked
    public void setDue(View view) {
        picker = 0;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    // when set reminder button clicked
    public void setRemind(View view) {
        picker = 1;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    // when clear due button clicked
    public void clearDue(View view) {
        tvDueDate.setText(getString(R.string.add_task_no_date));
        tvDueTime.setText("");
        due = null;
        btnClearDue.setVisibility(View.GONE);
    }

    // when clear reminder button clicked
    public void clearRemind(View view) {
        tvRemindDate.setText(getString(R.string.add_task_no_date));
        tvRemindTime.setText("");
        remind = null;
        btnClearRemind.setVisibility(View.GONE);
    }

    // when date is set
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String[] monthArray = getResources().getStringArray(R.array.month_abbr);

        if (picker == 0) {
            due = Calendar.getInstance();
            due.set(year, month, day, 0, 0, 0);
            tvDueDate.setText(getString(R.string.add_task_due_date, monthArray[month], day, year));
            btnClearDue.setVisibility(View.VISIBLE);
        } else {
            remind = Calendar.getInstance();
            remind.set(year, month, day, 0, 0, 0);
            tvRemindDate.setText(getString(R.string.add_task_due_date, monthArray[month], day, year));
            btnClearRemind.setVisibility(View.VISIBLE);
        }

        // show time picker
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");

        // show repeating options
        tvRepeat.setVisibility(View.VISIBLE);
        repeatSpinner.setVisibility(View.VISIBLE);
    }

    // when time is set
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (picker == 0) {
            due.set(Calendar.HOUR_OF_DAY, hourOfDay);
            due.set(Calendar.MINUTE, minute);
            Date date = due.getTime();

            if (militaryTime) {
                tvDueTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date));
            } else {
                tvDueTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date));
            }
        } else {
            remind.set(Calendar.HOUR_OF_DAY, hourOfDay);
            remind.set(Calendar.MINUTE, minute);
            Date date = remind.getTime();

            if (militaryTime) {
                tvRemindTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date));
            } else {
                tvRemindTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date));
            }
        }
    }

    // add task to database
    @SuppressWarnings("ConstantConditions")
    private void addTask() {
        EditText etName = findViewById(R.id.task_name);
        EditText etDetails = findViewById(R.id.task_details);
        String taskName = etName.getText().toString().trim();
        String details = etDetails.getText().toString();

        // check for empty task name
        if (taskName.length() == 0) {
            h.displayAlert(getString(R.string.dialog_empty_task), getString(R.string.dialog_confirmation), "");
            return;
        }

        // check if new list was added
        if (tvAddList.getVisibility() == View.VISIBLE && etAddList.getVisibility() == View.VISIBLE) {
            String listName = etAddList.getText().toString().trim();

            // check for empty list name
            if (listName.length() == 0) {
                h.displayAlert(getString(R.string.dialog_empty_list), getString(R.string.dialog_confirmation), "");
                return;
            }

            // check for duplicate list name
            listList = db.getAllLists();

            for (int i = 0; i < listList.size(); i++) {
                if (listName.equals(listList.get(i).getName())) {
                    h.displayAlert(getString(R.string.dialog_duplicate_list), getString(R.string.dialog_confirmation)
                            , "");
                    return;
                }
            }

            // check for "All Tasks" name
            if (listName.equals("All Tasks")) {
                h.displayAlert(getString(R.string.dialog_duplicate_list), getString(R.string.dialog_confirmation), "");
                return;
            }

            // add new list
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
        int repeat = 0;
        long nextRemindMillis = -1;

        if (remind != null) {
            remindMillis = remind.getTimeInMillis();
            repeat = repeatSpinner.getSelectedItemPosition();
            nextRemindMillis = remindMillis;
        }

        // add task & print completion message
        Task newTask = new Task(taskName, details, dueMillis, remindMillis, repeat, nextRemindMillis, listId);
        db.addTask(newTask);
        Toast.makeText(this, getString(R.string.add_task_confirmation), Toast.LENGTH_SHORT).show();

        // set reminder
        if (remindMillis != -1 && remindMillis > System.currentTimeMillis()) {
            h.setReminder(taskName, newTask.getId(), remindMillis);
        }

        // update widgets
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
        h.displayAlert(getString(R.string.dialog_discard_task), getString(R.string.dialog_discard_cancel), getString
                (R.string.dialog_discard));
    }

    // close database
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

}
