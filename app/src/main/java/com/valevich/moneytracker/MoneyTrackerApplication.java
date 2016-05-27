package com.valevich.moneytracker;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.valevich.moneytracker.utils.Preferences_;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by NotePad.by on 07.05.2016.
 */
@EApplication
public class MoneyTrackerApplication extends Application {
    @Pref
    static Preferences_ mPreferences;
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this)
                .openDatabasesOnInit(true).build());
    }
    public static void saveGoogleToken(String token) {
        mPreferences.googleToken().put(token);
    }
    public static String getGoogleToken() {
        return mPreferences.googleToken().get();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(base);
        }
    }
}
