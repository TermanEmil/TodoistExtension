package com.university.unicornslayer.todoistextension.utils.alarms.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.university.unicornslayer.todoistextension.di.component.AppComponent;
import com.university.unicornslayer.todoistextension.di.component.DaggerAppComponent;
import com.university.unicornslayer.todoistextension.di.module.AppModule;
import com.university.unicornslayer.todoistextension.utils.alarms.AppScheduleManager;
import com.university.unicornslayer.todoistextension.utils.alarms.ScheduleManager;

import javax.inject.Inject;

public class OnBootCompleted extends BroadcastReceiver {
    @Inject
    ScheduleManager scheduleManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            return;

        DaggerAppComponent.builder()
            .appModule(new AppModule(context))
            .build()
            .inject(this);

        scheduleManager.setRepeatingAlarm();
    }
}
