package com.android.agzakhanty.sprints.three.models.api_responses;

import com.android.agzakhanty.sprints.three.models.MeasurementChartDetails;

import java.util.ArrayList;

/**
 * Created by Aly on 06/10/2018.
 */

/*"MinMsrDate": "2018-09-01T06:00:00",
    "MaxMsrDate": "2018-09-30T00:00:09",
    "StartMonth": "Sep",
    "EndMonth": "Sep",
    "ExistChartCstMsrTrnList": [*/
public class MeasurementChartDetailsResponseModel {

    private String MinMsrDate;
    private String MaxMsrDate;
    private String StartMonth;
    private String EndMonth;
    private ArrayList<MeasurementChartDetails> ExistChartCstMsrTrnList;

    public String getMinMsrDate() {
        return MinMsrDate;
    }

    public void setMinMsrDate(String minMsrDate) {
        MinMsrDate = minMsrDate;
    }

    public String getMaxMsrDate() {
        return MaxMsrDate;
    }

    public void setMaxMsrDate(String maxMsrDate) {
        MaxMsrDate = maxMsrDate;
    }

    public String getStartMonth() {
        return StartMonth;
    }

    public void setStartMonth(String startMonth) {
        StartMonth = startMonth;
    }

    public String getEndMonth() {
        return EndMonth;
    }

    public void setEndMonth(String endMonth) {
        EndMonth = endMonth;
    }

    public ArrayList<MeasurementChartDetails> getExistChartCstMsrTrnList() {
        return ExistChartCstMsrTrnList;
    }

    public void setExistChartCstMsrTrnList(ArrayList<MeasurementChartDetails> existChartCstMsrTrnList) {
        ExistChartCstMsrTrnList = existChartCstMsrTrnList;
    }
}
