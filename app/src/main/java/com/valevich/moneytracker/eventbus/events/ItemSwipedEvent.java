package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 11.07.2016.
 */
public class ItemSwipedEvent {
    private int mItemPosition;

    public ItemSwipedEvent(int itemPosition) {
        mItemPosition = itemPosition;
    }

    public int getItemPosition() {
        return mItemPosition;
    }
}
