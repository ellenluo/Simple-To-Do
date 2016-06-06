package com.ellenluo.simpleto_do;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addTask(View view) {
        EditText etName = (EditText) findViewById(R.id.add_task_task_name);
        EditText etText = (EditText) findViewById(R.id.add_task_task_details);
        DBHandler db = new DBHandler(this);

        db.addTask(new Task(etName.getText().toString(), etText.getText().toString()));
        Log.d("ADDTASK", "Task successfully added");

        Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
