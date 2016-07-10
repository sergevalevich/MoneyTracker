package com.valevich.moneytracker.eventbus.events;

/**
 * Created by User on 09.07.2016.
 */
public class SyncFinishedEvent {
    private boolean mStopAfterSync;

    public SyncFinishedEvent(boolean stopAfterSync) {
        mStopAfterSync = stopAfterSync;
    }

    public boolean isStopAfterSync() {
        return mStopAfterSync;
    }
}
