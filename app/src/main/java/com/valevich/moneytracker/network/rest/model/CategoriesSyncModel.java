package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NotePad.by on 14.06.2016.
 */
public class CategoriesSyncModel {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<CategoryData> data = new ArrayList<CategoryData>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CategoryData> getData() {
        return data;
    }

    public void setData(List<CategoryData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return status + "|||||||||||" + data;
    }
}
