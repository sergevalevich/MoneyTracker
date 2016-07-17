package com.valevich.moneytracker.eventbus.events;

public class CategoryAddedEvent {

    private String mTitle;

    public CategoryAddedEvent(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
