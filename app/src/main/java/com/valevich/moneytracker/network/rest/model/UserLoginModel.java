package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class UserLoginModel {

    @SerializedName("status")
    private String mStatus;

    @SerializedName("id")
    private Integer mId;

    @SerializedName("auth_token")
    private String mAuthToken;

    @SerializedName("code")
    int mCode;

    public String getStatus() {
        return mStatus;
    }

    public Integer getId() {
        return mId;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

}
