package com.ellenluo.simpleto_do;

import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity implements FragmentTimePicker.OnTimeSetListener, FragmentDatePicker.OnDateSetListener {

    DBHandler db;
    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    String[] listSpinnerItem;
    ArrayList<List> listList;
    String listName = "";
    int size = 0;
    int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute = -1;

    TextView tvAddList;
    EditText etAddList;
    Spinner listSpinner, remindSpinner;

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

        // set up reminder spinner
        remindSpinner = (Spinner) findViewById(R.id.remind_spinner);
        ArrayList<String> remindSpinnerItem = new ArrayList<String>(1);
        remindSpinnerItem.add("Select custom time");
        ArrayAdapter<String> remindAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, remindSpinnerItem);
        remindSpinner.setAdapter(remindAdapter);

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

    // get index of item in spinner
    private int getIndex(String item)
    {
        int index = 0;

        for (int i = 0; i < listSpinner.getCount(); i++){
            if (listSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void setDate(View view) {
        DialogFragment datePicker = new FragmentDatePicker();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    // when date is set
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        selectedYear = year;
        selectedMonth = month;
        selectedDay = day;

        TextView tvDate = (TextView) findViewById(R.id.task_date);
        tvDate.setText(monthArray[month] + " " + day + ", " + year);

        // show time picker
        DialogFragment timePicker = new FragmentTimePicker();
        timePicker.show(getSupportFragmentManager(), "timePicker");
    }

    // when time is set
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedHour = hourOfDay;
        selectedMinute = minute;

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
        time.set(Calendar.MINUTE, minute);
        Date date = time.getTime();

        TextView tvTime = (TextView) findViewById(R.id.task_time);
        tvTime.setText(new SimpleDateFormat("hh:mm a").format(date));
    }

    // add task to database
    public void addTask(View view) {
        EditText etName = (EditText) findViewById(R.id.add_task_task_name);
        EditText etText = (EditText) findViewById(R.id.add_task_task_details);
        DBHandler db = new DBHandler(this);

        // check if new list was added
        if (etText.getVisibility() == View.VISIBLE && etAddList.getVisibility() == View.VISIBLE) {
            listName = etAddList.getText().toString();
            db.addList(new List(listName));
        }

        long millis = -1;

        if (selectedMinute != -1) {
            Calendar cal = Calendar.getInstance();
            cal.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
            millis = cal.getTimeInMillis();
        }

        // add task & return to main activity
        db.addTask(new Task(etName.getText().toString(), etText.getText().toString(), millis, listName));
        Toast.makeText(this, "New task successfully added", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
        startActivity(intent);
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
