package com.ellenluo.minimaList;

/**
 * MainFragment
 * Created by Ellen Luo
 * Fragment that displays a list of either all tasks, or the tasks in a particular list.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
                final Task task = taskList.get(pos);
                final TextView tvName = (TextView) v.findViewById(R.id.task_row_name);
                tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                // undo snackbar
                CharSequence text = String.format("%s '%s'?", getString(R.string.deleting_task), task.getName());
                Snackbar.make(v, text, Snackbar.LENGTH_LONG)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                switch (event) {
                                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                                        // undo
                                        taskAdapter.notifyDataSetChanged();
                                        tvName.setPaintFlags(tvName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                                        break;
                                    default:
                                        // cancel any reminders
                                        final Helper h = new Helper(getActivity());
                                        h.cancelReminder(task.getId());

                                        // update database and list adapter
                                        db.deleteTask(taskList.remove(pos));
                                        taskAdapter.notifyDataSetChanged();
                                        tvName.setPaintFlags(tvName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                                        // update widgets
                                        h.updateWidgets();

                                        String message = String.format("'%s' %s", task.getName(), getString(R.string.delete_task_confirmation));
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        })
                        .setAction(getString(R.string.undo_delete), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // do nothing
                            }
                        })
                        .show();

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
