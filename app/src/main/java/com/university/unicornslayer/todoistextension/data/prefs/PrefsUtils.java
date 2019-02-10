package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

import javax.inject.Inject;

public class PrefsUtils {
    private final Context context;
    private final SharedPreferences sharedPrefs;

    @Inject
    public PrefsUtils(Context context, SharedPreferences sharedPrefs) {
        this.context = context;
        this.sharedPrefs = sharedPrefs;
    }

    public int getMins(String prefKey, int defaultValueStrId) {
        final String pref = sharedPrefs.getString(prefKey, context.getString(defaultValueStrId));
        return Integer.parseInt(Objects.requireNonNull(pref)) * 1000 * 60;
    }
}
