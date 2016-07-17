package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class UserLogoutModel {

    @SerializedName("status")
    private String mStatus;

    @SerializedName("code")
    int mCode;

    public String getStatus() {
        return mStatus;
    }
}
