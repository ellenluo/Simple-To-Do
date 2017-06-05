package com.ellenluo.minimaList;

/**
 * MainActivity
 * Created by Ellen Luo
 * Activity that switches between fragments using a side navigation drawer and allows adding/editing lists.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHandler db;
    private SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton fab;
    private EditText etListName;

    private ArrayList<List> listList;
    private boolean showEditList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show intro if first time
        pref = getSharedPreferences("Main", PREFERENCE_MODE_PRIVATE);
        if (true){//(pref.getBoolean("show_intro", true)) {
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
            pref.edit().putBoolean("show_intro", false).apply();
            MainActivity.this.finish();
        }

        // Google analytics
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);

        // set theme
        Helper h = new Helper(this);
        h.setTheme();

        setContentView(R.layout.activity_main);

        // set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setPadding(0, h.getStatusBarHeight(), 0, 0);
        }

        // set up navigation menu
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        db = new DBHandler(this);
        refreshLists();

        // set up floating add button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        // set main fragment
        String curList = pref.getString("current_list", "All Tasks");

        // check for extras (if launched from widget)
        String newList = getIntent().getStringExtra("current_list");

        // check for faulty value
        if (newList != null) {
            boolean exists = false;
            for (int i = 0; i < listList.size(); i++) {
                if (newList.equals(listList.get(i).getName())) {
                    curList = newList;
                    pref.edit().putString("current_list", newList).apply();
                    break;
                }
            }
        }

        // set main fragment
        Fragment fragment = null;

        try {
            fragment = MainFragment.class.newInstance();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
        setTitle(curList);
    }

    // refresh navigation menu of lists
    private void refreshLists() {
        listList = db.getAllLists();
        Menu menu = nvDrawer.getMenu();

        for (int i = 0; i < listList.size(); i++) {
            menu.add(R.id.group_lists, Menu.NONE, Menu.NONE, listList.get(i).getName());
        }
    }

    // when add list button is clicked
    public void addList(View view) {
        showAddDialog();
    }

    // set up navigation drawer
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    // set up menu selection options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // check if edit list icon selected
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            showEditDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    // when item selected from navigation drawer
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;

        // if default item selected
        if (menuItem.getItemId() != 0) {
            switch (menuItem.getItemId()) {
                case R.id.nav_all:
                    fragmentClass = MainFragment.class;
                    pref.edit().putString("current_list", "All Tasks").apply();
                    fab.setVisibility(View.VISIBLE);
                    showEditList = false;
                    break;
                case R.id.nav_settings:
                    showEditList = false;
                    fab.setVisibility(View.GONE);

                    // set settings fragment directly
                    getFragmentManager().beginTransaction()
                            .replace(R.id.frame, new SettingsFragment())
                            .commit();
                    setTitle(menuItem.getTitle());
                    mDrawer.closeDrawers();
                    supportInvalidateOptionsMenu();
                    break;
                case R.id.nav_feedback:
                    fragmentClass = FeedbackFragment.class;
                    fab.setVisibility(View.GONE);
                    showEditList = false;
                    break;
            }

            // set fragment
            if (fragmentClass != null) {
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

                setTitle(menuItem.getTitle());
                mDrawer.closeDrawers();
                supportInvalidateOptionsMenu();
            }
        }
        // if custom list selected
        else {
            try {
                fragment = MainFragment.class.newInstance();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

            // set list
            pref.edit().putString("current_list", menuItem.getTitle().toString()).apply();
            showEditList = true;

            setTitle(menuItem.getTitle());
            mDrawer.closeDrawers();
            supportInvalidateOptionsMenu();
        }
    }

    // set up drawer toggle
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    // set up drawer toggle
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    // set up drawer toggle
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    // inflate action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pref.getString("current_list", "All Tasks").equals("All Tasks") || !showEditList) {
            return true;
        }

        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return true;
    }

    // edit list dialog
    public void showEditDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_list);
        setDialogWidth(dialog);

        // set text field to current name
        etListName = (EditText) dialog.findViewById(R.id.list_name);
        etListName.setText(pref.getString("current_list", "All Tasks"));

        // save button
        Button btnSave = (Button) dialog.findViewById(R.id.save_changes);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listName = etListName.getText().toString().trim();

                // check for empty list name
                if (listName.length() == 0) {
                    dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.dialog_empty_list));

                    builder.setPositiveButton(getString(R.string.dialog_confirmation), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showEditDialog();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                // check for duplicate list name
                for (int i = 0; i < listList.size(); i++) {
                    if (listName.equals(listList.get(i).getName())) {
                        dialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(getString(R.string.dialog_duplicate_list));

                        builder.setPositiveButton(getString(R.string.dialog_confirmation), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                showEditDialog();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return;
                    }
                }

                // check for "All Tasks" name
                if (listName.equals("All Tasks")) {
                    dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.dialog_duplicate_list));

                    builder.setPositiveButton(getString(R.string.dialog_confirmation), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showEditDialog();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                // update list
                List curList = db.getList(pref.getString("current_list", "error"));
                String oldListName = curList.getName();
                curList.setName(listName);
                db.updateList(curList);

                pref.edit().putString("current_list", listName).apply();

                // update widgets
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), WidgetProvider.class));

                for (int appWidgetId : appWidgetIds) {
                    SharedPreferences widgetPref = getSharedPreferences(String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
                    if (widgetPref.getString("widget_list", "All Tasks").equals(oldListName)) {
                        widgetPref.edit().putString("widget_list", curList.getName()).apply();
                    }
                }

                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list);

                // reset activity
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                MainActivity.this.finish();
            }
        });

        // delete button
        Button btnDelete = (Button) dialog.findViewById(R.id.delete_list);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                // display confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.dialog_delete_confirmation));

                // delete button
                builder.setNegativeButton(getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        List curList = db.getList(pref.getString("current_list", "error"));
                        String oldListName = curList.getName();
                        ArrayList<Task> taskList = db.getTasksFromList(curList.getId());

                        // cancel all reminders
                        Helper h = new Helper(getApplicationContext());

                        for (int i = 0; i < taskList.size(); i++) {
                            h.cancelReminder(taskList.get(i).getId());
                        }

                        // delete list & tasks
                        db.deleteList(curList);
                        db.deleteTasksFromList(curList.getId());

                        // update widgets
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), WidgetProvider.class));

                        for (int appWidgetId : appWidgetIds) {
                            SharedPreferences widgetPref = getSharedPreferences(String.valueOf(appWidgetId), PREFERENCE_MODE_PRIVATE);
                            if (widgetPref.getString("widget_list", "All Tasks").equals(oldListName)) {
                                widgetPref.edit().putString("widget_list", "All Tasks").apply();
                            }
                        }

                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list);

                        // reset activity
                        pref.edit().putString("current_list", "All Tasks").apply();
                        Intent intent = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }
                });

                // cancel button
                builder.setPositiveButton(getString(R.string.dialog_delete_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        showEditDialog();
                    }
                });

                AlertDialog warning = builder.create();
                warning.show();
            }
        });

        dialog.show();
    }

    // add list dialog
    public void showAddDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_list);
        setDialogWidth(dialog);

        // initialize text field
        etListName = (EditText) dialog.findViewById(R.id.list_name);

        // add button
        Button btnAdd = (Button) dialog.findViewById(R.id.add_list);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listName = etListName.getText().toString().trim();

                // check for empty list name
                if (listName.length() == 0) {
                    dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.dialog_empty_list));

                    builder.setPositiveButton(getString(R.string.dialog_confirmation), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showAddDialog();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                // check for duplicate list name
                for (int i = 0; i < listList.size(); i++) {
                    if (listName.equals(listList.get(i).getName())) {
                        dialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.dialog_duplicate_list);

                        builder.setPositiveButton(R.string.dialog_confirmation, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                showAddDialog();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return;
                    }
                }

                // check for "All Tasks" name
                if (listName.equals("All Tasks")) {
                    dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.dialog_duplicate_list);

                    builder.setPositiveButton(R.string.dialog_confirmation, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            showAddDialog();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                List newList = new List(listName);
                db.addList(newList);

                // reset activity
                pref.edit().putString("current_list", newList.getName()).apply();
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                MainActivity.this.finish();
            }
        });

        // cancel button
        Button btnCancel = (Button) dialog.findViewById(R.id.cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    // set dialog width
    @SuppressWarnings("ConstantConditions")
    private void setDialogWidth(Dialog dialog) {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = width;
        dialog.getWindow().setAttributes(lp);
    }

    // make back button return to all tasks
    @Override
    public void onBackPressed() {
        // check if currently showing all tasks
        if (!pref.getString("current_list", "All Tasks").equals("All Tasks")) {
            Fragment fragment = null;

            try {
                fragment = MainFragment.class.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

            pref.edit().putString("current_list", "All Tasks").apply();
            setTitle("All Tasks");
        }
    }

    // close database
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

}

