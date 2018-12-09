package com.android.agzakhanty.api;

import com.android.agzakhanty.api.responses.MainWSResponseModel;


/**
 * Created by a.moanes on 24/09/2017.
 */

public interface OnWebServiceCallEndedListener {

    public void onWebServiceCallEnded(MainWSResponseModel resultingResponse, int operationCode);
}
