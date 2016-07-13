package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 20.06.2016.
 */
public class AddedCategoryModel {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    CategoryData data;
    @SerializedName("code")
    int code;

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public CategoryData getData() {
        return data;
    }
}
