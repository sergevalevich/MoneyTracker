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

    public Integer getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}
