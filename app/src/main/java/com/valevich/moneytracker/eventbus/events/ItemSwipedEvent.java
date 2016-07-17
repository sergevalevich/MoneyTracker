package com.valevich.moneytracker.eventbus.events;

public class ItemSwipedEvent {
    private int mItemPosition;

    public ItemSwipedEvent(int itemPosition) {
        mItemPosition = itemPosition;
    }

    public int getItemPosition() {
        return mItemPosition;
    }
}
