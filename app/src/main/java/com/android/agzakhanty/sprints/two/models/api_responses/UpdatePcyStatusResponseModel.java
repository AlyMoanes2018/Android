package com.android.agzakhanty.sprints.two.models.api_responses;

public class UpdatePcyStatusResponseModel {

    private String IsUpdated;
    private String IsActive;

    public String isUpdated() {
        return IsUpdated;
    }

    public void setUpdated(String updated) {
        IsUpdated = updated;
    }

    public String isActive() {
        return IsActive;
    }

    public void setActive(String active) {
        IsActive = active;
    }
}
