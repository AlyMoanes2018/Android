package com.android.agzakhanty.sprints.three.models.api_responses;

import com.android.agzakhanty.R;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a.moanes on 28/08/2018.
 */

/*
* {
        "Id": 2,
        "NameEn": "شكوي من أعراض",
        "NameAr": "شكوي من أعراض",
        "Active": "Y",
    }*/
public class ViolationTypesResponesModel {

    private String Id;
    @SerializedName("DescEn")
    private String NameEn;
    @SerializedName("DescAr")
    private String NameAr;
    private String Active;


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

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }
}
