package com.university.unicornslayer.todoistextension.Scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootCompleted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ScheduleManager scheduleManager = new ScheduleManager(context);
            scheduleManager.setRepeatingAlarm();
        }
    }
}
