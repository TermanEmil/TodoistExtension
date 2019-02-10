package com.university.unicornslayer.todoistextension.di.component;

import android.content.Context;

import com.university.unicornslayer.todoistextension.di.module.ActivityModule;
import com.university.unicornslayer.todoistextension.ui.main.MainActivity;
import com.university.unicornslayer.todoistextension.ui.token_input.TokenInputActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = { ActivityModule.class })
@Singleton
public interface ActivityComponent {
    Context context();

    void inject(MainActivity mainActivity);
    void inject(TokenInputActivity activity);
}
