package com.android.agzakhanty.sprints.two.models.api_responses;

import com.android.agzakhanty.sprints.one.models.Pharmacy;

import java.util.ArrayList;

/**
 * Created by Aly on 29/05/2018.
 */

public class CircleResponseModel {

    private String Status;
    private Pharmacy Circles;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Pharmacy getCircles() {
        return Circles;
    }

    public void setCircles(Pharmacy circles) {
        Circles = circles;
    }
}
