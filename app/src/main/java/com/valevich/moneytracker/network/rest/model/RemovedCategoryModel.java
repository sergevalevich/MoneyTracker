package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 19.06.2016.
 */
public class RemovedCategoryModel {
    @SerializedName("data")
    Integer data;
    @SerializedName("status")
    String status;

    @SerializedName("code")
    int code;

    public int getCode() {
        return code;
    }

    public Integer getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}
