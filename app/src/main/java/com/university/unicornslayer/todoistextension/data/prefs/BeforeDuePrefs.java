package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

import javax.inject.Inject;

public class BeforeDuePrefs implements RelativeToNowPrefsProvider {
    private final Context context;
    private final SharedPreferences sharedPrefs;
    private final PrefsUtils prefsUtils;

    @Inject
    public BeforeDuePrefs(Context context, SharedPreferences sharedPrefs, PrefsUtils prefsUtils) {
        this.context = context;
        this.sharedPrefs = sharedPrefs;
        this.prefsUtils = prefsUtils;
    }

    @Override
    public long getIntervalMin() {
        // Default should be AtDue here
        return prefsUtils.getMins(
            context.getString(R.string.prefs_before_due_interval_min_key),
            R.string.default_mins_remind_at_due);
    }

    @Override
    public long getIntervalMax() {
        return prefsUtils.getMins(
            context.getString(R.string.prefs_before_due_interval_max_key),
            R.string.default_mins_remind_before_due);
    }

    @Override
    public boolean produceSound() {
        return sharedPrefs.getBoolean(
            context.getString(R.string.prefs_before_due_produce_sound_key),
            context.getResources().getBoolean(R.bool.default_before_due_make_sound));
    }
}
