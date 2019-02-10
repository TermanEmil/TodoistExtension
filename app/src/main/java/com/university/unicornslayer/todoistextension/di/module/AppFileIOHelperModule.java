package com.university.unicornslayer.todoistextension.di.module;

import com.university.unicornslayer.todoistextension.utils.files.AppFileIOHelper;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class AppFileIOHelperModule {
    @Binds
    abstract FileIOHelper bindFileIOHelper(AppFileIOHelper target);
}
