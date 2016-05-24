package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class UserRegistrationModel {

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_LOGIN_BUSY = "Login busy already";

    @SerializedName("status")
    private String status;
    @SerializedName("id")
    private Integer id;


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


