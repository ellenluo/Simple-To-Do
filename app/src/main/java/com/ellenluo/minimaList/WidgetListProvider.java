package com.ellenluo.minimaList;

/*
 * WidgetListProvider
 * Created by Ellen Luo
 * RemoteViewsFactory that populates a widget with tasks in the selected list using a custom row layout.
 */

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {

    private DBHandler db;
    private SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    private ArrayList<Task> taskList;
    private Context context = null;

    private String list;

    WidgetListProvider(Context context, Intent intent) {
        this.context = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        // get widget display list
        this.pref = this.context.getSharedPreferences(String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
        this.list = this.pref.getString("widget_list", "All Tasks");

        // set task list to display
        this.db = new DBHandler(this.context);

        if (list.equals("All Tasks")) {
            this.taskList = this.db.getAllTasks();
        } else {
            this.taskList = this.db.getTasksFromList(this.db.getList(this.list).getId());
        }
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDataSetChanged() {
        String newList = this.pref.getString("widget_list", "All Tasks");

        if (newList.equals("All Tasks")) {
            this.taskList = this.db.getAllTasks();

            // update widget header
            updateHeader();
        } else if (!newList.equals(list)) {
            this.taskList = this.db.getTasksFromList(this.db.getList(newList).getId());

            // update widget header
            updateHeader();
        } else {
            this.taskList = this.db.getTasksFromList(this.db.getList(this.list).getId());
        }
    }

    // update widget header
    private void updateHeader() {
        Intent intent = new Intent(this.context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(this.context).getAppWidgetIds(new ComponentName(this.context, WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        this.context.sendBroadcast(intent);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    // close database
    @Override
    public void onDestroy() {
        this.db.close();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return this.taskList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(this.context.getPackageName(), R.layout.task_row);
        Task curTask = this.taskList.get(position);

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this.context);
        boolean militaryTime = prefSettings.getBoolean("24h", false);
        int color = prefSettings.getInt("theme_color", ContextCompat.getColor(this.context, R.color.dark_blue));

        // set name and list
        if (curTask.getList() != -1) {
            remoteView.setTextViewText(R.id.task_row_list, this.db.getList(curTask.getList()).getName());
        } else {
            remoteView.setTextViewText(R.id.task_row_list, this.context.getString(R.string.details_no_list));
        }

        remoteView.setTextViewText(R.id.task_row_name, curTask.getName());

        // set font sizes & colors
        remoteView.setTextViewTextSize(R.id.task_row_name, TypedValue.COMPLEX_UNIT_SP, 14);
        remoteView.setTextViewTextSize(R.id.task_row_time, TypedValue.COMPLEX_UNIT_SP, 12);
        remoteView.setTextViewTextSize(R.id.task_row_date, TypedValue.COMPLEX_UNIT_SP, 12);
        remoteView.setTextColor(R.id.task_row_name, Color.parseColor("#757575"));
        remoteView.setTextColor(R.id.task_row_time, Color.parseColor("#757575"));
        remoteView.setTextColor(R.id.task_row_list, color);
        remoteView.setTextColor(R.id.task_row_date, color);

        // set due date & time text
        long millis = curTask.getDue();
        if (millis != -1) {
            // get calendars
            Calendar now = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);
            Calendar due = Calendar.getInstance();
            due.setTimeInMillis(millis);

            // get date
            Date date = due.getTime();

            if (due.before(now))
                remoteView.setTextViewText(R.id.task_row_date, this.context.getString(R.string.task_row_overdue));
            else {
                // set time
                if (militaryTime) {
                    remoteView.setTextViewText(R.id.task_row_time, new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date));
                } else {
                    remoteView.setTextViewText(R.id.task_row_time, new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date));
                }

                // set date
                if (now.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    remoteView.setTextViewText(R.id.task_row_date, this.context.getString(R.string.task_row_today));
                else if (tomorrow.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && tomorrow.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    remoteView.setTextViewText(R.id.task_row_date, this.context.getString(R.string.task_row_tomorrow));
                else if (now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    remoteView.setTextViewText(R.id.task_row_date, new SimpleDateFormat("MMM dd", Locale.getDefault()).format(date));
                else
                    remoteView.setTextViewText(R.id.task_row_date, new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date));
            }
        }

        // make row click open details
        Intent intent = new Intent();
        intent.putExtra("id", curTask.getId());
        remoteView.setOnClickFillInIntent(R.id.task_row, intent);

        return remoteView;
    }

}
