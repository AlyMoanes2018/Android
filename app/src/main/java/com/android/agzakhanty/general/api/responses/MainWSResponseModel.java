package com.android.agzakhanty.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


/**
 * Created by a.moanes on 20/09/2017.
 */

public class MainWSResponseModel {

    private int statusCode;
    private String errorMsg;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}



