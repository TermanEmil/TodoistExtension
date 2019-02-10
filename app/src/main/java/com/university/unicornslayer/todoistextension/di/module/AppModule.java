package com.university.unicornslayer.todoistextension.di.module;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.university.unicornslayer.todoistextension.R;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.network.AppApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.AppSharedPrefs;
import com.university.unicornslayer.todoistextension.data.prefs.AtDuePrefs;
import com.university.unicornslayer.todoistextension.data.prefs.BeforeDuePrefs;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.AppReminderManager;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;
import com.university.unicornslayer.todoistextension.utils.reminder.agents.RemindBeforeDueAgent;
import com.university.unicornslayer.todoistextension.utils.reminder.agents.RemindAtDueAgent;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = AppBindingsModule.class)
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Provides @Named("localDataManagerFileName")
    String provideLocalDataManagerFileName() {
        return provideContext().getString(R.string.reminders_data_filename);
    }

    @Provides @Singleton
    SharedPreferences provideSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides @Singleton
    AlarmManager provideAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Provides @Singleton
    ApiHelper provideApiHelper(TokenPrefHelper tokenPrefHelper) {
        ApiHelper result = new AppApiHelper();
        if (tokenPrefHelper.getToken() != null)
            result.setToken(tokenPrefHelper.getToken());

        return result;
    }

    @Provides
    RemindBeforeDueAgent provideRemindBeforeDueAgent(BeforeDuePrefs prefs, TodoistNotifHelper notifHelper) {
        return new RemindBeforeDueAgent(prefs, notifHelper);
    }

    @Provides
    RemindAtDueAgent provideRemindAtDueAgent(AtDuePrefs prefs, TodoistNotifHelper notifHelper) {
        return new RemindAtDueAgent(prefs, notifHelper);
    }

    @Provides
    ReminderManager provideReminderManager(
        AppReminderManager appReminderManager,
        RemindBeforeDueAgent beforeDueAgent,
        RemindAtDueAgent atDueAgent
    ) {
        appReminderManager.addReminderAgent(beforeDueAgent);
        appReminderManager.addReminderAgent(atDueAgent);
        return appReminderManager;
    }
}
