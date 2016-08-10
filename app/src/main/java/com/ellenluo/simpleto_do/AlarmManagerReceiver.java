package com.ellenluo.simpleto_do;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;

public class AlarmManagerReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmManagerReceiver", "received");
        long id = intent.getExtras().getLong("id");
        String text = intent.getExtras().getString("text");
        Log.d("AlarmManagerReceiver", "id is " + id + ", text is " + text);

        Intent service = new Intent(context, NotifService.class);
        service.putExtra("id", id);
        service.putExtra("text", text);
        context.startService(service);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmManagerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void setReminders(Context context) {
        Log.d("AlarmManagerReceiver", "Reminders are being set");
        // cancel existing reminders
        Intent oldIntent = new Intent(context, NotifService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, oldIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        // set new reminders
        DBHandler db = new DBHandler(context);
        ArrayList<Task> taskList = db.getAllTasks();

        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getRemind() != -1 && taskList.get(i).getRemind() > System.currentTimeMillis()) {
                Intent intent = new Intent(context, AlarmManagerReceiver.class);
                intent.putExtra("text", taskList.get(i).getName());
                intent.putExtra("id", taskList.get(i).getId());
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC, taskList.get(i).getRemind(), pendingIntent);
                Log.d("AlarmManagerReceiver", "Reminder for task '" + taskList.get(i).getName() + "' has been set");
                Log.d("AlarmManagerReceiver", "i = " + i + ", text is " + taskList.get(i).getName() + ", alarm set for " + taskList.get(i).getRemind());
            }
        }
    }

}