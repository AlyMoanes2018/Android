package com.android.agzakhanty.sprints.three.models;

/**
 * Created by a.moanes on 20/05/2018.
 */

public class Reminder {


    /*id
    *name
    * type
    * item
    * end date
    * dose number
    * repeat rate (daily, weekly or monthly)
    * reminder hours
    * reminder minutes
    * reminder day name
    * reminder day of month*/
    private String reminderID;
    private String name;
    private String date;
    private String Status;

    public static final String TABLE_NAME = "reminders";
    public static final String ID_COLUMN_NAME = "reminderID";
    public static final String NAME_COLUMN_NAME = "name";
    public static final String DATE_COLUMN_NAME = "date";
    public static final String STATUS_COLUMN_NAME = "Status";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + NAME_COLUMN_NAME + " TEXT,"
                    + DATE_COLUMN_NAME + " TEXT,"
                    + STATUS_COLUMN_NAME + " TEXT"
                    + ")";

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }


    public String getReminderID() {
        return reminderID;
    }

    public void setReminderID(String reminderID) {
        this.reminderID = reminderID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
