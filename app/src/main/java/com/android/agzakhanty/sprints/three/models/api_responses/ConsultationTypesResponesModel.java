package com.android.agzakhanty.sprints.three.models.api_responses;

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
public class ConsultationTypesResponesModel {

    private String Id;
    private String NameEn;
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
