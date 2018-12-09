package com.android.agzakhanty.sprints.two.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aly on 11/06/2018.
 */

public class AdDetails {

    private String AdvId;
    @SerializedName("Qty")
    private String quantity;
    @SerializedName("Item")
    private Item item;
    @SerializedName("Active")
    private String isActive;

    public String getAdvId() {
        return AdvId;
    }

    public void setAdvId(String advId) {
        AdvId = advId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
