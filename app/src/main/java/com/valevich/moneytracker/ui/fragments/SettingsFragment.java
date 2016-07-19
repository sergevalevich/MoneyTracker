package com.valevich.moneytracker.ui.fragments;


import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.sync.TrackerSyncAdapter;
import com.valevich.moneytracker.utils.Preferences_;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

@PreferenceScreen(R.xml.pref_general)
@EFragment(R.layout.fragment_settings)
public class SettingsFragment
        extends PreferenceFragmentCompat {

    @Pref
    Preferences_ mPreferences;

    @PreferenceByKey(R.string.pref_sync_interval_key)
    ListPreference mSyncIntervalPreference;

    @PreferenceByKey(R.string.pref_enable_notifications_key)
    SwitchPreferenceCompat mNotificationPreference;

    @PreferenceByKey(R.string.pref_enable_sound_key)
    SwitchPreferenceCompat mSoundPreference;

    @PreferenceByKey(R.string.pref_enable_vibration_key)
    SwitchPreferenceCompat mVibrationPreference;

    @PreferenceByKey(R.string.pref_enable_indicator_key)
    SwitchPreferenceCompat mIndicatorPreference;

    @Bean
    TrackerSyncAdapter mTrackerSyncAdapter;

    @PreferenceChange(R.string.pref_sync_interval_key)
    void onSyncIntervalPreferenceChanged(int interval, ListPreference listPreference) {
        mPreferences.syncIntervalPreference().put(interval);
        int prefIndex = listPreference.findIndexOfValue(String.valueOf(interval));
        if (prefIndex >= 0) listPreference.setSummary(listPreference.getEntries()[prefIndex]);
        mTrackerSyncAdapter.configureSync();
    }

    @PreferenceChange(R.string.pref_enable_notifications_key)
    void onNotificationPreferenceChanged(boolean isEnabled,SwitchPreferenceCompat switchPreference) {
        mPreferences.notificationPreference().put(isEnabled);
        togglePrefs(isEnabled);
    }

    @PreferenceChange(R.string.pref_enable_sound_key)
    void onSoundPreferenceChanged(boolean newValue,SwitchPreferenceCompat switchPreference) {
        mPreferences.soundPreference().put(newValue);
    }

    @PreferenceChange(R.string.pref_enable_vibration_key)
    void onVibrationPreferenceChanged(boolean newValue,SwitchPreferenceCompat switchPreference) {
        mPreferences.vibrationPreference().put(newValue);
    }

    @PreferenceChange(R.string.pref_enable_indicator_key)
    void onIndicatorPreferenceChanged(boolean newValue,SwitchPreferenceCompat switchPreference) {
        mPreferences.indicatorPreference().put(newValue);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {}

    @AfterPreferences
    void initPrefs() {
        onNotificationPreferenceChanged(mPreferences.notificationPreference().get(),
                mNotificationPreference);
        onSyncIntervalPreferenceChanged(mPreferences.syncIntervalPreference().get(),
                mSyncIntervalPreference);
    }

    private void togglePrefs(boolean enabled){
        if(!enabled) {
            setPrefsState(false);
            mVibrationPreference.setChecked(false);
            mSoundPreference.setChecked(false);
            mIndicatorPreference.setChecked(false);
        } else {
            setPrefsState(true);
            boolean vibrationState = mPreferences.vibrationPreference().get();
            boolean soundState = mPreferences.soundPreference().get();
            boolean indicatorState = mPreferences.indicatorPreference().get();
            mVibrationPreference.setChecked(vibrationState);
            mSoundPreference.setChecked(soundState);
            mIndicatorPreference.setChecked(indicatorState);
        }
    }

    private void setPrefsState(boolean state) {
        mSoundPreference.setEnabled(state);
        mIndicatorPreference.setEnabled(state);
        mVibrationPreference.setEnabled(state);
    }
}
