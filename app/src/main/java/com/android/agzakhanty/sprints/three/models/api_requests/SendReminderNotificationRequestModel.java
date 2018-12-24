package com.android.agzakhanty.sprints.three.models.api_requests;

import java.util.ArrayList;

public class SendReminderNotificationRequestModel {

    private String MedicineName;
    private String ReminderDescName;
    private ArrayList<String> ReminderTimes;

    public String getMedicineName() {
        return MedicineName;
    }

    public void setMedicineName(String medicineName) {
        MedicineName = medicineName;
    }

    public ArrayList<String> getReminderTimes() {
        return ReminderTimes;
    }

    public void setReminderTimes(ArrayList<String> reminderTimes) {
        ReminderTimes = reminderTimes;
    }

    public String getReminderDescName() {
        return ReminderDescName;
    }

    public void setReminderDescName(String reminderDescName) {
        ReminderDescName = reminderDescName;
    }
}
