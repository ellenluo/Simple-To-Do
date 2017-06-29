package com.ellenluo.minimaList;

/**
 * SettingsFragment
 * Created by Ellen Luo
 * PreferenceFragment that allows users to set display, theme and notification settings.
 */

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment {

    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        // delete all button
        Preference prefDelete = findPreference("delete_all");
        prefDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // display confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.dialog_delete_all_confirmation));

                // delete button
                builder.setNegativeButton(getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        SharedPreferences pref = getActivity().getSharedPreferences("Main", PREFERENCE_MODE_PRIVATE);
                        DBHandler db = new DBHandler(getActivity());
                        ArrayList<Task> taskList = db.getAllTasks();

                        // cancel all alarms
                        Helper h = new Helper(getActivity());

                        for (int i = 0; i < taskList.size(); i++) {
                            h.cancelReminder(taskList.get(i).getId());
                        }

                        // delete all lists & tasks
                        db.deleteAll();

                        // update widgets
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(),
                                WidgetProvider.class));

                        for (int appWidgetId : appWidgetIds) {
                            SharedPreferences widgetPref = getActivity().getSharedPreferences(
                                    String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
                            widgetPref.edit().putString("widget_list", "All Tasks").apply();
                        }

                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list);

                        // reset activity
                        pref.edit().putString("current_list", "All Tasks").apply();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                // cancel button
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // show dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    // set background
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setBackgroundColor(Color.WHITE);
    }

}
