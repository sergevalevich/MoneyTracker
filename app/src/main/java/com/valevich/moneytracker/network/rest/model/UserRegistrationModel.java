package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class UserRegistrationModel {

    @SerializedName("status")
    private String mStatus;

    @SerializedName("id")
    private Integer mId;

    @SerializedName("code")
    int mCode;

    public String getStatus() {
        return mStatus;
    }

    public Integer getId() {
        return mId;
    }

}


