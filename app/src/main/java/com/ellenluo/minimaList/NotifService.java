package com.ellenluo.minimaList;

/**
 * NotifService
 * Created by Ellen Luo
 * IntentService that creates a notification reminder with optional vibration, LED light and sound settings.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Calendar;

public class NotifService extends IntentService {

    public NotifService() {
        super(NotifService.class.getName());
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // get extras
        long id = intent.getExtras().getLong("id");
        String text = intent.getExtras().getString("text");

        // get notification settings from preferences
        SharedPreferences prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        String sound = prefSettings.getString("sound", "DEFAULT_NOTIFICATION_URI");
        boolean vibration = prefSettings.getBoolean("vibration", true);
        boolean light = prefSettings.getBoolean("light", true);

        // open details when notification clicked
        Intent newIntent = new Intent(this, TaskDetailsActivity.class);
        newIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, PendingIntent.FLAG_ONE_SHOT);

        // set up notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(text)
                .setContentText("Tap for more details")
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // vibration option
        if (vibration) {
            builder.setVibrate(new long[]{0, 1000});
        }

        // LED light option
        if (light) {
            builder.setLights(ContextCompat.getColor(this, R.color.colorAccent), 2000, 1000);
        }

        // sound option
        if (sound.length() > 0) {
            Uri notifUri = Uri.parse(sound);
            builder.setSound(notifUri);
        }

        // set notification color
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        // send notification
        Notification notif = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) id, notif);
        Log.d("NotifService", "notification sent at " + Calendar.getInstance().getTimeInMillis());
        Log.d("NotifService", "task id is " + id);

        // set next reminder (if recurring)
        DBHandler db = new DBHandler(this);
        Task curTask = db.getTask(id);
        int option = (int) curTask.getRepeat();
        Helper h = new Helper(this);

        if (option > 0) {
            long millis = curTask.getNextRemind();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(millis);

            switch (option) {
                case 1: {
                    // daily
                    cal.add(Calendar.DATE, 1);
                    long nextMillis = cal.getTimeInMillis();
                    curTask.setNextRemind(nextMillis);
                    db.updateTask(curTask);
                    Log.d("NotifService", "setting reminder for " + nextMillis);
                    Log.d("NotifService", "new task id is " + curTask.getId());
                    h.setReminder(curTask.getName(), curTask.getId(), nextMillis);
                    break;
                }
                case 2: {
                    // weekly
                    cal.add(Calendar.DATE, 7);
                    long nextMillis = cal.getTimeInMillis();
                    curTask.setNextRemind(nextMillis);
                    db.updateTask(curTask);
                    h.setReminder(curTask.getName(), curTask.getId(), nextMillis);
                    break;
                }
                case 3: {
                    // monthly
                    cal.add(Calendar.MONTH, 1);
                    long nextMillis = cal.getTimeInMillis();
                    curTask.setNextRemind(nextMillis);
                    db.updateTask(curTask);
                    h.setReminder(curTask.getName(), curTask.getId(), nextMillis);
                    break;
                }
            }
        }

        db.close();
        AlarmManagerReceiver.completeWakefulIntent(intent);
    }

}
