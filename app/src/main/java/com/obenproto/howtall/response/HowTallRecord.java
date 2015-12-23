package com.obenproto.howtall.response;

public class HowTallRecord {

    private String actualAge;
    private String actualGender;
    private String actualHeight;
    private String email;
    private String estimatedAge;
    private String estimatedGender;
    private String estimatedHeight;
    private String message;
    private String recordURL;
    private String recordId;
    private String selfieURL;
    private String userId;

    public String getActualAge() {
        return actualAge;
    }

    public String getActualGender() {
        return actualGender;
    }

    public String getActualHeight() {
        return actualHeight;
    }

    public String getEmail() {
        return email;
    }

    public String getEstimatedAge() {
        return estimatedAge;
    }

    public String getEstimatedGender() {
        return estimatedGender;
    }

    public String getEstimatedHeight() {
        return estimatedHeight;
    }

    public String getMessage() {
        return message;
    }

    public String getRecordURL() {
        return recordURL;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getSelfieURL() {
        return selfieURL;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "HowTallRecord{" +
                "actualAge='" + actualAge + '\'' +
                ", actualGender='" + actualGender + '\'' +
                ", actualHeight='" + actualHeight + '\'' +
                ", email='" + email + '\'' +
                ", estimatedAge='" + estimatedAge + '\'' +
                ", estimatedGender='" + estimatedGender + '\'' +
                ", estimatedHeight='" + estimatedHeight + '\'' +
                ", message='" + message + '\'' +
                ", recordURL='" + recordURL + '\'' +
                ", recordId='" + recordId + '\'' +
                ", selfieURL='" + selfieURL + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
