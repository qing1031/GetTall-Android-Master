package com.obenproto.howtall.response;

public class HowTallUser {

    private String message;
    private String phoneId;
    private int userId;

    public String getMessage() {
        return message;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "HowTallUser{" +
                "message='" + message + '\'' +
                ", phoneId='" + phoneId + '\'' +
                ", userId=" + userId +
                '}';
    }
}
