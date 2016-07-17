package com.valevich.moneytracker.eventbus.events;

public class ExpenseItemClickedEvent {

    private int mPosition;

    public ExpenseItemClickedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
