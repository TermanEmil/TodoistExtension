package com.university.unicornslayer.todoistextension.utils.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.university.unicornslayer.todoistextension.data.prefs.NetworkUpdatePrefsProvider;
import com.university.unicornslayer.todoistextension.utils.alarms.recivers.ScheduleAlarmReceiver;

import java.util.Calendar;

import javax.inject.Inject;

public class AppScheduleManager implements ScheduleManager {
    private final static String TAG = "ScheduleManager";

    private final AlarmManager alarmManager;
    private final Context context;
    private final NetworkUpdatePrefsProvider networkUpdatePrefsProvider;
    private boolean repeatingAlarmIsSet;

    @Inject
    public AppScheduleManager(
        Context context,
        AlarmManager alarmManager,
        NetworkUpdatePrefsProvider networkUpdatePrefsProvider
    ) {
        this.alarmManager = alarmManager;
        this.context = context;
        this.networkUpdatePrefsProvider = networkUpdatePrefsProvider;
    }

    @Override
    public void setRepeatingAlarm()
    {
        int repeatingInterval = networkUpdatePrefsProvider.getNetworkCheckInterval();
        if (repeatingAlarmIsSet || repeatingInterval < 0)
            return;

        Intent intent = new Intent(context, ScheduleAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
            context,
            12,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000 * 5,
            repeatingInterval, pi);
        repeatingAlarmIsSet = true;

        Log.i(TAG, String.format("Repeating broadcast set. Interval = %f", repeatingInterval / 1000.0));
    }

    @Override
    public void setExactAlarm(long localTargetTime) {
        Intent intent = new Intent(context, ScheduleAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
            context,
            1,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT);

        long targetTime = localTargetTime + getLocalTimeDiff();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetTime, pi);

        Log.i(TAG, String.format("Exact alarm set for %d", localTargetTime));
    }

    private long getLocalTimeDiff() {
        return Calendar.getInstance().getTimeInMillis() - System.currentTimeMillis();
    }
}
