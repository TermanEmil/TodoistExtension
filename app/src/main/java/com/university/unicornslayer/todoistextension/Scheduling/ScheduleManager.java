package com.university.unicornslayer.todoistextension.Scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import com.university.unicornslayer.todoistextension.DataStuff.SharedPrefsUtils;

import java.util.Calendar;

public class ScheduleManager extends ContextWrapper {
    private final AlarmManager alarmManager;
    private final SharedPrefsUtils sharedPrefsUtils;

    private boolean repeatingAlarmIsSet = false;

    public ScheduleManager(Context context) {
        super(context);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sharedPrefsUtils = new SharedPrefsUtils(this);
    }

    public void setRepeatingAlarm()
    {
        int repeatingInterval = sharedPrefsUtils.getNetworkCheckInterval();
        if (repeatingAlarmIsSet || repeatingInterval < 0)
            return;

        Intent intent = new Intent(this, ScheduleRepeatingReciver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
            this,
            10,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            repeatingInterval, pi);
        repeatingAlarmIsSet = true;
    }

    public void setExactAlarm(long localTargetTime) {
        Intent intent = new Intent(this, ScheduleExactReciver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
            this,
            1,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT);

        long targetTime = localTargetTime + getLocalTimeDiff();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetTime, pi);
    }

    private long getLocalTimeDiff() {
        return Calendar.getInstance().getTimeInMillis() - System.currentTimeMillis();
    }
}
