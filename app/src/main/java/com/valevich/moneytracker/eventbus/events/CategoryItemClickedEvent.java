package com.valevich.moneytracker.eventbus.events;


public class CategoryItemClickedEvent {

    private int mPosition;

    public CategoryItemClickedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
