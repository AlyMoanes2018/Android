package com.android.agzakhanty.sprints.one.models;

import com.android.agzakhanty.sprints.one.models.Tag;
import com.android.agzakhanty.sprints.one.models.TagAndUser;
import com.google.gson.annotations.SerializedName;

/**
 * Created by a.moanes on 14/05/2018.
 */

public class TagsAndStatus {

    @SerializedName("tags")
    private Tag tag;
    private String Status;
    private TagAndUser ifCheckedObject = new TagAndUser();


    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public TagAndUser getIfCheckedObject() {
        return ifCheckedObject;
    }

    public void setIfCheckedObject(TagAndUser ifCheckedObject) {
        this.ifCheckedObject = ifCheckedObject;
    }


}
