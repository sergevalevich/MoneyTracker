package com.valevich.moneytracker.network.rest.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ExpensesSyncModel {

    @SerializedName("status")
    private String mStatus;

    @SerializedName("data")
    private List<ExpenseData> mData = new ArrayList<>();

    @SerializedName("code")
    int mCode;

    public String getStatus() {
        return mStatus;
    }

    public List<ExpenseData> getData() {
        return mData;
    }

}
