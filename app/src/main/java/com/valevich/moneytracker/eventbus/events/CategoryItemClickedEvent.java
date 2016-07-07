package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 05.07.2016.
 */
public class CategoryItemClickedEvent {

    private int mPosition;

    public CategoryItemClickedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
