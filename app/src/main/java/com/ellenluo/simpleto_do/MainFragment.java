package com.ellenluo.simpleto_do;

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

    static ArrayList<Task> taskList;
    DBHandler db;
    String curList;
    private Handler handler = new Handler();

    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // set up task list
        db = new DBHandler(getActivity());
        ListView lvTasks = (ListView) v.findViewById(R.id.task_list);
        pref = getActivity().getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        curList = pref.getString("current_list", "All Tasks");

        // set list to view
        if (curList.equals("All Tasks"))
            taskList = db.getAllTasks();
        else
            taskList = db.getTasksFromList(curList);

        //sortByDate();

        final TaskListAdapter taskAdapter = new TaskListAdapter(getActivity(), taskList);
        lvTasks.setAdapter(taskAdapter);

        // show details when a task is clicked
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Task curTask = taskList.get(position);
                pref.edit().putInt("id", curTask.getId()).apply();

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

                // delay deletion
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (db.getTasksFromList(taskList.get(pos).getList()).size() == 1) {
                            pref.edit().putString("current_list", "All Tasks").apply();
                            db.deleteList(db.getList(taskList.get(pos).getList()));
                            db.deleteTask(taskList.remove(pos));
                            taskAdapter.notifyDataSetChanged();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            db.deleteTask(taskList.remove(pos));
                            taskAdapter.notifyDataSetChanged();
                        }
                    }
                }, 500);

                return true;
            }
        });

        return v;
    }

    // sort tasks by due date
    /*private static void sortByDate() {
        Collections.sort(taskList, new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                if (t2.getDue() == -1)
                    return -1;
                else if (t1.getDue() > t2.getDue() || t1.getDue() == -1)
                    return 1;
                else if (t1.getDue() < t2.getDue())
                    return -1;
                return 0;
            }
        });
    }*/
}
