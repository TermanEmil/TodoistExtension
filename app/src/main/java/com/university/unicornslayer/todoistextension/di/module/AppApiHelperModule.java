package com.university.unicornslayer.todoistextension.di.module;

import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.network.AppApiHelper;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class AppApiHelperModule {
    @Binds
    abstract ApiHelper bindApiHelper(AppApiHelper target);
}
