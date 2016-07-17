package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GlobalCategoriesDataModel {

    @SerializedName("id")
    private Integer mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("transactions")
    private List<ExpenseData> mTransactions;

    public Integer getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<ExpenseData> getTransactions() {
        return mTransactions;
    }

}
