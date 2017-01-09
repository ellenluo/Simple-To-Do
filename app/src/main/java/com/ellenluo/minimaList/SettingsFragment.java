package com.ellenluo.minimaList;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment {

    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        // delete all button
        Preference prefDelete = (Preference) findPreference("delete_all");
        prefDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // display confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete all tasks and lists? This cannot be undone.");

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        SharedPreferences pref = getActivity().getSharedPreferences("Main", PREFERENCE_MODE_PRIVATE);
                        DBHandler db = new DBHandler(getActivity());
                        ArrayList<Task> taskList = db.getAllTasks();

                        // cancel all alarms
                        for (int i = 0; i < taskList.size(); i++) {
                            Intent alarmIntent = new Intent(getActivity(), AlarmManagerReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) taskList.get(i).getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                        }

                        // delete all lists & tasks
                        db.deleteAll();

                        // update widgets
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), WidgetProvider.class));

                        for (int appWidgetId : appWidgetIds) {
                            SharedPreferences widgetPref = getActivity().getSharedPreferences(String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
                            widgetPref.edit().putString("widget_list", "All Tasks").apply();
                        }

                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list);
                        Log.d("SettingsFragment", "Widgets successfully updated");

                        // reset activity
                        pref.edit().putString("current_list", "All Tasks").apply();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setBackgroundColor(Color.WHITE);
        getView().setClickable(true);
    }

}
