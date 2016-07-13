package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 16.06.2016.
 */
public class GlobalCategoriesDataModel {
    @SerializedName("id")
    private Integer id;

    @SerializedName("title")
    private String title;

    @SerializedName("transactions")
    private List<ExpenseData> transactions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ExpenseData> getTransactions() {
        return transactions;
    }

}
