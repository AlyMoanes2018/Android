package com.android.agzakhanty.sprints.two.models;

import com.android.agzakhanty.sprints.one.models.Pharmacy;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by a.moanes on 24/05/2018.
 */

public class Ad {

    @SerializedName("Pharmacy")
    private Pharmacy pharmacy;
    @SerializedName("EndDate")
    private String expiryDate;
    @SerializedName("StartDate")
    private String startDate;
    @SerializedName("Details")
    private String description;
    @SerializedName("Subject")
    private String subject;
    @SerializedName("Image")
    private String image;
    @SerializedName("Active")
    private String isActive;
    @SerializedName("PcyAdvDtl")
    ArrayList<AdDetails> adDetails = new ArrayList<>();

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public ArrayList<AdDetails> getAdDetails() {
        return adDetails;
    }

    public void setAdDetails(ArrayList<AdDetails> adDetails) {
        this.adDetails = adDetails;
    }
}
