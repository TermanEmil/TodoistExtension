package com.university.unicornslayer.todoistextension.utils.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.university.unicornslayer.todoistextension.utils.alarms.recivers.ScheduleAlarmReciver;
import com.university.unicornslayer.todoistextension.data.SharedPrefsUtils;

import java.util.Calendar;

public class AppScheduleManager implements ScheduleManager {
    private final AlarmManager alarmManager;
    private final SharedPrefsUtils sharedPrefsUtils;
    private final Context context;
    private boolean repeatingAlarmIsSet;

    public AppScheduleManager(Context context) {
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.sharedPrefsUtils = new SharedPrefsUtils(context);
        this.context = context;
    }

    @Override
    public void setRepeatingAlarm()
    {
        int repeatingInterval = sharedPrefsUtils.getNetworkCheckInterval();
        if (repeatingAlarmIsSet || repeatingInterval < 0)
            return;

        Intent intent = new Intent(context, ScheduleAlarmReciver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
            context,
            10,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            repeatingInterval, pi);
        repeatingAlarmIsSet = true;
    }

    @Override
    public void setExactAlarm(long localTargetTime) {
        Intent intent = new Intent(context, ScheduleAlarmReciver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
            context,
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
