package com.ellenluo.minimaList;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Ellen Luo on 2016-08-16.
 */
public class WidgetConfigureActivity extends AppCompatActivity {

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    Spinner listSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configure);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Helper h = new Helper(this);
        toolbar.setPadding(0, h.getStatusBarHeight(), 0, 0);

        // in case user presses back button
        setResult(RESULT_CANCELED);

        // find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // if no app widget ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // set up list spinner
        listSpinner = (Spinner) findViewById(R.id.display_list);

        DBHandler db = new DBHandler(this);
        ArrayList<List> listList = db.getAllLists();
        int size = listList.size();

        String[] listSpinnerItem = new String[size + 1];
        listSpinnerItem[0] = "All Tasks";

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                listSpinnerItem[i + 1] = listList.get(i).getName();
            }
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listSpinnerItem);
        listSpinner.setAdapter(listAdapter);
    }

    // adding the widget
    private void addWidget() {
        // store preferences
        SharedPreferences pref = getSharedPreferences(String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
        pref.edit().putString("widget_list", listSpinner.getSelectedItem().toString()).apply();
        Log.w("WidgetConfigureActivity", "appWidgetId is " + String.valueOf(appWidgetId));

        // call widget update
        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, WidgetProvider.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});
        sendBroadcast(updateIntent);

        // launch widget
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, intent);
        finish();
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
            addWidget();
        }

        return super.onOptionsItemSelected(item);
    }

}
