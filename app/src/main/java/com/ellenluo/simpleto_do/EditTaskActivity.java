package com.ellenluo.simpleto_do;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {

    DBHandler db = new DBHandler(this);
    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    EditText etName, etDetails;
    Task curTask;
    int id;

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

        // set text fields to current values
        etName = (EditText) findViewById(R.id.edit_task_task_name);
        etDetails = (EditText) findViewById(R.id.edit_task_task_details);
        etName.setText(curTask.getName());
        etDetails.setText(curTask.getDetails());
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

        curTask.setName(newName);
        curTask.setDetails(newDetails);
        db.updateTask(curTask);

        Intent intent = new Intent(EditTaskActivity.this, TaskDetailsActivity.class);
        startActivity(intent);
    }

}
