package com.android.agzakhanty.sprints.two.models.api_responses;

import com.android.agzakhanty.sprints.one.models.Pharmacy;

/**
 * Created by Aly on 27/05/2018.
 */

public class PharmacyResponseModel {

    private String Status;
    private Pharmacy Pharmacy;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public com.android.agzakhanty.sprints.one.models.Pharmacy getPharmacy() {
        return Pharmacy;
    }

    public void setPharmacy(com.android.agzakhanty.sprints.one.models.Pharmacy pharmacy) {
        Pharmacy = pharmacy;
    }
}
