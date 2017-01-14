package com.ellenluo.minimaList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent incomingIntent) {
        // get task list
        DBHandler db = new DBHandler(context);
        ArrayList<Task> taskList = db.getAllTasks();

        // set new reminders
        for (int i = 0; i < taskList.size(); i++) {
            Task curTask = taskList.get(i);

            if (curTask.getRemind() != -1 && curTask.getRemind() > System.currentTimeMillis()) {
                Helper h = new Helper(context);
                h.setReminder(curTask.getName(), curTask.getId(), curTask.getRemind());
            }
        }
    }

}
