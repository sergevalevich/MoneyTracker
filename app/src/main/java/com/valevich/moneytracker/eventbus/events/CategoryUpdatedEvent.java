package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 21.06.2016.
 */
public class CategoryUpdatedEvent {
    private String mNewName;

    private int mId;

    public CategoryUpdatedEvent(String newName, int id) {
        mNewName = newName;
        mId = id;
    }

    public String getNewName() {
        return mNewName;
    }

    public int getId() {
        return mId;
    }
}
