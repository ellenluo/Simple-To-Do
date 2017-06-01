package com.ellenluo.minimaList;

/**
 * BootCompletedReceiver
 * Created by Ellen Luo
 * BroadcastReceiver that resets all reminders after device reboot (powering off device cancels notifications).
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

        // schedules widget update at midnight
        Helper h = new Helper(context);
        h.scheduleUpdate();
    }

}
