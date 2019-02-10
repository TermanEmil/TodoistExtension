package com.university.unicornslayer.todoistextension.di.module;

import com.university.unicornslayer.todoistextension.data.local.AppLocalDataManager;
import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;
import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.network.AppApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.AppTokenPrefHelper;
import com.university.unicornslayer.todoistextension.data.prefs.BeforeDuePrefs;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;
import com.university.unicornslayer.todoistextension.ui.main.MainActivity;
import com.university.unicornslayer.todoistextension.ui.main.MainMvpView;
import com.university.unicornslayer.todoistextension.utils.alarms.AppScheduleManager;
import com.university.unicornslayer.todoistextension.utils.alarms.ScheduleManager;
import com.university.unicornslayer.todoistextension.utils.files.AppFileIOHelper;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;

import dagger.Binds;
import dagger.Module;

@Module
public interface AppBindingsModule {
    @Binds
    FileIOHelper bindFileIOHelper(AppFileIOHelper t);

    @Binds
    LocalDataManager bindLocalDataManager(AppLocalDataManager t);

    @Binds
    TokenPrefHelper bindTokenPrefHelper(AppTokenPrefHelper t);

    @Binds
    ScheduleManager bindScheduleManager(AppScheduleManager t);
}
