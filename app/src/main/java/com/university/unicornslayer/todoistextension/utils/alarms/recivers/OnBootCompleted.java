package com.university.unicornslayer.todoistextension.utils.alarms.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.university.unicornslayer.todoistextension.utils.alarms.AppScheduleManager;
import com.university.unicornslayer.todoistextension.utils.alarms.ScheduleManager;

public class OnBootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null)
            return;

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ScheduleManager scheduleManager = new AppScheduleManager(context);
            scheduleManager.setRepeatingAlarm();
        }
    }
}
