package com.valevich.moneytracker.model;

/**
 * Created by NotePad.by on 20.04.2016.
 */
public class Expense {

    private String mDescription;
    private String mPrice;

    public Expense(String description, String price) {
        mDescription = description;
        mPrice = price;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPrice() {
        return mPrice;
    }
}
