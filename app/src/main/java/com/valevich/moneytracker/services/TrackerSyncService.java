package com.valevich.moneytracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;

/**
 * Created by NotePad.by on 11.06.2016.
 */
public class TrackerSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TrackerSyncAdapter sTrackerSyncAdapter = null;
    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sTrackerSyncAdapter == null) {
                sTrackerSyncAdapter=new TrackerSyncAdapter(getApplicationContext(), true);
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sTrackerSyncAdapter.getSyncAdapterBinder();
    }
}
