package com.android.agzakhanty.sprints.three.models.api_responses;

import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Aly on 28/07/2018.
 */

public class SaveOrderDetails {

    /*"CstId":9,
  "PcyId":1007,
  "DeliveryType": "H",
    "ReqType":"O",
  "Lattitude":40,
  "Longitude":60,
  "Discount" : 0,
  "Total" : 150,
  "Status":"Y",
  "Comment":"sdsds"
  */

    private String CstId;
    private String PcyId;
    private String DeliveryType;
    private String ReqType;
    private String Lattitude;
    private String Longitude;
    private String Discount;
    private String Total;
    private String Status;
    private String Comment;
    private String RefType;
    private String RefId;
    @SerializedName("CstDtlOrdTrns")
    private ArrayList<ItemsResponseModel> ItemsList;

    public SaveOrderDetails(){
        CstId = "";
        PcyId = "";
        DeliveryType = "H";
        ReqType = "O";
        Lattitude = "";
        Longitude = "";
        Discount = "0";
        Total = "";
        Status = "Y";
        Comment = "";
    }

    public String getCstId() {
        return CstId;
    }

    public void setCstId(String cstId) {
        CstId = cstId;
    }

    public String getPcyId() {
        return PcyId;
    }

    public void setPcyId(String pcyId) {
        PcyId = pcyId;
    }

    public String getDeliveryType() {
        return DeliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        DeliveryType = deliveryType;
    }

    public String getReqType() {
        return ReqType;
    }

    public void setReqType(String reqType) {
        ReqType = reqType;
    }

    public String getLattitude() {
        return Lattitude;
    }

    public void setLattitude(String lattitude) {
        Lattitude = lattitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public ArrayList<ItemsResponseModel> getItemsList() {
        return ItemsList;
    }

    public void setItemsList(ArrayList<ItemsResponseModel> itemsList) {
        ItemsList = itemsList;
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
