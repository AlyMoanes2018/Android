package com.android.agzakhanty.sprints.one.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a.moanes on 15/05/2018.
 */

public class TagAndUser {

    private String CstId;
    private String TagId;


    public String getCstId() {
        return CstId;
    }

    public void setCstId(String cstId) {
        CstId = cstId;
    }

    public String getTagId() {
        return TagId;
    }

    public void setTagId(String tagId) {
        TagId = tagId;
    }


}
