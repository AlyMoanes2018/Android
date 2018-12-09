package com.android.agzakhanty.sprints.three.models.api_requests;

/**
 * Created by a.moanes on 28/08/2018.
 */

public class SaveViolationtRequestModel {
    /* "ReportTypeId":"1",
 "Details":"صيدليه غير مرخصه"*/

    private String ReportTypeId;
    private String Details;

    public String getReportTypeId() {
        return ReportTypeId;
    }

    public void setReportTypeId(String reportTypeId) {
        ReportTypeId = reportTypeId;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }
}
