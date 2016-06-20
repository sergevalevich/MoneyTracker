package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 16.06.2016.
 */
public class UserLogoutModel {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }
}
