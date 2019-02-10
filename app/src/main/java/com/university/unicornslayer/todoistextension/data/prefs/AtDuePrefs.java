package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

import javax.inject.Inject;

public class AtDuePrefs implements RelativeToNowPrefsProvider {
    private final Context context;
    private final SharedPreferences sharedPrefs;
    private final PrefsUtils prefsUtils;

    @Inject
    public AtDuePrefs(Context context, SharedPreferences sharedPrefs, PrefsUtils prefsUtils) {
        this.context = context;
        this.sharedPrefs = sharedPrefs;
        this.prefsUtils = prefsUtils;
    }

    @Override
    public long getIntervalMin() {
        return 0;
    }

    @Override
    public long getIntervalMax() {
        return prefsUtils.getMins(
            context.getString(R.string.prefs_at_due_interval_max_key),
            R.string.default_mins_remind_at_due);
    }

    @Override
    public boolean produceSound() {
        return sharedPrefs.getBoolean(
            context.getString(R.string.prefs_at_due_produce_sound_key),
            context.getResources().getBoolean(R.bool.default_at_due_make_sound));
    }
}
