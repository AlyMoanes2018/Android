package com.android.agzakhanty.sprints.three.models;

/**
 * Created by Aly on 06/10/2018.
 */

/*"Val1": 0,
            "Val2": 0,
            "ValNo": 1,
            "Active": "Y",
            "MsrDate": "2018-09-01T06:00:00",
            "Month": "Sep"*/
public class MeasurementChartDetails {

    private String Val1;
    private String Val2;
    private String ValNo;
    private String Active;
    private String MsrDate;
    private String Month;

    public String getVal1() {
        return Val1;
    }

    public void setVal1(String val1) {
        Val1 = val1;
    }

    public String getVal2() {
        return Val2;
    }

    public void setVal2(String val2) {
        Val2 = val2;
    }

    public String getValNo() {
        return ValNo;
    }

    public void setValNo(String valNo) {
        ValNo = valNo;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }

    public String getMsrDate() {
        return MsrDate;
    }

    public void setMsrDate(String msrDate) {
        MsrDate = msrDate;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }
}
