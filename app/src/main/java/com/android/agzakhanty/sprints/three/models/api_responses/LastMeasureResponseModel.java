package com.android.agzakhanty.sprints.three.models.api_responses;

/**
 * Created by Aly on 06/10/2018.
 */
/*"Id": 1041,
    "Val1": 120,
    "Val2": 80,
    "MsrDate": "2018-10-06T22:42:56",
    "MsrId": 1
    */
public class LastMeasureResponseModel {

    private String Id;
    private String Val1;
    private String Val2;
    private String MsrDate;
    private String MsrId;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

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

    public String getMsrDate() {
        return MsrDate;
    }

    public void setMsrDate(String msrDate) {
        MsrDate = msrDate;
    }

    public String getMsrId() {
        return MsrId;
    }

    public void setMsrId(String msrId) {
        MsrId = msrId;
    }
}
