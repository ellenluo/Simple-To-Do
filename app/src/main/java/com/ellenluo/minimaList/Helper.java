package com.ellenluo.minimaList;

/*
 * Helper
 * Created by Ellen Luo
 * Helper class that contains methods used by multiple classes.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.Spinner;

import java.util.Calendar;

class Helper {

    private Context context;
    private static final String ACTION_SCHEDULED_UPDATE = "scheduled_update";

    Helper(Context context) {
        this.context = context;
    }

    // find height of status bar
    int getStatusBarHeight() {
        int result = 0;
        int resourceId = this.context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // display alert confirmation message
    void displayAlert(String message, String posMsg, String negMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(message);

        builder.setNegativeButton(negMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        });

        builder.setPositiveButton(posMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // set reminder
    void setReminder(String name, long id, long remindMillis) {
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.context, AlarmManagerReceiver.class);
        intent.putExtra("text", name);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, remindMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, remindMillis, pendingIntent);
        }
    }

    // cancel reminder
    void cancelReminder(long id) {
        Intent intent = new Intent(this.context, AlarmManagerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // update all widgets
    void updateWidgets() {
        // update widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this.context, WidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list);
    }

    // set style used for activities (based on user settings)
    void setTheme() {
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this.context);
        int color = prefSettings.getInt("theme_color", ContextCompat.getColor(this.context, R.color.dark_blue));

        if (color == ContextCompat.getColor(this.context, R.color.red)) {
            this.context.setTheme(R.style.RedTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.pink)) {
            this.context.setTheme(R.style.PinkTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.purple)) {
            this.context.setTheme(R.style.PurpleTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.deep_purple)) {
            this.context.setTheme(R.style.DeepPurpleTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.blue_grey)) {
            this.context.setTheme(R.style.BlueGreyTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.light_blue)) {
            this.context.setTheme(R.style.LightBlueTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.aqua)) {
            this.context.setTheme(R.style.AquaTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.green)) {
            this.context.setTheme(R.style.GreenTheme);
        } else if (color == ContextCompat.getColor(this.context, R.color.orange)) {
            this.context.setTheme(R.style.OrangeTheme);
        } else {
            this.context.setTheme(R.style.AppTheme);
        }
    }

    // get index of item in spinner
    int getIndex(String item, Spinner listSpinner) {
        int index = 0;

        for (int i = 0; i < listSpinner.getCount(); i++) {
            if (listSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                index = i;
                break;
            }
        }
        return index;
    }

    // schedules widget update at midnight
    void scheduleUpdate() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(ACTION_SCHEDULED_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // get calendar instance for midnight tomorrow
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 1);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_YEAR, 1);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
        }
    }

}
