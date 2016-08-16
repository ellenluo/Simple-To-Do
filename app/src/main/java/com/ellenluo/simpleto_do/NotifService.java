package com.ellenluo.simpleto_do;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotifService extends IntentService {

    private static final int PREFERENCE_MODE_PRIVATE = 0;

    public NotifService() {
        super(NotifService.class.getName());
    }

    @Override
    public void onHandleIntent(Intent intent) {
        long id = intent.getExtras().getLong("id");
        String text = intent.getExtras().getString("text");
        Log.d("NotifService", "id is " + id + ", text is " + text + ", time is " + System.currentTimeMillis());

        // get notification settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        String sound = prefSettings.getString("sound", "DEFAULT_NOTIFICATION_URI");
        boolean vibration = prefSettings.getBoolean("vibration", true);
        boolean light = prefSettings.getBoolean("light", true);

        // set up notification
        Intent newIntent = new Intent(this, TaskDetailsActivity.class);
        newIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Lists")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (vibration) {
            builder.setVibrate(new long[]{0, 1000});
        }

        if (light) {
            builder.setLights(Color.rgb(0, 191, 255), 2000, 2000);
        }

        if (sound.length() > 0) {
            Uri notifUri = Uri.parse(sound);
            builder.setSound(notifUri);
        }

        Notification notif = builder.build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) id, notif);

        AlarmManagerReceiver.completeWakefulIntent(intent);
    }

}
