package com.university.unicornslayer.todoistextension.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.university.unicornslayer.todoistextension.R;

import java.util.Objects;

public class AppPrefHelper implements TokenPrefHelper {
    private static final String keyToken = "token";

    private static AppPrefHelper instance;

    private final SharedPreferences sharedPrefs;
    private final Context context;
    private final PrefsUtils prefsUtils;

    public static AppPrefHelper getInstance(Context context) {
        if (instance == null)
            instance = new AppPrefHelper(context);

        return instance;
    }

    public AppPrefHelper(Context context) {
        this.context = context;
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.prefsUtils = new PrefsUtils(context, sharedPrefs);
    }

    /*
    ** TokenPrefHelper
     */

    @Override
    public String getToken() {
        return sharedPrefs.getString(keyToken, null);
    }

    @Override
    public void setToken(String token) {
        sharedPrefs.edit().putString(keyToken, token).apply();
    }

    /*
    ** RemindersPrefHelper
     */

//    @Override
//    public int getIntervalMax() {
//        return getMin("remindBeforeDue", R.string.default_mins_remind_before_due);
//    }
//
//    @Override
//    public int getIntervalMin() {
//        return getMin("remindAtDue", R.string.default_mins_remind_at_due);
//    }
//
//    // How much time due can be late before it's considered unfinished
//    public int getDueCanBeLate() {
//        return sharedPrefs.getInt("dueCanBeLate", 1000 * 60 * 5);
//    }
//
//    public boolean produceSound() {
//        return sharedPrefs.getBoolean(
//            "produceSoundBeforeDue",
//            context.getResources().getBoolean(R.bool.default_before_due_make_sound));
//    }
//
//    public boolean getProduceSoundAtDue() {
//        return sharedPrefs.getBoolean(
//            "produceSoundAtDue",
//            context.getResources().getBoolean(R.bool.default_at_due_make_sound));
//    }
//
//    public int getMaxNbOfRemindersToShowAfterDue() {
//        return sharedPrefs.getInt("maxNbOfRemindersToShowAfterDue", 5);
//    }
//
//    public int getIntervalRemindAfterDue() {
//        return this.getSharedPreferences().getInt(
//            "intervalRemindAfterDue",
//            1000 * 60 * 60 * 24);
//    }
//
//    public boolean getDoRemindAboutUnfinishedTasks() {
//        return this.getSharedPreferences().getBoolean(
//            "doRemindAboutUnfinishedTasks",
//            getResources().getBoolean(R.bool.default_do_remind_about_unfinished));
//    }
//
//    public int getMaxContentSizeForShortDisplay() {
//        return this.getSharedPreferences().getInt(
//            "maxContentSizeForShortDisplay",
//            20);
//    }
//
//    public int getNetworkCheckInterval() {
//        return getMin("networkCheckInterval", R.string.default_mins_network_check);
//    }
}
