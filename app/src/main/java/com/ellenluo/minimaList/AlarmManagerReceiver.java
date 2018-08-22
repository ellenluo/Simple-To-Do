package com.ellenluo.minimaList;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * AlarmManagerReceiver
 * Created by Ellen Luo
 * BroadcastReceiver that calls a notification service when a task reminder fires.
 */
public class AlarmManagerReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        long id = intent.getExtras().getLong("id");
        String text = intent.getExtras().getString("text");

        // start notification service
        Intent service = new Intent(context, NotificationService.class);
        service.putExtra("id", id);
        service.putExtra("text", text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }
    }

}