package com.android.agzakhanty.general.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.agzakhanty.sprints.three.models.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.moanes on 18/09/2018.
 */

public class LocalDBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "remindersDB";


    public LocalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create reminders table
        sqLiteDatabase.execSQL(Reminder.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Reminder.TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public long insertReminder(Reminder reminder) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` will be inserted automatically.
        // no need to add it
        values.put(Reminder.NAME_COLUMN_NAME, reminder.getName());
        values.put(Reminder.DATE_COLUMN_NAME, reminder.getDate());
        values.put(Reminder.STATUS_COLUMN_NAME, reminder.getStatus());

        // insert row
        long id= 0;
        try {
            id = db.insert(Reminder.TABLE_NAME, null, values);
        }catch (Exception e){
            Log.d("TEST_DB","SQLException"+String.valueOf(e.getMessage()));
        }

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Reminder.TABLE_NAME + " ORDER BY " +
                Reminder.DATE_COLUMN_NAME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setReminderID(cursor.getString(cursor.getColumnIndex(Reminder.ID_COLUMN_NAME)));
                reminder.setName(cursor.getString(cursor.getColumnIndex(Reminder.NAME_COLUMN_NAME)));
                reminder.setDate(cursor.getString(cursor.getColumnIndex(Reminder.DATE_COLUMN_NAME)));
                reminder.setStatus(cursor.getString(cursor.getColumnIndex(Reminder.STATUS_COLUMN_NAME)));
                reminders.add(reminder);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return reminders list
        return reminders;
    }

    public int updateReminderStatus(Reminder updatedReminder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Reminder.STATUS_COLUMN_NAME, updatedReminder.getStatus());

        // updating row
        return db.update(Reminder.TABLE_NAME, values, Reminder.ID_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(updatedReminder.getReminderID())});
    }
}
