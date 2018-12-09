package com.android.agzakhanty.sprints.three.models.api_requests;

/**
 * Created by Aly on 29/09/2018.
 */

/*"PcyId":1007,
  "CstId":9,
  "MsrId":1,
  "Val1":125,
  "Val2":null*/
public class SaveMeasurementRequestModel {

    private String PcyId;
    private String CstId;
    private String MsrId;
    private String Val1;
    private String Val2;

    public String getPcyId() {
        return PcyId;
    }

    public void setPcyId(String pcyId) {
        PcyId = pcyId;
    }

    public String getCstId() {
        return CstId;
    }

    public void setCstId(String cstId) {
        CstId = cstId;
    }

    public String getMsrId() {
        return MsrId;
    }

    public void setMsrId(String msrId) {
        MsrId = msrId;
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
}
