package com.android.agzakhanty.sprints.two.models;

/**
 * Created by Aly on 05/06/2018.
 */

public class Item {

    private String Id;
    private String NameAr;
    private String NameEn;
    private float Price;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }
}
