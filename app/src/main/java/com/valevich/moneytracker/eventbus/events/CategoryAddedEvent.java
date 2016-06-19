package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 20.06.2016.
 */
public class CategoryAddedEvent {
    private String mTitle;

    public CategoryAddedEvent(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
