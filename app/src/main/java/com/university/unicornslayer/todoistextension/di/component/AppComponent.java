package com.university.unicornslayer.todoistextension.di.component;

import android.content.Context;

import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.BeforeDuePrefs;
import com.university.unicornslayer.todoistextension.data.prefs.PrefsUtils;
import com.university.unicornslayer.todoistextension.di.module.AppModule;
import com.university.unicornslayer.todoistextension.ui.main.MainActivity;
import com.university.unicornslayer.todoistextension.ui.main.MainPresenter;
import com.university.unicornslayer.todoistextension.ui.token_input.TokenInputPresenter;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.ReminderManager;
import com.university.unicornslayer.todoistextension.utils.reminder.agent.RemindBeforeDueAgent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {
    ReminderManager getReminderManager();

    /*
    ** Presenters
     */

    MainPresenter getMainPresenter();
    TokenInputPresenter getTokenInputPresenter();
}
