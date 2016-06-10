package com.ellenluo.simpleto_do;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    DBHandler db = new DBHandler(this);
    SharedPreferences pref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        // set up navigation menu
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        refreshLists();

        // set up add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        // set main fragment
        Fragment fragment = null;

        try {
            fragment = (Fragment) FragmentMain.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
    }

    // refresh navigation menu of lists
    public void refreshLists() {
        ArrayList<List> listList = db.getAllLists();
        ArrayList<String> listName = new ArrayList<String>();
        Menu menu = nvDrawer.getMenu();

        for (int i = 0; i < listList.size(); i++) {
            listName.add(i, listList.get(i).getName());
        }

        Collections.sort(listName, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < listList.size(); i++) {
            menu.add(R.id.group_lists, Menu.NONE, Menu.NONE, listName.get(i));
        }
    }

    // set up navigation drawer
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    // change fragments if item in drawer is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;

        if (menuItem.getItemId() != 0) {
            switch (menuItem.getItemId()) {
                case R.id.nav_all:
                    fragmentClass = FragmentMain.class;
                    pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
                    pref.edit().putString("current_list", "all_tasks").apply();
                    break;
                case R.id.nav_about:
                    break;
                case R.id.nav_backup:
                    break;
                default:
                    break;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

            setTitle(menuItem.getTitle());
            mDrawer.closeDrawers();
        } else {
            try {
                fragment = (Fragment) FragmentMain.class.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

            // set list
            pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
            pref.edit().putString("current_list", menuItem.getTitle().toString()).apply();

            setTitle(menuItem.getTitle());
            mDrawer.closeDrawers();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    // set up drawer toggle
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    // find height of status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // change back to viewing all tasks
    @Override
    protected void onStop() {
        pref = getSharedPreferences("Settings", PREFERENCE_MODE_PRIVATE);
        pref.edit().putString("current_list", "all_tasks").apply();
        super.onStop();
    }
}

