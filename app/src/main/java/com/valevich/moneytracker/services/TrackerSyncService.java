package com.valevich.moneytracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

@EService
public class TrackerSyncService extends Service {
    @Bean
    TrackerSyncAdapter sTrackerSyncAdapter;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sTrackerSyncAdapter.getSyncAdapterBinder();
    }
}
