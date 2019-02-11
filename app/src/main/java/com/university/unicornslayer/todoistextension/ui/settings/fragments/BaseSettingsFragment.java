package com.university.unicornslayer.todoistextension.ui.settings.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.university.unicornslayer.todoistextension.di.component.AppComponent;
import com.university.unicornslayer.todoistextension.di.component.DaggerAppComponent;
import com.university.unicornslayer.todoistextension.di.module.AppModule;

import javax.inject.Inject;

public class BaseSettingsFragment extends PreferenceFragment {
    private AppComponent dagger;

    @Inject SharedPreferences sharedPreferences;

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     */

    protected void bindPreferenceSummaryToValue(Preference preference) {
        Preference.OnPreferenceChangeListener listener = new OnPreferenceChangeListener();

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(listener);

        if (dagger == null) {
            dagger = DaggerAppComponent.builder()
                .appModule(new AppModule(preference.getContext()))
                .build();
            dagger.inject(this);
        }

        SharedPreferences sharedPrefs = sharedPreferences;
        String key = preference.getKey();
        Object changedObject;

        if (preference instanceof SwitchPreference)
            changedObject = sharedPrefs.getBoolean(key, false);
        else
            changedObject = sharedPrefs.getString(key, "");

        // Trigger the listener immediately with the preference's current value.
        listener.onPreferenceChange(preference, changedObject);
    }

    private class OnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                    index >= 0
                        ? listPreference.getEntries()[index]
                        : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}
