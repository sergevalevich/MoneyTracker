package com.valevich.moneytracker.network.rest.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NotePad.by on 11.06.2016.
 */
public class ExpensesSyncModel {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<ExpenseData> data = new ArrayList<ExpenseData>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ExpenseData> getData() {
        return data;
    }

    public void setData(List<ExpenseData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return status + "|||||||||||" + data;
    }
}
