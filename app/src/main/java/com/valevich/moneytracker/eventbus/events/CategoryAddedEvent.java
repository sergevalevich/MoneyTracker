package com.valevich.moneytracker.eventbus.events;

import com.valevich.moneytracker.database.data.CategoryEntry;

/**
 * Created by User on 20.06.2016.
 */
public class CategoryAddedEvent {
    private CategoryEntry mCategory;

    public CategoryAddedEvent(CategoryEntry category) {
        mCategory = category;
    }

    public String getTitle() {
        String title = "";
        if (mCategory != null) title = mCategory.getName();
        return title;
    }
}
