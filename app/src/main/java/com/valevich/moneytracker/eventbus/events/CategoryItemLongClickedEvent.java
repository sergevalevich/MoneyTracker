package com.valevich.moneytracker.eventbus.events;


public class CategoryItemLongClickedEvent {

    private int mPosition;

    public CategoryItemLongClickedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
