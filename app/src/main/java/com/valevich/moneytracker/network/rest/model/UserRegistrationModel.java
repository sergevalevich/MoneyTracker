package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class UserRegistrationModel {

    @SerializedName("status")
    private String status;
    @SerializedName("id")
    private Integer id;
    @SerializedName("code")
    int code;

    public int getCode() {
        return code;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}


