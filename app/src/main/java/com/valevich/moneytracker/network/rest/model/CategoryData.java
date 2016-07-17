package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class CategoryData {

    @SerializedName("id")
    private Integer mId;

    @SerializedName("title")
    private String mTitle;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
