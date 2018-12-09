package com.android.agzakhanty.sprints.one.models.api_responses;

import com.android.agzakhanty.sprints.one.models.Customer;

/**
 * Created by Aly on 05/05/2018.
 */

public class CustomerInfoResponseModel {

    private String Status;
    private Customer Cstmr;


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Customer getCstmr() {
        return Cstmr;
    }

    public void setCstmr(Customer cstmr) {
        Cstmr = cstmr;
    }

}
