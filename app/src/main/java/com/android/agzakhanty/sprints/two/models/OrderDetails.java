package com.android.agzakhanty.sprints.two.models;

/**
 * Created by Aly on 05/06/2018.
 */

public class OrderDetails {

    private String Id;
    private String OrderId;
    private String ItemId;
    private String Status;
    private String Qty;
    private String Price;
    private Item Item;

    public Item getItem() {
        return Item;
    }

    public void setItem(Item item) {
        this.Item = item;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
