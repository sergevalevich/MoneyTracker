package com.valevich.moneytracker.model;

/**
 * Created by NotePad.by on 20.04.2016.
 */
public class Expense {

    private String mDescription;
    private String mPrice;
    private String mDate;
    private String mCategory;

    public String getDescription() {
        return mDescription;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }
}
