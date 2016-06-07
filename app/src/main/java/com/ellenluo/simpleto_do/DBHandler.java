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
    private static final String TABLE_TASKS = "tasks";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DETAILS = "details";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASK_TABLE = "CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, details TEXT)";
        db.execSQL(CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, task.getName());
        values.put(KEY_DETAILS, task.getDetails());

        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_ID, KEY_NAME, KEY_DETAILS}, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

        return task;
    }

    public ArrayList<Task> getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery =  "SELECT * FROM tasks";
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.i("TAG", "Query returned: " + cursor.getCount());

        ArrayList<Task> taskList = new ArrayList<Task>();

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        Log.i("TAG", taskList.size() + " tasks found");

        return taskList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, task.getName());
        values.put(KEY_DETAILS, task.getDetails());

        return db.update(TABLE_TASKS, values, KEY_ID + " = ?", new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?", new String[] { String.valueOf(task.getId()) });
        db.close();
    }

}
