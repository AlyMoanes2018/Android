package com.android.agzakhanty.sprints.two.models.api_responses;

/**
 * Created by a.moanes on 05/07/2018.
 */

public class ItemsResponseModel {
    /* "Id": 10001,
        "NameEn": "Catheter Foly- Size 10",
        "NameAr": "قسطرة فولي مقاس 10",
        "Price": 10,
        "Qty": 0,
        "Bonus": 0,
        "Discount": 0*/

    /*"RefId": null,
            "BasicItemHint": null*/
    private String ItemId;
    private String NameEn;
    private String NameAr;
    private String Price;
    private String Qty;
    private String Bonus;
    private String Discount;
    private String isItemAvailable;
    private String Status;
    private String RefId;
    private String BasicItemHint;

    public String getId() {
        return ItemId;
    }

    public void setId(String id) {
        ItemId = id;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getBonus() {
        return Bonus;
    }

    public void setBonus(String bonus) {
        Bonus = bonus;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getIsItemAvailable() {
        return isItemAvailable;
    }

    public void setIsItemAvailable(String isItemAvailable) {
        this.isItemAvailable = isItemAvailable;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRefId() {
        return RefId;
    }

    public void setRefId(String refId) {
        RefId = refId;
    }

    public String getBasicItemHint() {
        return BasicItemHint;
    }

    public void setBasicItemHint(String basicItemHint) {
        BasicItemHint = basicItemHint;
    }
}
