package com.valevich.moneytracker.eventbus.events;

public class ExpenseItemLongClickedEvent {

    private int mPosition;

    public ExpenseItemLongClickedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
