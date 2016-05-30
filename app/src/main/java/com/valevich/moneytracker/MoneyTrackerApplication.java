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
    public static boolean isGoogleTokenExist() {
        return mPreferences.googleToken().exists();
    }

    public static void saveLoftApiToken(String token) {
        mPreferences.loftApiToken().put(token);
    }
    public static boolean isLoftTokenExist() {
        return mPreferences.loftApiToken().exists();
    }

    public static void saveUserInfo(String name, String email, String picture) {
        mPreferences.edit()
                .userFullName().put(name)
                .userEmail().put(email)
                .userPhoto().put(picture)
                .apply();
    }

    public static String getUserFullName() {
        return mPreferences.userFullName().get();
    }

    public static String getUserEmail() {
        return mPreferences.userEmail().get();
    }

    public static String getUserPhoto() {
        return mPreferences.userPhoto().get();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(base);
        }
    }
}
