package com.ellenluo.simpleto_do;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {

    DBHandler db = new DBHandler(this);
    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    EditText etName, etDetails, etAddList;
    Spinner listSpinner;
    TextView tvAddList;
    Task curTask;

    int id;
    String[] spinnerItem;
    ArrayList<List> listList;
    String listName = "";
    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        // get task from database
        pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        id = pref.getInt("id", 0);
        curTask = db.getTask(id);

        // set fields to current values
        etName = (EditText) findViewById(R.id.edit_task_task_name);
        etDetails = (EditText) findViewById(R.id.edit_task_task_details);
        etName.setText(curTask.getName());
        etDetails.setText(curTask.getDetails());

        // make new list edittext & textview invisible
        tvAddList = (TextView) findViewById(R.id.edit_task_instructions);
        etAddList = (EditText) findViewById(R.id.edit_task_list_name);
        tvAddList.setVisibility(View.GONE);
        etAddList.setVisibility(View.GONE);

        // set up spinner
        listSpinner = (Spinner) findViewById(R.id.edit_task_list_spinner);

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
        listSpinner.setSelection(getIndex(curTask.getList()));

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

    // find height of status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // save changes
    public void saveChanges(View view) {
        String newName = etName.getText().toString();
        String newDetails = etDetails.getText().toString();

        // check if new list was added
        if (tvAddList.getVisibility() == View.VISIBLE && etAddList.getVisibility() == View.VISIBLE) {
            listName = etAddList.getText().toString();
            db.addList(new List(listName));
        }

        // update task
        curTask.setName(newName);
        curTask.setList(listName);
        curTask.setDetails(newDetails);
        db.updateTask(curTask);
        pref.edit().putString("current_list", listName).apply();

        Intent intent = new Intent(EditTaskActivity.this, TaskDetailsActivity.class);
        startActivity(intent);
    }

}
