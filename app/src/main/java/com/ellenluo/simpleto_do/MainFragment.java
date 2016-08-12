package com.ellenluo.simpleto_do;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainFragment extends Fragment {

    ArrayList<Task> taskList;
    DBHandler db;
    private Handler handler = new Handler();

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // set up task list
        db = new DBHandler(getActivity());
        ListView lvTasks = (ListView) v.findViewById(R.id.task_list);
        pref = getActivity().getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);

        // set list to view
        if (pref.getString("current_list", "All Tasks").equals("All Tasks")) {
            taskList = db.getAllTasks();
        } else {
            List curList = db.getList(pref.getString("current_list", "All Tasks"));
            taskList = db.getTasksFromList(curList.getId());
        }

        // set up empty view
        TextView tvEmpty = (TextView) v.findViewById(R.id.empty_list);
        lvTasks.setEmptyView(tvEmpty);

        final TaskListAdapter taskAdapter = new TaskListAdapter(getActivity(), taskList);
        lvTasks.setAdapter(taskAdapter);

        // show details when a task is clicked
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Task curTask = taskList.get(position);
                pref.edit().putLong("id", curTask.getId()).apply();

                Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
                startActivity(intent);
            }
        });

        // delete task when a task is long clicked
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, final int pos, long id) {
                TextView tvName = (TextView) v.findViewById(R.id.task_row_name);
                tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Toast.makeText(getActivity(), "'" + taskList.get(pos).getName() + "' successfully removed", Toast.LENGTH_SHORT).show();

                // cancel any reminders
                Intent intent = new Intent(getActivity(), AlarmManagerReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) taskList.get(pos).getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                // delay deletion
                handler.postDelayed(new Runnable() {
                    public void run() {
                        db.deleteTask(taskList.remove(pos));
                        taskAdapter.notifyDataSetChanged();
                    }
                }, 500);

                return true;
            }
        });

        return v;
    }

}
