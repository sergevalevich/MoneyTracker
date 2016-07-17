package com.valevich.moneytracker.eventbus.events;

public class NetworkErrorEvent {
    private String mMessage;

    public NetworkErrorEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
