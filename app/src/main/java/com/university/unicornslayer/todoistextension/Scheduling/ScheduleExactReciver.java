package com.university.unicornslayer.todoistextension.Scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.university.unicornslayer.todoistextension.ReminderManager.ReminderManager;

public class ScheduleExactReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Recived", "-------------------------------1");
        ReminderManager reminderManager = new ReminderManager(context);
        reminderManager.checkNotifications();
    }
}
