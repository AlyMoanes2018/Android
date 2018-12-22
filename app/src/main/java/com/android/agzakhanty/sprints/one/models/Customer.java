package com.android.agzakhanty.sprints.one.models;

/**
 * Created by Aly on 05/05/2018.
 */

/*"Id": 9,
        "Name": "محمود البهنساوي",
        "E_Mail": "asdsadb@gmail.com",
        "Address": "20 شارع الوحدة",
        "Mobile": "01002454121",
        "Latitude": 20,
        "Longitude": 40,
        "CityId": 1,
        "Governrate_id": 10,
        "Gender": "M",
        "DateOfBirth": null,
        "FavPcy": 2,
        "Profile_Photo": null,
        "RegId": null,
        "FbId": "2",
        "GmailId": "3",
        "Pwd": "123"*/
public class Customer {

    private String Id;
    private String Name;
    private String E_Mail;
    private String Mobile;
    private String Latitude;
    private String Longitude;
    private String Gender;
    private String DateOfBirth;
    private String FavPcy;
    private String RegId;
    private String FbId;
    private String GmailId;
    private String Pwd;
    private String ProfilePhoto;
    private String ProfilePhotoImgUrl;
    private String FileName;

    public void setFileName() {
        FileName =  Id + "_Photo.jpg";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getE_Mail() {
        return E_Mail;
    }

    public void setE_Mail(String e_Mail) {
        E_Mail = e_Mail;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getFavPcy() {
        return FavPcy;
    }

    public void setFavPcy(String favPcy) {
        FavPcy = favPcy;
    }

    public String getRegId() {
        return RegId;
    }

    public void setRegId(String regId) {
        RegId = regId;
    }

    public String getFbId() {
        return FbId;
    }

    public void setFbId(String fbId) {
        FbId = fbId;
    }

    public String getGmailId() {
        return GmailId;
    }

    public void setGmailId(String gmailId) {
        GmailId = gmailId;
    }

    public String getPwd() {
        return Pwd;
    }

    public void setPwd(String pwd) {
        Pwd = pwd;
    }

    public String getProfile_Photo() {
        return ProfilePhoto;
    }

    public void setProfile_Photo(String profile_Photo) {
        ProfilePhoto = profile_Photo;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getProfilePhotoImgUrl() {
        return ProfilePhotoImgUrl;
    }

    public void setProfilePhotoImgUrl(String profilePhotoImgUrl) {
        ProfilePhotoImgUrl = profilePhotoImgUrl;
    }
}
