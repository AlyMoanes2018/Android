package com.android.agzakhanty.sprints.one.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aly on 27/04/2018.
 */

public class Pharmacy {

    @SerializedName("PcyName")
    private String name;
    @SerializedName("Id")
    private String id;
    @SerializedName("Address")
    private String address;
    private String distance;
    @SerializedName("ProfilePhotoImgUrl")
    private String logoURL;
    private String InterfaceImgUrl;
    private String Longitude;
    private String Latitude;
    @SerializedName("PcyMngrMobile")
    private String mobile;
    private String AllDay;
    private String Delivery;
    private String Chat;
    private String Rate;
    private String Active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAllDay() {
        return AllDay;
    }

    public void setAllDay(String allDay) {
        AllDay = allDay;
    }

    public String getDelivery() {
        return Delivery;
    }

    public void setDelivery(String delivery) {
        Delivery = delivery;
    }

    public String getChat() {
        return Chat;
    }

    public void setChat(String chat) {
        Chat = chat;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }

    public String getInterfaceImgUrl() {
        return InterfaceImgUrl;
    }

    public void setInterfaceImgUrl(String interfaceImgUrl) {
        InterfaceImgUrl = interfaceImgUrl;
    }
}
