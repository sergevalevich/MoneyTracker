package com.valevich.moneytracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.valevich.moneytracker.network.sync.TrackerAuthenticator;

import org.androidannotations.annotations.EService;

/**
 * Created by NotePad.by on 11.06.2016.
 */
@EService
public class TrackerAuthenticatorService extends Service {
    private TrackerAuthenticator mTrackerAuthenticator;
    @Override
    public void onCreate() {
        mTrackerAuthenticator = new TrackerAuthenticator(this);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}