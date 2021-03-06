package com.android.agzakhanty.sprints.three.models;

import com.android.agzakhanty.sprints.one.models.Pharmacy;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aly on 27/07/2018.
 */

public class Consultation {
    private String ConsltionId;
    private String PcyId;
    private String PcyName;
    private String PcyImage;
    private String PcyAddress;
    @SerializedName("ConsltDate")
    private String date;
    private String ConsltionTypeName;
    private String ConsltionTypeNameEn;
    @SerializedName("Status")
    private String status;
    private String ConsltReply;
    private String Subject;
    private String ConsltImgUrl;



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConsltionId() {
        return ConsltionId;
    }

    public void setConsltionId(String consltionId) {
        ConsltionId = consltionId;
    }

    public String getPcyId() {
        return PcyId;
    }

    public void setPcyId(String pcyId) {
        PcyId = pcyId;
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

    public String getPcyAddress() {
        return PcyAddress;
    }

    public void setPcyAddress(String pcyAddress) {
        PcyAddress = pcyAddress;
    }

    public String getConsltionTypeName() {
        return ConsltionTypeName;
    }

    public void setConsltionTypeName(String consltionTypeName) {
        ConsltionTypeName = consltionTypeName;
    }

    public String getConsltionTypeNameEn() {
        return ConsltionTypeNameEn;
    }

    public void setConsltionTypeNameEn(String consltionTypeNameEn) {
        ConsltionTypeNameEn = consltionTypeNameEn;
    }

    public String getConsltReply() {
        return ConsltReply;
    }

    public void setConsltReply(String consltReply) {
        ConsltReply = consltReply;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getConsltImgUrl() {
        return ConsltImgUrl;
    }

    public void setConsltImgUrl(String consltImgUrl) {
        ConsltImgUrl = consltImgUrl;
    }
}
