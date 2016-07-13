package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 09.07.2016.
 */
public class SyncFinishedEvent {
    private boolean mIsSyncBeforeExit;

    private boolean mIsSuccessful;

    public SyncFinishedEvent(boolean stopAfterSync, boolean isSuccessful) {
        mIsSyncBeforeExit = stopAfterSync;
        mIsSuccessful = isSuccessful;
    }

    public boolean isSyncBeforeExit() {
        return mIsSyncBeforeExit;
    }

    public boolean isSuccessful() {
        return mIsSuccessful;
    }
}
