package com.android.agzakhanty.sprints.one.models;

/**
 * Created by a.moanes on 14/05/2018.
 */

public class Tag {

    private String Id;
    private String TagEn;
    private String TagAr;
    private String TagUrl;

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

    public String getTagUrl() {
        return TagUrl;
    }

    public void setTagUrl(String tagUrl) {
        TagUrl = tagUrl;
    }
}
