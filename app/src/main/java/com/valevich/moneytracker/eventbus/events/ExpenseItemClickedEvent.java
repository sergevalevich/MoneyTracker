package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 05.07.2016.
 */
public class ExpenseItemClickedEvent {

    private int mPosition;

    public ExpenseItemClickedEvent(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }
}
