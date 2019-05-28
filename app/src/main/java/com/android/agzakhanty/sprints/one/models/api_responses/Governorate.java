package com.android.agzakhanty.sprints.one.models.api_responses;

import com.google.gson.annotations.SerializedName;

import retrofit2.http.Streaming;

public class Governorate {
    @SerializedName("Id")
    private int id;
    @SerializedName("GovDescAr")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
