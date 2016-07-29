package com.ellenluo.simpleto_do;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmManagerReceiver extends BroadcastReceiver {

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

    public void setAlarm(Context context, long millis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC, millis, pendingIntent);
    }

    /*public void setRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
    }*/

}