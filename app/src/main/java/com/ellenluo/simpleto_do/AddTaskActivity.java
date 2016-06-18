package com.ellenluo.simpleto_do;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    DBHandler db;
    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    String[] spinnerItem;
    ArrayList<List> listList;
    String listName = "";
    int size = 0;

    TextView tvAddList, tvTime;
    Button btnTime;
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

        // make timepicker invisible
        tvTime = (TextView) findViewById(R.id.add_task_time);
        btnTime = (Button) findViewById(R.id.add_task_set_time);
        tvTime.setVisibility(View.GONE);
        btnTime.setVisibility(View.GONE);

        // make new list edittext & textview invisible
        tvAddList = (TextView) findViewById(R.id.add_task_instructions);
        etAddList = (EditText) findViewById(R.id.add_task_list_name);
        tvAddList.setVisibility(View.GONE);
        etAddList.setVisibility(View.GONE);

        // set up spinner
        listSpinner = (Spinner) findViewById(R.id.add_task_list_spinner);

        db = new DBHandler(this);
        db.getReadableDatabase();
        listList = db.getAllLists();
        size = listList.size();

        spinnerItem = new String[size + 2];
        spinnerItem[0] = "Select one";
        spinnerItem[size + 1] = "Add new list";

        if (size > 0)
            for (int i = 0; i < listList.size(); i++)
                spinnerItem[i + 1] = listList.get(i).getName();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItem);
        listSpinner.setAdapter(adapter);

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
                    listName = spinnerItem[position];
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

        tvTime.setVisibility(View.VISIBLE);
        btnTime.setVisibility(View.VISIBLE);
    }

    public void setTime(View view) {
        DialogFragment timePicker = new FragmentTimePicker();
        timePicker.show(getSupportFragmentManager(), "timePicker");
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

        // get time & date
        int hour = pref.getInt("hour", 0);
        int minutes = pref.getInt("minutes", 0);
        int day = pref.getInt("day", -1);
        int month = pref.getInt("month", -1);
        int year = pref.getInt("year", -1);
        long millis = -1;

        if (day != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            millis = calendar.getTimeInMillis();
        }

        // reset preferences
        pref.edit().remove("hour").apply();
        pref.edit().remove("minutes").apply();
        pref.edit().remove("day").apply();
        pref.edit().remove("month").apply();
        pref.edit().remove("year").apply();

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
