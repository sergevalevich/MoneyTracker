package com.valevich.moneytracker;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.valevich.moneytracker.utils.Preferences_;
import com.valevich.moneytracker.utils.ReleaseTree;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import timber.log.Timber;

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

        //DbFlow
        FlowManager.init(new FlowConfig.Builder(this)
                .openDatabasesOnInit(true).build());

        //Timber
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
            //releaseTree
            Timber.plant(new ReleaseTree());
        }
        //LeakCanary
        LeakCanary.install(this);
    }

    public static void saveGoogleToken(String token) {
        mPreferences.googleToken().put(token);
    }
    public static boolean isGoogleTokenExist() {
        return !mPreferences.googleToken().get().equals("");
    }
    public static String getGoogleToken() {
        return mPreferences.googleToken().get();
    }

    public static void saveLoftApiToken(String token) {
        mPreferences.loftApiToken().put(token);
    }
    public static boolean isLoftTokenExist() {
        return !mPreferences.loftApiToken().get().equals("");
    }
    public static String getLoftApiToken() {
        return mPreferences.loftApiToken().get();
    }


    public static void saveUserInfo(String name, String email, String picture, String password) {
        mPreferences.edit()
                .userFullName().put(name)
                .userEmail().put(email)
                .userPhoto().put(picture)
                .userPassword().put(password)
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

    public static String getUserPassword() {
        return mPreferences.userPassword().get();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(base);
        }
    }
}
