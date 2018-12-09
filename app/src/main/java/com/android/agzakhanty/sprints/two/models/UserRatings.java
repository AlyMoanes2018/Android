package com.android.agzakhanty.sprints.two.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a.moanes on 24/05/2018.
 */

public class UserRatings {
    @SerializedName("CstmrName")
    private String userName;
    @SerializedName("Rate")
    private float rating;
    @SerializedName("Comment")
    private String comment;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
