package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

public class AtDuePrefs implements RelativeToNowPrefsProvider {
    private static final String MAX_KEY = "at-due-interval-max";
    private static final String SOUND_KEY = "at-due-produce-sound";

    private final Context context;
    private final PrefsUtils prefsUtils;

    public AtDuePrefs(Context context) {
        this.context = context;
        this.prefsUtils = new PrefsUtils(context, AppSharedPrefs.getSharedPrefs(context));
    }

    @Override
    public long getIntervalMin() {
        return 0;
    }

    @Override
    public long getIntervalMax() {
        return prefsUtils.getMins(MAX_KEY, R.string.default_mins_remind_at_due);
    }

    @Override
    public boolean produceSound() {
        return AppSharedPrefs.getSharedPrefs(context).getBoolean(
            SOUND_KEY,
            context.getResources().getBoolean(R.bool.default_at_due_make_sound));
    }
}
