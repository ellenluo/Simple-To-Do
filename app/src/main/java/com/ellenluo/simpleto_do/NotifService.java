package com.ellenluo.simpleto_do;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotifService extends IntentService {

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    public NotifService() {
        super(NotifService.class.getName());
    }

    @Override
    public void onHandleIntent(Intent intent) {
        long id = intent.getExtras().getLong("id");
        String text = intent.getExtras().getString("text");
        Log.d("NotifService", "id is " + id + ", text is " + text);

        pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        pref.edit().putLong("id", id).apply();

        Intent newIntent = new Intent(this, TaskDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Lists")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 1000})
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(Color.rgb(0, 191, 255), 2000, 3000);

        Notification notif = builder.build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notif);
    }

}
