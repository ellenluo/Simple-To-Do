package com.ellenluo.simpleto_do;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;


public class FragmentMain extends Fragment {

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
        taskList = db.getAllTasks();

        final TaskListAdapter taskAdapter = new TaskListAdapter(getActivity(), taskList);
        lvTasks.setAdapter(taskAdapter);

        // show details when a task is clicked
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Task curTask = taskList.get(position);

                pref = getActivity().getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
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
