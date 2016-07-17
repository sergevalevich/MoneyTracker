package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class RemovedCategoryModel {

    @SerializedName("data")
    Integer mData;

    @SerializedName("status")
    String mStatus;

    @SerializedName("code")
    int mCode;

    public Integer getData() {
        return mData;
    }

    public String getStatus() {
        return mStatus;
    }
}
