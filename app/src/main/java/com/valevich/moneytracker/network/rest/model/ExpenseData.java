package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class ExpenseData {

    @SerializedName("id")
    private Integer mId;

    @SerializedName("category_id")
    private Integer mCategoryId;

    @SerializedName("comment")
    private String mComment;

    @SerializedName("sum")
    private Double mSum;

    @SerializedName("tr_date")
    private String mDate;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public void setCategoryId(Integer category_id) {
        mCategoryId = category_id;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public Double getSum() {
        return mSum;
    }

    public void setSum(Double sum) {
        mSum = sum;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

}
