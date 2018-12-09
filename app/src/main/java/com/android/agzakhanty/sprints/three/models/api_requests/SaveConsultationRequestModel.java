package com.android.agzakhanty.sprints.three.models.api_requests;

/**
 * Created by a.moanes on 28/08/2018.
 */

public class SaveConsultationRequestModel {

    /*
    * "PcyId":"1007",
 "CstId":"9",
 "ConsltTypeId":"2",
 "Subject":"اشتكى من الالم ف المعده",
 "Photo":null*/

    private String PcyId;
    private String CstId;
    private String ConsltTypeId;
    private String Subject;
    private String Photo;
    private String FileName;

    public String getPcyId() {
        return PcyId;
    }

    public void setPcyId(String pcyId) {
        PcyId = pcyId;
    }

    public String getCstId() {
        return CstId;
    }

    public void setCstId(String cstId) {
        CstId = cstId;
    }

    public String getConsltTypeId() {
        return ConsltTypeId;
    }

    public void setConsltTypeId(String consltTypeId) {
        ConsltTypeId = consltTypeId;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public void setFileName() {
        FileName = CstId + "_consult.jpg";
    }
}
