package com.valevich.moneytracker.utils;

import com.valevich.moneytracker.R;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by NotePad.by on 22.05.2016.
 */
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

    @DefaultInt(0)
    int exceptionsCount();
}
