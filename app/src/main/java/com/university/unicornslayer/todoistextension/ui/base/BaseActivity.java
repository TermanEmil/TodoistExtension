package com.university.unicornslayer.todoistextension.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.university.unicornslayer.todoistextension.di.component.AppComponent;
import com.university.unicornslayer.todoistextension.di.component.DaggerAppComponent;
import com.university.unicornslayer.todoistextension.di.module.AppModule;

public abstract class BaseActivity extends AppCompatActivity {
    private AppComponent appComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .build();
    }

    public AppComponent getDagger() {
        return appComponent;
    }
}
