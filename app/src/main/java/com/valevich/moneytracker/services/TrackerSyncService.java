package com.valevich.moneytracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

/**
 * Created by NotePad.by on 11.06.2016.
 */
@EService
public class TrackerSyncService extends Service {
//    private static final Object sSyncAdapterLock = new Object();
//    private static TrackerSyncAdapter sTrackerSyncAdapter = null;
//    @Override
//    public void onCreate() {
//        synchronized (sSyncAdapterLock) {
//            if (sTrackerSyncAdapter == null) {// TODO: 28.06.2016 Не забыть
//                sTrackerSyncAdapter=new TrackerSyncAdapter(getApplicationContext());
//            }
//        }
//    }
    @Bean
    TrackerSyncAdapter sTrackerSyncAdapter;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sTrackerSyncAdapter.getSyncAdapterBinder();
    }
}
