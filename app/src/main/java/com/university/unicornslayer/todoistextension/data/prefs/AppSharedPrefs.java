package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPrefs {
    private static SharedPreferences prefs = null;

    public static SharedPreferences getSharedPrefs(Context context) {
        if (prefs == null)
            prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs;
    }
}
