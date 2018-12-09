package com.android.agzakhanty.sprints.one.models.api_responses;

import com.android.agzakhanty.sprints.one.models.Pharmacy;

/**
 * Created by a.moanes on 21/05/2018.
 */

public class PharmacyDistance {
    private String DistanceResult;
    private com.android.agzakhanty.sprints.one.models.Pharmacy Pharmacy;
    private String Status;
    private String Rate;
    private String IsCircle;
    private String LastMeasure;
    private String TotalNoOrders;

    public String getDistanceResult() {
        return DistanceResult;
    }

    public void setDistanceResult(String distanceResult) {
        DistanceResult = distanceResult;
    }

    public com.android.agzakhanty.sprints.one.models.Pharmacy getPharmacy() {
        return Pharmacy;
    }

    public void setPharmacy(com.android.agzakhanty.sprints.one.models.Pharmacy pharmacy) {
        Pharmacy = pharmacy;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getIsCircle() {
        return IsCircle;
    }

    public void setIsCircle(String isCircle) {
        IsCircle = isCircle;
    }

    public String getLastMeasure() {
        return LastMeasure;
    }

    public void setLastMeasure(String lastMeasure) {
        LastMeasure = lastMeasure;
    }

    public String getTotalNoOrders() {
        return TotalNoOrders;
    }

    public void setTotalNoOrders(String totalNoOrders) {
        TotalNoOrders = totalNoOrders;
    }
}
