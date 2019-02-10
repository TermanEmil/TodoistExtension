package com.university.unicornslayer.todoistextension.di.component;

import com.university.unicornslayer.todoistextension.di.module.ActivityModule;
import com.university.unicornslayer.todoistextension.di.module.AppFileIOHelperModule;
import com.university.unicornslayer.todoistextension.utils.files.AppFileIOHelper;
import com.university.unicornslayer.todoistextension.utils.files.FileIOHelper;

import dagger.Component;

@Component(modules = { ActivityModule.class, AppFileIOHelperModule.class })
public interface AppFileIOHelperComponent {
    FileIOHelper getFileIOHelper();
}
