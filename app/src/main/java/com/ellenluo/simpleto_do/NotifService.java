package com.ellenluo.simpleto_do;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class NotifService extends Service {

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);

        Notification notifBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(pref.getString("notif_name", "error"))
                .setContentText(pref.getString("notif_details", "error"))
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, notifBuilder);
    }

}
