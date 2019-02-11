package com.university.unicornslayer.todoistextension.ui.settings.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.ui.settings.SettingsActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends BaseSettingsFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_before_due_interval_max_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_at_due_interval_max_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.prefs_network_check_interval_key)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
