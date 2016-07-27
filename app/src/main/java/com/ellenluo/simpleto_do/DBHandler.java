package com.ellenluo.simpleto_do;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbTasks";

    // tables
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_LISTS = "lists";

    // column names
    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_TASK_NAME = "task_name";
    private static final String KEY_TASK_DETAILS = "task_details";
    private static final String KEY_TASK_DUE = "task_due";
    private static final String KEY_TASK_LIST = "task_list";
    private static final String KEY_LIST_ID = "list_id";
    private static final String KEY_LIST_NAME = "list_name";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" + KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TASK_NAME + " TEXT, " + KEY_TASK_DETAILS + " TEXT, " + KEY_TASK_DUE + " TEXT, " + KEY_TASK_LIST + " TEXT)";
        String CREATE_LIST_TABLE = "CREATE TABLE " + TABLE_LISTS + " (" + KEY_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_LIST_NAME + " TEXT)";

        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }

    // all task table operations
    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TASK_NAME, task.getName());
        values.put(KEY_TASK_DETAILS, task.getDetails());
        values.put(KEY_TASK_DUE, task.getDue());
        values.put(KEY_TASK_LIST, task.getList());

        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_TASK_ID, KEY_TASK_NAME, KEY_TASK_DETAILS, KEY_TASK_DUE, KEY_TASK_LIST}, KEY_TASK_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), cursor.getString(4));
        return task;
    }

    public ArrayList<Task> getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery =  "SELECT * FROM " + TABLE_TASKS + " WHERE " + KEY_TASK_DUE + " != -1 ORDER BY " + KEY_TASK_DUE + ", " + KEY_TASK_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Task> taskList = new ArrayList<Task>();

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), cursor.getString(4));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        selectQuery =  "SELECT * FROM " + TABLE_TASKS + " WHERE " + KEY_TASK_DUE + " = -1 ORDER BY " + KEY_TASK_NAME;
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), cursor.getString(4));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        return taskList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TASK_NAME, task.getName());
        values.put(KEY_TASK_DETAILS, task.getDetails());
        values.put(KEY_TASK_DUE, task.getDue());
        values.put(KEY_TASK_LIST, task.getList());

        return db.update(TABLE_TASKS, values, KEY_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_TASK_ID + " = ?", new String[] { String.valueOf(task.getId()) });
        db.close();
    }

    // all list table operations
    public void addList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_LIST_NAME, list.getName());

        db.insert(TABLE_LISTS, null, values);
        db.close();
    }

    public List getList(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "Select * FROM " + TABLE_LISTS + " WHERE " + KEY_LIST_NAME + " =  \"" + name + "\"";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        List list = new List(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
        return list;
    }

    public ArrayList<List> getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery =  "SELECT * FROM " + TABLE_LISTS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<List> listList = new ArrayList<List>();

        if (cursor.moveToFirst()) {
            do {
                List list = new List(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
                listList.add(list);
            } while (cursor.moveToNext());
        }

        return listList;
    }

    public int updateList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LIST_NAME, list.getName());

        return db.update(TABLE_LISTS, values, KEY_LIST_ID + " = ?", new String[]{String.valueOf(list.getId())});
    }

    public void deleteList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LISTS, KEY_LIST_ID + " = ?", new String[] { String.valueOf(list.getId()) });
        db.close();
    }

    public ArrayList<Task> getTasksFromList(String listName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " t1, " + TABLE_LISTS + " t2 WHERE t2." + KEY_LIST_NAME + " = '" + listName + "' AND t1." + KEY_TASK_LIST + " = t2." + KEY_LIST_NAME + " AND " + KEY_TASK_DUE + " != -1 ORDER BY " + KEY_TASK_DUE + ", " + KEY_TASK_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<Task> tasks = new ArrayList<Task>();

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), cursor.getString(4));
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        selectQuery = "SELECT  * FROM " + TABLE_TASKS + " t1, " + TABLE_LISTS + " t2 WHERE t2." + KEY_LIST_NAME + " = '" + listName + "' AND t1." + KEY_TASK_LIST + " = t2." + KEY_LIST_NAME + " AND " + KEY_TASK_DUE + " = -1 ORDER BY " + KEY_TASK_NAME;
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Long.parseLong(cursor.getString(3)), cursor.getString(4));
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        return tasks;
    }
}
