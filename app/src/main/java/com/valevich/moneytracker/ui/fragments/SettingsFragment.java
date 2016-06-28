package com.valevich.moneytracker.ui.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.utils.Preferences_;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

@PreferenceScreen(R.xml.pref_general)
@EFragment(R.layout.fragment_settings)
public class SettingsFragment
        extends PreferenceFragmentCompat {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Pref
    Preferences_ mPreferences;

    @PreferenceByKey(R.string.pref_enable_notifications_key)
    SwitchPreferenceCompat mSwitchPreference;

    @PreferenceChange(R.string.pref_enable_notifications_key)
    void onPreferenceChanged(boolean newValue,SwitchPreferenceCompat switchPreference) {
        switchPreference.setSummary(String.valueOf(newValue));
        mPreferences.notificationPreference().put(newValue);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

}
