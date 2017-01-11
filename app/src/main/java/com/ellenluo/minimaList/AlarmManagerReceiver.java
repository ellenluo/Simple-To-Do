package com.ellenluo.minimaList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmManagerReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        long id = intent.getExtras().getLong("id");
        String text = intent.getExtras().getString("text");

        // start notification service
        Intent service = new Intent(context, NotifService.class);
        service.putExtra("id", id);
        service.putExtra("text", text);
        context.startService(service);
    }

}