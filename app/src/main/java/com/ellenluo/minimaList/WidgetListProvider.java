package com.ellenluo.minimaList;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Task> taskList;
    private Context context = null;

    DBHandler db;
    SharedPreferences pref;

    String list;

    public WidgetListProvider(Context context, Intent intent) {
        this.context = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        pref = context.getSharedPreferences(String.valueOf(appWidgetId), 0);
        list = pref.getString("widget_list", "All Tasks");
        db = new DBHandler(context);

        if (list.equals("All Tasks")) {
            taskList = db.getAllTasks();
        } else {
            taskList = db.getTasksFromList(db.getList(list).getId());
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        String newList = pref.getString("widget_list", "All Tasks");

        if (newList.equals("All Tasks")) {
            taskList = db.getAllTasks();

            // update widget title
            Intent intent = new Intent(this.context, WidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(this.context).getAppWidgetIds(new ComponentName(this.context, WidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            this.context.sendBroadcast(intent);
            Log.w("WidgetListProvider", "All tasks called");
        } else if (!newList.equals(list)) {
            taskList = db.getTasksFromList(db.getList(newList).getId());

            // update widget title
            Intent intent = new Intent(this.context, WidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(this.context).getAppWidgetIds(new ComponentName(this.context, WidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            this.context.sendBroadcast(intent);
            Log.w("WidgetListProvider", "Name change called");
        } else {
            taskList = db.getTasksFromList(db.getList(list).getId());
            Log.w("WidgetListProvider", "Custom list called");
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onDestroy() {
        // no-op
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
        return taskList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.task_row);
        Task task = taskList.get(position);

        // get settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean militaryTime = prefSettings.getBoolean("24h", false);
        int color = prefSettings.getInt("theme_color", 4149685);

        // set name and list
        if (task.getList() != -1) {
            remoteView.setTextViewText(R.id.task_row_list, db.getList(task.getList()).getName());
        } else {
            remoteView.setTextViewText(R.id.task_row_list, "No List");
        }

        remoteView.setTextViewText(R.id.task_row_name, task.getName());
        remoteView.setTextViewTextSize(R.id.task_row_name, TypedValue.COMPLEX_UNIT_SP, 14);
        remoteView.setTextViewTextSize(R.id.task_row_time, TypedValue.COMPLEX_UNIT_SP, 12);
        remoteView.setTextViewTextSize(R.id.task_row_date, TypedValue.COMPLEX_UNIT_SP, 12);
        remoteView.setTextColor(R.id.task_row_name, Color.parseColor("#757575"));
        remoteView.setTextColor(R.id.task_row_time, Color.parseColor("#757575"));
        remoteView.setTextColor(R.id.task_row_list, color);
        remoteView.setTextColor(R.id.task_row_date, color);

        // set date & time
        long millis = task.getDue();
        if (millis != -1) {
            // get calendars
            Calendar now = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);
            Calendar due = Calendar.getInstance();
            due.setTimeInMillis(millis);

            // get date
            Date date = due.getTime();

            // set text
            if (due.before(now))
                remoteView.setTextViewText(R.id.task_row_date, "Overdue");
            else {
                if (militaryTime) {
                    remoteView.setTextViewText(R.id.task_row_time, new SimpleDateFormat("HH:mm").format(date));
                } else {
                    remoteView.setTextViewText(R.id.task_row_time, new SimpleDateFormat("hh:mm a").format(date));
                }

                if (now.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    remoteView.setTextViewText(R.id.task_row_date, "Today");
                else if (tomorrow.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR) && tomorrow.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    remoteView.setTextViewText(R.id.task_row_date, "Tomorrow");
                else if (now.get(Calendar.YEAR) == due.get(Calendar.YEAR))
                    remoteView.setTextViewText(R.id.task_row_date, new SimpleDateFormat("MMM dd").format(date));
                else
                    remoteView.setTextViewText(R.id.task_row_date, new SimpleDateFormat("MMM dd, yyyy").format(date));
            }
        }

        // make clickable
        Intent intent = new Intent();
        intent.putExtra("id", task.getId());
        remoteView.setOnClickFillInIntent(R.id.task_row, intent);

        Log.w("WidgetListProvider", "row created");
        return remoteView;
    }

}
