package com.android.agzakhanty.sprints.three.models.api_responses;

import com.android.agzakhanty.sprints.three.models.Measurement;

/**
 * Created by Aly on 07/10/2018.
 */

/*"Status": true,
    "CstMsrTrn*/
public class UpdateMeasureResponseModel {
    private String Status;
    private Measurement CstMsrTrn;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Measurement getCstMsrTrn() {
        return CstMsrTrn;
    }

    public void setCstMsrTrn(Measurement cstMsrTrn) {
        CstMsrTrn = cstMsrTrn;
    }
}
