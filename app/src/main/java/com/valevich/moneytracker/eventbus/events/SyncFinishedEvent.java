package com.valevich.moneytracker.eventbus.events;

public class SyncFinishedEvent {
    private boolean mIsSyncBeforeExit;

    public SyncFinishedEvent(boolean isSyncBeforeExit) {
        mIsSyncBeforeExit = isSyncBeforeExit;
    }

    public boolean isSyncBeforeExit() {
        return mIsSyncBeforeExit;
    }

}
