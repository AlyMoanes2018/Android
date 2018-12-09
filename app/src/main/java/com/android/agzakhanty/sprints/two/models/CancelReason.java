package com.android.agzakhanty.sprints.two.models;

/**
 * Created by Aly on 29/07/2018.
 */

public class CancelReason {

    /* "Id": 1,
        "DescAr": "خطأ فى الطلب",
        "DescEn": "Error in Order",
        "Active": "Y"*/
    private String Id;
    private String DescAr;
    private String DescEn;
    private String Active;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDescAr() {
        return DescAr;
    }

    public void setDescAr(String descAr) {
        DescAr = descAr;
    }

    public String getDescEn() {
        return DescEn;
    }

    public void setDescEn(String descEn) {
        DescEn = descEn;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }
}
