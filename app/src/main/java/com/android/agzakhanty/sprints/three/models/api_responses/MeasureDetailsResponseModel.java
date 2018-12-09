package com.android.agzakhanty.sprints.three.models.api_responses;

/**
 * Created by Aly on 30/09/2018.
 */

public class MeasureDetailsResponseModel {

    /*
    * "MsrId": 1,
        "MsrName": "قياس الضغط",
        "MsrNameEn": "Blood Pressure",
        "NoMeasures": 4*/

    private String MsrId;
    private String MsrName;
    private String MsrNameEn;
    private String NoMeasures;

    public String getMsrId() {
        return MsrId;
    }

    public void setMsrId(String msrId) {
        MsrId = msrId;
    }

    public String getMsrName() {
        return MsrName;
    }

    public void setMsrName(String msrName) {
        MsrName = msrName;
    }

    public String getMsrNameEn() {
        return MsrNameEn;
    }

    public void setMsrNameEn(String msrNameEn) {
        MsrNameEn = msrNameEn;
    }

    public String getNoMeasures() {
        return NoMeasures;
    }

    public void setNoMeasures(String noMeasures) {
        NoMeasures = noMeasures;
    }
}
