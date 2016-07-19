package com.valevich.moneytracker.utils;

import com.valevich.moneytracker.R;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface Preferences {
    @DefaultString("")
    String loftApiToken();

    @DefaultString("")
    String googleToken();

    @DefaultString("")
    String userFullName();

    @DefaultString("")
    String userEmail();

    @DefaultString("")
    String userPhoto();

    @DefaultString("")
    String userPassword();

    @DefaultBoolean(value = true, keyRes = R.string.pref_enable_notifications_key)
    boolean notificationPreference();

    @DefaultBoolean(value = true,keyRes = R.string.pref_enable_indicator_key)
    boolean indicatorPreference();

    @DefaultBoolean(value = true,keyRes = R.string.pref_enable_vibration_key)
    boolean vibrationPreference();

    @DefaultBoolean(value = true,keyRes = R.string.pref_enable_sound_key)
    boolean soundPreference();

    @DefaultInt(value = ConstantsManager.DEFAULT_SYNC_INTERVAL, keyRes = R.string.pref_sync_interval_key)
    int syncIntervalPreference();

    @DefaultBoolean(value = false)
    boolean isLoginFinished();

    @DefaultBoolean(value = false)
    boolean isLogoutFinished();

    @DefaultBoolean(value = false)
    boolean isSyncFinished();

    @DefaultBoolean(value = false)
    boolean isSignUpFinished();

    @DefaultBoolean(value = false)
    boolean isNetworkError();

    @DefaultString("")
    String errorMessage();

}
