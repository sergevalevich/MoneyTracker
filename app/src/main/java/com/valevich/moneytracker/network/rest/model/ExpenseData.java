package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NotePad.by on 11.06.2016.
 */
public class ExpenseData {

    @SerializedName("id")
    private Integer id;

    @SerializedName("category_id")
    private Integer category_id;

    @SerializedName("comment")
    private String comment;

    @SerializedName("sum")
    private Double sum;

    @SerializedName("tr_date")
    private String tr_date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategoryId(Integer category_id) {
        this.category_id = category_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getTrDate() {
        return tr_date;
    }

    public void setTrDate(String trDate) {
        this.tr_date = trDate;
    }

}
