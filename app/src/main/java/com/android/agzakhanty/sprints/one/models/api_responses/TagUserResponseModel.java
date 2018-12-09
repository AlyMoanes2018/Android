package com.android.agzakhanty.sprints.one.models.api_responses;

import com.android.agzakhanty.sprints.one.models.TagAndUser;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by a.moanes on 15/05/2018.
 */

public class TagUserResponseModel {

    private String Status;
    @SerializedName("ListTagUsr")
    private ArrayList<TagAndUser> tags;


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<TagAndUser> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagAndUser> tags) {
        this.tags = tags;
    }
}
