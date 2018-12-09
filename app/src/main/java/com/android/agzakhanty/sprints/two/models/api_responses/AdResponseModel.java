package com.android.agzakhanty.sprints.two.models.api_responses;

import com.android.agzakhanty.sprints.two.models.Ad;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aly on 11/06/2018.
 */

/* "Day": "Dec 12",
        "AdvId": 2010,
        "PcyName": "العزبى",
        "PcyImage
                "AdvSubject": "اشتري 3 بسعر 2",
        "AdvStartDate": "2017-12-13T00:00:00",
        "AdvExpireDate": "2020-10-22T00:00:00",
        "Details": "واحصل علي 200 شعبة هدية ",
        "TotalOrgPrice": 0,
        "Rate": 0

        */
public class AdResponseModel {
private String Day;
    private String AdvId;
    private String PcyName;
    private String PcyImage;
    private String PcyId;
    private String AdvSubject;
    private String AdvStartDate;
    private String AdvExpireDate;
    private String Details;
    private String TotalOrgPrice;
    private String Rate;
    private String AdvImage;
    @SerializedName("Active")
    private String IsActive;


    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getAdvId() {
        return AdvId;
    }

    public void setAdvId(String advId) {
        AdvId = advId;
    }

    public String getPcyName() {
        return PcyName;
    }

    public void setPcyName(String pcyName) {
        PcyName = pcyName;
    }

    public String getPcyImage() {
        return PcyImage;
    }

    public void setPcyImage(String pcyImage) {
        PcyImage = pcyImage;
    }

    public String getAdvSubject() {
        return AdvSubject;
    }

    public void setAdvSubject(String advSubject) {
        AdvSubject = advSubject;
    }

    public String getAdvStartDate() {
        return AdvStartDate;
    }

    public void setAdvStartDate(String advStartDate) {
        AdvStartDate = advStartDate;
    }

    public String getAdvExpireDate() {
        return AdvExpireDate;
    }

    public void setAdvExpireDate(String advExpireDate) {
        AdvExpireDate = advExpireDate;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getTotalOrgPrice() {
        return TotalOrgPrice;
    }

    public void setTotalOrgPrice(String totalOrgPrice) {
        TotalOrgPrice = totalOrgPrice;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getAdvImage() {
        return AdvImage;
    }

    public void setAdvImage(String advImage) {
        AdvImage = advImage;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getPcyId() {
        return PcyId;
    }

    public void setPcyId(String pcyId) {
        PcyId = pcyId;
    }
}
