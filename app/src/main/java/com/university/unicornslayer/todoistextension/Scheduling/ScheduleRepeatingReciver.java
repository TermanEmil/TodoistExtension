package com.university.unicornslayer.todoistextension.Scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.university.unicornslayer.todoistextension.ReminderManager.Reminder;
import com.university.unicornslayer.todoistextension.ReminderManager.ReminderManager;

public class ScheduleRepeatingReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("Recived", "-------------------------------");
        ReminderManager reminderManager = new ReminderManager(context);
        reminderManager.checkNotifications();
    }
}
