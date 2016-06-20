package com.valevich.moneytracker.eventbus.buses;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

/**
 * Created by User on 17.06.2016.
 */

public class OttoBus extends Bus{
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    OttoBus.super.post(event);
                }
            });
        }
    }
}
