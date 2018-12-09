package com.android.agzakhanty.sprints.three.models.api_responses;

import com.android.agzakhanty.sprints.three.models.Reminder;

import java.util.ArrayList;

/**
 * Created by Aly on 03/06/2018.
 */

public class ReminderResponseModel {
    ArrayList<Reminder> orders;


    public ArrayList<Reminder> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Reminder> orders) {
        this.orders = orders;
    }


}
