package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

public class BeforeDuePrefs implements RelativeToNowPrefsProvider {
    private static final String MIN_KEY = "before-due-interval-min";
    private static final String MAX_KEY = "before-due-interval-max";
    private static final String SOUND_KEY = "before-due-produce-sound";

    private final Context context;
    private final PrefsUtils prefsUtils;

    public BeforeDuePrefs(Context context) {
        this.context = context;
        this.prefsUtils = new PrefsUtils(context, AppSharedPrefs.getSharedPrefs(context));
    }

    @Override
    public long getIntervalMin() {
        // Default should be AtDue here
        return prefsUtils.getMins(MIN_KEY, R.string.default_mins_remind_at_due);
    }

    @Override
    public long getIntervalMax() {
        return prefsUtils.getMins(MAX_KEY, R.string.default_mins_remind_before_due);
    }

    @Override
    public boolean produceSound() {
        return AppSharedPrefs.getSharedPrefs(context).getBoolean(
            SOUND_KEY,
            context.getResources().getBoolean(R.bool.default_before_due_make_sound));
    }
}
