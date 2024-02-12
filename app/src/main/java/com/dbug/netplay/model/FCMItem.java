package com.dbug.netplay.model;

public class FCMItem {
    public String fcm_token;
    public int id;

    public FCMItem(String fcm_token, int id) {
        this.fcm_token = fcm_token;
        this.id = id;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
