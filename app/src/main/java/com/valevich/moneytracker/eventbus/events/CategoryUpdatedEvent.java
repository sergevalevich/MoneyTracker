package com.valevich.moneytracker.eventbus.events;

import com.valevich.moneytracker.database.data.CategoryEntry;

/**
 * Created by User on 21.06.2016.
 */
public class CategoryUpdatedEvent {

    private CategoryEntry mCategory;

    public CategoryUpdatedEvent(CategoryEntry category) {
        mCategory = category;
    }

    public String getNewName() {
        String name = "";
        if (mCategory != null) name = mCategory.getName();
        return name;
    }

    public int getId() {
        int id = 0;
        if (mCategory != null) id = mCategory.getServerId();
        return id;
    }
}
