package com.android.agzakhanty.sprints.two.models.api_responses;

import com.android.agzakhanty.sprints.two.models.Order;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Aly on 03/06/2018.
 */

public class OrderResponseModel {
    ArrayList<Order> orders;


    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }


}
