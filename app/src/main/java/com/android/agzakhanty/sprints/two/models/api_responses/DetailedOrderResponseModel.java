package com.android.agzakhanty.sprints.two.models.api_responses;

import java.util.ArrayList;

/**
 * Created by Aly on 28/07/2018.
 */

public class DetailedOrderResponseModel {
    /*"DeliveryType": "D",
    "Total": 30,
    "Comment": "",
    "OrderId": 1012,
    "ListItem*/
    private String DeliveryType;
    private String Total;
    private String Comment;
    private String OrderId;
    private String StatusId;
    private ArrayList<ItemsResponseModel> ListItem;

    public String getDeliveryType() {
        return DeliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        DeliveryType = deliveryType;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public ArrayList<ItemsResponseModel> getListItem() {
        return ListItem;
    }

    public void setListItem(ArrayList<ItemsResponseModel> listItem) {
        ListItem = listItem;
    }

    public String getStatusId() {
        return StatusId;
    }

    public void setStatusId(String statusId) {
        StatusId = statusId;
    }
}
