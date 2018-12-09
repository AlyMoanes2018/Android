package com.android.agzakhanty.sprints.three.models;

/**
 * Created by Aly on 04/08/2018.
 */

public class Dose {
    private String doseID;
    private String FK_reminderID;
    private String doseNumber;
    private String repeatRate;
    private String hours;
    private String minutes;
    private String dayName;
    private String dayOfMonth;
    private int dayWeekIndex;

    public  Dose(String doseNumber){
        this.doseNumber = doseNumber;
    }

    public String getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(String doseNumber) {
        this.doseNumber = doseNumber;
    }

    public String getRepeatRate() {
        return repeatRate;
    }

    public void setRepeatRate(String repeatRate) {
        this.repeatRate = repeatRate;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getDoseID() {
        return doseID;
    }

    public void setDoseID(String doseID) {
        this.doseID = doseID;
    }

    public String getFK_reminderID() {
        return FK_reminderID;
    }

    public void setFK_reminderID(String FK_reminderID) {
        this.FK_reminderID = FK_reminderID;
    }

    public int getDayWeekIndex() {
        return dayWeekIndex;
    }

    public void setDayWeekIndex(int dayWeekIndex) {
        this.dayWeekIndex = dayWeekIndex;
    }
}
