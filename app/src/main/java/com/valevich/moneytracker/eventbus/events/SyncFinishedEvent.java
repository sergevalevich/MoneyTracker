package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 09.07.2016.
 */
public class SyncFinishedEvent {
    private boolean mIsSyncBeforeExit;

    public SyncFinishedEvent(boolean isSyncBeforeExit) {
        mIsSyncBeforeExit = isSyncBeforeExit;
    }

    public boolean isSyncBeforeExit() {
        return mIsSyncBeforeExit;
    }

}
