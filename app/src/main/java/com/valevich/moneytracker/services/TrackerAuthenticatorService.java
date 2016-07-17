package com.valevich.moneytracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.valevich.moneytracker.network.sync.TrackerAuthenticator;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

@EService
public class TrackerAuthenticatorService extends Service {

    @Bean
    TrackerAuthenticator mTrackerAuthenticator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}