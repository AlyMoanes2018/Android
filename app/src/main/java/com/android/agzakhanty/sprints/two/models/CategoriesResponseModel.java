package com.android.agzakhanty.sprints.two.models;

/*        "Id": 1,
        "TagEn": "Skin Care",
        "TagAr": "منتجات العناية بالبشرة ",
        "Active": "Y",
        "TagAdv": [],
        "TagUsr": []*/

public class CategoriesResponseModel {
    private String Id;
    private String TagEn;
    private String TagAr;
    private String Active;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTagEn() {
        return TagEn;
    }

    public void setTagEn(String tagEn) {
        TagEn = tagEn;
    }

    public String getTagAr() {
        return TagAr;
    }

    public void setTagAr(String tagAr) {
        TagAr = tagAr;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }
}
