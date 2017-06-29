package com.ellenluo.minimaList;

/**
 * DBHandler
 * Created by Ellen Luo
 * SQLiteOpenHelper that creates SQLite tables to store tasks and lists and allows various database operations.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_tasks";

    // tables
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_LISTS = "lists";

    // column names
    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_TASK_NAME = "task_name";
    private static final String KEY_TASK_DETAILS = "task_details";
    private static final String KEY_TASK_DUE = "task_due";
    private static final String KEY_TASK_REMIND = "task_remind";
    private static final String KEY_TASK_NEXT_REMIND = "task_next_remind";
    private static final String KEY_TASK_REPEAT = "task_repeat";
    private static final String KEY_TASK_LIST = "task_list";
    private static final String KEY_LIST_ID = "list_id";
    private static final String KEY_LIST_NAME = "list_name";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create task and list tables
        String CREATE_TASK_TABLE = String.format(Locale.US, "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s " +
                        "TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)", TABLE_TASKS, KEY_TASK_ID,
                KEY_TASK_NAME, KEY_TASK_DETAILS, KEY_TASK_DUE, KEY_TASK_REMIND, KEY_TASK_REPEAT,
                KEY_TASK_NEXT_REMIND, KEY_TASK_LIST);
        String CREATE_LIST_TABLE = String.format(Locale.US, "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT)",
                TABLE_LISTS, KEY_LIST_ID, KEY_LIST_NAME);

        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }

    // add task to table
    void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TASK_NAME, task.getName());
        values.put(KEY_TASK_DETAILS, task.getDetails());
        values.put(KEY_TASK_DUE, task.getDue());
        values.put(KEY_TASK_REMIND, task.getRemind());
        values.put(KEY_TASK_REPEAT, task.getRepeat());
        values.put(KEY_TASK_NEXT_REMIND, task.getNextRemind());
        values.put(KEY_TASK_LIST, task.getList());

        db.insert(TABLE_TASKS, null, values);

        // set id
        long id = -1;
        String selectQuery = "SELECT " + KEY_TASK_ID + " FROM " + TABLE_TASKS + " ORDER BY " + KEY_TASK_ID +
                " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        task.setId(id);

        cursor.close();
        db.close();
    }

    // get task from table
    Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_TASK_ID, KEY_TASK_NAME, KEY_TASK_DETAILS,
                KEY_TASK_DUE, KEY_TASK_REMIND, KEY_TASK_REPEAT, KEY_TASK_NEXT_REMIND, KEY_TASK_LIST}, KEY_TASK_ID
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            Task task = new Task(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                    Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)),
                    Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6)),
                    Long.parseLong(cursor.getString(7)));
            cursor.close();
            db.close();
            return task;
        }

        cursor.close();
        db.close();
        return null;
    }

    // get all tasks from table
    ArrayList<Task> getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();

        // select all tasks with due dates first
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " WHERE " + KEY_TASK_DUE + " != -1 ORDER BY " +
                KEY_TASK_DUE + ", " + KEY_TASK_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Task> taskList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)),
                        Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6)),
                        Long.parseLong(cursor.getString(7)));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // select all tasks without due dates
        selectQuery = "SELECT * FROM " + TABLE_TASKS + " WHERE " + KEY_TASK_DUE + " = -1 ORDER BY " + KEY_TASK_NAME;
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)),
                        Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6)),
                        Long.parseLong(cursor.getString(7)));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    // update task in table
    int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TASK_NAME, task.getName());
        values.put(KEY_TASK_DETAILS, task.getDetails());
        values.put(KEY_TASK_DUE, task.getDue());
        values.put(KEY_TASK_REMIND, task.getRemind());
        values.put(KEY_TASK_REPEAT, task.getRepeat());
        values.put(KEY_TASK_NEXT_REMIND, task.getNextRemind());
        values.put(KEY_TASK_LIST, task.getList());
        return db.update(TABLE_TASKS, values, KEY_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});
    }

    // delete task from table
    void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    // add list to table
    void addList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_LIST_NAME, list.getName());

        db.insert(TABLE_LISTS, null, values);
        db.close();
    }

    // get list from table (by id)
    List getList(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LISTS, new String[]{KEY_LIST_ID, KEY_LIST_NAME}, KEY_LIST_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            List list = new List(Long.parseLong(cursor.getString(0)), cursor.getString(1));
            cursor.close();
            db.close();
            return list;
        }

        cursor.close();
        db.close();
        return null;
    }

    // get list from table (by name)
    List getList(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LISTS + " WHERE " + KEY_LIST_NAME + " =  \"" + name + "\"";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            List list = new List(Long.parseLong(cursor.getString(0)), cursor.getString(1));
            cursor.close();
            db.close();
            return list;
        }

        cursor.close();
        db.close();
        return null;
    }

    // get all lists from table
    ArrayList<List> getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_LISTS + " ORDER BY " + KEY_LIST_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<List> listList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                List list = new List(Long.parseLong(cursor.getString(0)), cursor.getString(1));
                listList.add(list);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listList;
    }

    // update list in table
    int updateList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LIST_NAME, list.getName());

        return db.update(TABLE_LISTS, values, KEY_LIST_ID + " = ?", new String[]{String.valueOf(list.getId())});
    }

    // delete list from table
    void deleteList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, KEY_LIST_ID + " = ?", new String[]{String.valueOf(list.getId())});
        db.close();
    }

    // get all tasks from a particular list
    ArrayList<Task> getTasksFromList(long listId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // select all tasks with due dates first
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " t1, " + TABLE_LISTS + " t2 WHERE t2." + KEY_LIST_ID
                + " = '" + listId + "' AND t1." + KEY_TASK_LIST + " = t2." + KEY_LIST_ID + " AND " + KEY_TASK_DUE + "" +
                " != -1 ORDER BY " + KEY_TASK_DUE + ", " + KEY_TASK_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Task> tasks = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)),
                        Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6)),
                        Long.parseLong(cursor.getString(7)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // select all tasks without due dates
        selectQuery = "SELECT  * FROM " + TABLE_TASKS + " t1, " + TABLE_LISTS + " t2 WHERE t2." + KEY_LIST_ID + " = " +
                "'" + listId + "' AND t1." + KEY_TASK_LIST + " = t2." + KEY_LIST_ID + " AND " + KEY_TASK_DUE + " = -1" +
                " ORDER BY " + KEY_TASK_NAME;
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                        Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)),
                        Long.parseLong(cursor.getString(5)), Long.parseLong(cursor.getString(6)),
                        Long.parseLong(cursor.getString(7)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // delete all tasks from a particular list
    void deleteTasksFromList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_TASK_LIST + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // delete all tasks and lists
    void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, null, null);
        db.delete(TABLE_LISTS, null, null);
        db.close();
    }

}
