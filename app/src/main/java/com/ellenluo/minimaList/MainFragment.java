package com.ellenluo.minimaList;

/*
 * MainFragment
 * Created by Ellen Luo
 * Fragment that displays a list of either all tasks, or the tasks in a particular list.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainFragment extends Fragment {

    private DBHandler db;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    private ArrayList<Task> taskList;
    private Handler handler = new Handler();
    private View v;
    private ImageView ivEmpty;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);

        if (container != null) {
            container.removeAllViews();
        }

        // set up task list
        db = new DBHandler(getActivity());
        ListView lvTasks = (ListView) v.findViewById(R.id.task_list);
        SharedPreferences pref = getActivity().getSharedPreferences("Main", PREFERENCE_MODE_PRIVATE);

        // set list to view
        if (pref.getString("current_list", "All Tasks").equals("All Tasks")) {
            taskList = db.getAllTasks();
        } else {
            List curList = db.getList(pref.getString("current_list", "All Tasks"));
            taskList = db.getTasksFromList(curList.getId());
        }

        db.close();

        // set up empty view
        ivEmpty = (ImageView) v.findViewById(R.id.empty_list);
        lvTasks.setEmptyView(ivEmpty);

        // set custom list adapter
        final TaskListAdapter taskAdapter = new TaskListAdapter(getActivity(), taskList);
        lvTasks.setAdapter(taskAdapter);

        // show details when a task is clicked
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Task curTask = taskList.get(position);
                Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
                intent.putExtra("id", curTask.getId());
                startActivity(intent);
                getActivity().finish();
            }
        });

        // delete task when a task is long clicked
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, final int pos, long id) {
                Task task = taskList.get(pos);
                final TextView tvName = (TextView) v.findViewById(R.id.task_row_name);
                tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Toast.makeText(getActivity(), "'" + task.getName() + "' " + getString(R.string.delete_task_confirmation), Toast.LENGTH_SHORT).show();

                // cancel any reminders
                final Helper h = new Helper(getActivity());
                h.cancelReminder(task.getId());

                // delay deletion
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // update database and list adapter
                        db.deleteTask(taskList.remove(pos));
                        taskAdapter.notifyDataSetChanged();
                        tvName.setPaintFlags(0);

                        // update widgets
                        h.updateWidgets();
                    }
                }, 500);

                return true;
            }
        });

        return v;
    }

    // clear image drawable
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = null;
        ivEmpty.setImageDrawable(null);
    }

}
