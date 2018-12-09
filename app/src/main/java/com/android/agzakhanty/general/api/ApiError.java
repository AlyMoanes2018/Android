package com.android.agzakhanty.general.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by GAFI on 28/08/2017.
 */

public class ApiError {
    @SerializedName("ErrorMSG")
    private String error;
    @SerializedName("error_description")
    private String loginErrorMsg;
    @SerializedName("ErrorMSGAr")
    private String arabicErrorMsg;
    private String arabicLoginErrorMsg;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getLoginErrorMsg() {
        return loginErrorMsg;
    }

    public void setLoginErrorMsg(String loginErrorMsg) {
        this.loginErrorMsg = loginErrorMsg;
    }

    public String getArabicErrorMsg() {
        return arabicErrorMsg;
    }

    public void setArabicErrorMsg(String arabicErrorMsg) {
        this.arabicErrorMsg = arabicErrorMsg;
    }

    public String getArabicLoginErrorMsg() {
        return arabicLoginErrorMsg;
    }

    public void setArabicLoginErrorMsg(String arabicLoginErrorMsg) {
        this.arabicLoginErrorMsg = arabicLoginErrorMsg;
    }
}
