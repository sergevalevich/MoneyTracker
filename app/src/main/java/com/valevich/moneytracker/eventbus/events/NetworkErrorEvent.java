package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 14.07.2016.
 */
public class NetworkErrorEvent {
    private String mMessage;

    public NetworkErrorEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
