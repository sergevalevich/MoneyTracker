package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;


public class AddedCategoryModel {

    @SerializedName("status")
    String mStatus;

    @SerializedName("data")
    CategoryData mData;

    @SerializedName("code")
    int mCode;

    public String getStatus() {
        return mStatus;
    }

    public CategoryData getData() {
        return mData;
    }
}
