package com.university.unicornslayer.todoistextension.di.component;

import com.university.unicornslayer.todoistextension.di.module.AppModule;
import com.university.unicornslayer.todoistextension.ui.main.MainActivity;
import com.university.unicornslayer.todoistextension.ui.main.MainMvpView;
import com.university.unicornslayer.todoistextension.ui.main.MainPresenter;
import com.university.unicornslayer.todoistextension.ui.settings.SettingsActivity;
import com.university.unicornslayer.todoistextension.ui.token_input.TokenInputPresenter;
import com.university.unicornslayer.todoistextension.utils.alarms.recivers.OnBootCompleted;
import com.university.unicornslayer.todoistextension.utils.alarms.recivers.ScheduleAlarmReceiver;
import com.university.unicornslayer.todoistextension.utils.app_updates.AppUpdater;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {
    ReminderManager getReminderManager();
    AppUpdater getAppUpdater();

    // Presenters
    MainPresenter getMainPresenter();
    TokenInputPresenter getTokenInputPresenter();

    // Method injects
    void inject(MainActivity t);
    void inject(ScheduleAlarmReceiver t);
    void inject(OnBootCompleted t);
    void inject(SettingsActivity t);
}
