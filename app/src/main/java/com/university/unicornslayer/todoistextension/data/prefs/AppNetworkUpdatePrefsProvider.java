package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.university.unicornslayer.todoistextension.R;

import javax.inject.Inject;

public class AppNetworkUpdatePrefsProvider implements NetworkUpdatePrefsProvider {
    private final Context context;
    private final SharedPreferences sharedPrefs;
    private final PrefsUtils prefsUtils;

    @Inject
    public AppNetworkUpdatePrefsProvider(Context context, SharedPreferences sharedPrefs, PrefsUtils prefsUtils) {
        this.context = context;
        this.sharedPrefs = sharedPrefs;
        this.prefsUtils = prefsUtils;
    }

    @Override
    public int getNetworkCheckInterval() {
        return prefsUtils.getMins(
            context.getString(R.string.prefs_network_check_interval_key),
            R.string.default_mins_network_check);
    }
}
