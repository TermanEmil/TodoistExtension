package com.university.unicornslayer.todoistextension.di.module;

import com.university.unicornslayer.todoistextension.data.local.AppLocalDataManager;
import com.university.unicornslayer.todoistextension.data.local.LocalDataManager;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AppLocalDataManagerModule {
    @Binds
    abstract LocalDataManager bindLocalDataManager(AppLocalDataManager target);
}
