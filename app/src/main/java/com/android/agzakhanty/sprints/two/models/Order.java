package com.android.agzakhanty.sprints.two.models;

import com.android.agzakhanty.sprints.one.models.Pharmacy;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by a.moanes on 20/05/2018.
 */

public class Order {

    /**
     * PcyName
     * PcyImage
     * "OrderDate": "2018-06-02T00:00:00",
     * "StatusId": 2,
     * "NoofItems": 2,
     * "CstOrderNo": 1,
     * "OrderId": 2,
     * "Status": "Y"
     */

    private String PcyName;
    private String PcyImage;
    private String PcyId;
    private String OrderDate;
    private String StatusId;
    private String NoofItems;
    private String CstOrderNo;
    private String OrderId;
    private String Status;
    private String RecievedDate;
    private String orderComment;
    private String totalPrice;
    private String RemainingTime;
    private String RemainingTimeSecond;
    private String RemainingTimeHours;
    private String DeliveryType;
    private ArrayList<ItemsResponseModel> ListItem;
    private Order activeOrder;

    public String getPcyName() {
        return PcyName;
    }

    public void setPcyName(String pcyName) {
        PcyName = pcyName;
    }

    public String getPcyImage() {
        return PcyImage;
    }

    public void setPcyImage(String pcyImage) {
        PcyImage = pcyImage;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getStatusId() {
        return StatusId;
    }

    public void setStatusId(String statusId) {
        StatusId = statusId;
    }

    public String getNoofItems() {
        return NoofItems;
    }

    public void setNoofItems(String noofItems) {
        NoofItems = noofItems;
    }

    public String getCstOrderNo() {
        return CstOrderNo;
    }

    public void setCstOrderNo(String cstOrderNo) {
        CstOrderNo = cstOrderNo;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<ItemsResponseModel> getListItem() {
        return ListItem;
    }

    public void setListItem(ArrayList<ItemsResponseModel> listItem) {
        ListItem = listItem;
    }

    public Order getActiveOrder() {
        return activeOrder;
    }

    public void setActiveOrder(Order activeOrder) {
        this.activeOrder = activeOrder;
    }

    public String getRecievedDate() {
        return RecievedDate;
    }

    public void setRecievedDate(String recievedDate) {
        RecievedDate = recievedDate;
    }

    public String getOrderComment() {
        return orderComment;
    }

    public void setOrderComment(String orderComment) {
        this.orderComment = orderComment;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getRemainingTime() {
        return RemainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        RemainingTime = remainingTime;
    }

    public String getPcyId() {
        return PcyId;
    }

    public void setPcyId(String pcyId) {
        PcyId = pcyId;
    }

    public String getDeliveryType() {
        return DeliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        DeliveryType = deliveryType;
    }

    public String getRemainingTimeSecond() {
        return RemainingTimeSecond;
    }

    public void setRemainingTimeSecond(String remainingTimeSeconds) {
        RemainingTimeSecond = remainingTimeSeconds;
    }

    public String getRemainingTimeHours() {
        return RemainingTimeHours;
    }

    public void setRemainingTimeHours(String remainingTimeHours) {
        RemainingTimeHours = remainingTimeHours;
    }
}
