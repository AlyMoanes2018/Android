package com.android.agzakhanty.sprints.three.models;

/**
 * Created by Aly on 28/09/2018.
 */

/*"Id": 1,
        "NameEn": "Blood Pressure",
        "NameAr": "قياس الضغط",
        "Unit": null,
        "Active": "Y"
        "IsTwoValues":"Y"
        "Count":"5"*/

public class Measurement {

    private String Id;
    private String NameEn;
    private String NameAr;
    private String Unit;
    private String Active;
    private String IsTwoValues;
    private String Count;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }

    public String getIsTwoValues() {
        return IsTwoValues;
    }

    public void setIsTwoValues(String isTwoValues) {
        IsTwoValues = isTwoValues;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }
}
