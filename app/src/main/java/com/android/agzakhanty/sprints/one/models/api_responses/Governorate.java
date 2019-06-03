package com.android.agzakhanty.sprints.one.models.api_responses;

import com.google.gson.annotations.SerializedName;

import retrofit2.http.Streaming;

public class Governorate {
    @SerializedName("Id")
    private Integer id;
    @SerializedName("GovDescAr")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
