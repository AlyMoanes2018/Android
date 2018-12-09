package com.android.agzakhanty.sprints.three.models.api_responses;

import com.android.agzakhanty.sprints.two.models.OrderDetails;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Aly on 28/07/2018.
 */

public class SaveOrderResponseModel {

    /*"ReqType":"O",
  "Details":null,
  "RxImage":null, */
    private String ReqType;
    private String Details;
    private String RxImage;
    @SerializedName("CstOrdTrn")
    private ArrayList<SaveOrderDetails> orderDetails;
    private String FileName;
    private String RefType;
    private String RefId;


    public SaveOrderResponseModel(){
        ReqType = "O";
        Details = null;
        RxImage = "";
        orderDetails = new ArrayList<>();
    }

    public String getReqType() {
        return ReqType;
    }

    public void setReqType(String reqType) {
        ReqType = reqType;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getRxImage() {
        return RxImage;
    }

    public void setRxImage(String rxImage) {
        RxImage = rxImage;
    }

    public ArrayList<SaveOrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(ArrayList<SaveOrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setFileName() {
        FileName = orderDetails.get(0).getCstId() + "_order.jpg";
    }

    public String getRefType() {
        return RefType;
    }

    public void setRefType(String refType) {
        RefType = refType;
    }

    public String getRefId() {
        return RefId;
    }

    public void setRefId(String refId) {
        RefId = refId;
    }
}
