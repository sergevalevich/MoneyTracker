package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 14.07.2016.
 */
public class DbErrorEvent {
    private String mMessage;

    public DbErrorEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
