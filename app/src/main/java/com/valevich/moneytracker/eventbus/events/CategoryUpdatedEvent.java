package com.valevich.moneytracker.eventbus.events;

public class CategoryUpdatedEvent {

    private String mTitle;

    private int mId;

    public CategoryUpdatedEvent(String title, int id) {
        mTitle = title;
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getId() {
        return mId;
    }
}
